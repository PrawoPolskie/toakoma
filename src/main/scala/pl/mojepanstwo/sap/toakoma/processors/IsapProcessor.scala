package pl.mojepanstwo.sap.toakoma.processors

import java.io.File
import java.net.URL
import java.text.SimpleDateFormat

import com.gargoylesoftware.htmlunit.{Page, RefreshHandler, WebClient}
import org.apache.commons.io.FileUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.springframework.batch.item.ItemProcessor
import pl.mojepanstwo.sap.toakoma.readers.IsapReader
import pl.mojepanstwo.sap.toakoma._

import scala.collection.mutable.ArraySeq
import pl.mojepanstwo.sap.toakoma.services.Scraper

object IsapProcessor {
  val TITLE_CLASS                = "cel_p"
  val STATUS_TH                  = "Status aktu prawnego:"
  val TEKST_AKTU_TH              = "Tekst aktu:"
  val TEKST_OGLOSZONY_TH         = "Tekst ogłoszony:"
  val TEKST_UJEDNOLICONY_TH      = "Tekst ujednolicony:"
  val DATA_OGLOSZENIA_TH         = "Data ogłoszenia:"
  val DATA_WYDANIA_TH            = "Data wydania:"
  val DATA_WEJSCIA_W_ZYCIE_TH    = "Data wejścia w życie:"
  val DATA_WYGASNIECIA_TH        = "Data wygaśnięcia:"
  val DATA_UCHYLENIA_TH          = "Data uchylenia:"
  val ORGAN_WYDAJACY_TH          = "Organ wydający:"
  val ORGAN_ZOBOZWIAZANY_TH      = "Organ zobowiązany:"
  val ORGAN_UPRAWNIONY_TH        = "Organ uprawniony:"
  val UWAGI_TH                   = "Uwagi:"
  val LINK_TEKST_AKTU_TH         = "Tekst aktu:"
  val LINK_TEKST_OGLOSZONY_TH    = "Tekst ogłoszony:"
  val LINK_TEKST_UJEDNOLICONY_TH = "Tekst ujednolicony:"
  val AKTY_POWIAZANE_CLASS       = "cel_selektor"
  val AKT_POWIAZANY_CLASS        = "cel_p"

  val dateParser = new SimpleDateFormat("yyyy-MM-dd")
}

class IsapProcessor(scraper:Scraper) extends ItemProcessor[Document, Model] {

  override def process(item:Document): Model = {
    val output = new Model

    // ID
    val isapId = item.getElementsByClass("h1").text()
    output.id = isapId
    val idSplit: Array[String] = isapId.split(" ")

    // DZIENNIK
    output.dziennik = Dziennik.get("isap", idSplit.head)

    // ROK
    output.year = idSplit(1)

    // NUMER POZYCJA
    if (idSplit(2) == "nr") {
      output.number = idSplit(3)
      output.position = idSplit(5)
    } else output.position = idSplit(3)

    // ISAP_URL
    output.isapUrl = item.location()

    // TITLE
    output.title = item.getElementsByClass(IsapProcessor.TITLE_CLASS).text()

    // LINKS
    output.linksPdf += (Pdf.TEKST_AKTU -> downloadPdf(item, IsapProcessor.LINK_TEKST_AKTU_TH))
    output.linksPdf += (Pdf.TEKST_OGLOSZONY -> downloadPdf(item, IsapProcessor.LINK_TEKST_OGLOSZONY_TH))
    output.linksPdf += (Pdf.TEKST_UJEDNOLICONY -> downloadPdf(item, IsapProcessor.LINK_TEKST_UJEDNOLICONY_TH))

    // STATUS
    var els = item.select(f"th:contains(${IsapProcessor.STATUS_TH})")
    if (els.size() > 0)
      output.statusAktuPrawnego = StatusAktuPrawnego.get("isap", els.get(0).siblingElements().first().text())

    // DATA_OGLOSZENIA
    els = item.select(f"th:contains(${IsapProcessor.DATA_OGLOSZENIA_TH})")
    if (els.size() > 0)
      output.dataOgloszenia = IsapProcessor.dateParser.parse(els.get(0).siblingElements().first().text())

    // DATA_WYDANIA
    els = item.select(f"th:contains(${IsapProcessor.DATA_WYDANIA_TH})")
    if (els.size() > 0)
      output.dataWydania = IsapProcessor.dateParser.parse(els.get(0).siblingElements().first().text())

    // DATA_WEJSCIA_W_ZYCIE
    els = item.select(f"th:contains(${IsapProcessor.DATA_WEJSCIA_W_ZYCIE_TH})")
    if (els.size() > 0)
      output.dataWejsciaWZycie = IsapProcessor.dateParser.parse(els.get(0).siblingElements().first().text())

    // DATA_WYGASNIECIA
    els = item.select(f"th:contains(${IsapProcessor.DATA_WYGASNIECIA_TH})")
    if (els.size() > 0)
      output.dataWygasniecia = IsapProcessor.dateParser.parse(els.get(0).siblingElements().first().text())

    // DATA UCHYLENIA
    els = item.select(f"th:contains(${IsapProcessor.DATA_UCHYLENIA_TH})")
    if (els.size() > 0)
      output.dataUchylenia = IsapProcessor.dateParser.parse(els.get(0).siblingElements().first().text())

    // UWAGI
    els = item.select(f"th:contains(${IsapProcessor.UWAGI_TH})")
    if (els.size() > 0)
      output.uwagi = els.get(0).siblingElements().first().text()

    // ORGAN_WYDAJACY
    els = item.select(f"th:contains(${IsapProcessor.ORGAN_WYDAJACY_TH})")
    if (els.size() > 0) {
      val ou = els.get(0).siblingElements().first()
      ou.childNodes().stream().filter(n => n.toString != "<br>")
                              .forEach(n => output.organWydajacy :+= Organ.get("isap", n.toString))
    }

    // ORGAN_ZOBOWIAZANY
    els = item.select(f"th:contains(${IsapProcessor.ORGAN_ZOBOZWIAZANY_TH})")
    if (els.size() > 0) {
      val ou = els.get(0).siblingElements().first()
      ou.childNodes().stream().filter(n => n.toString != "<br>")
                              .forEach(n => output.organZobowiazany :+= Organ.get("isap", n.toString))
    }

    // ORGAN_UPRAWNIONY
    els = item.select(f"th:contains(${IsapProcessor.ORGAN_UPRAWNIONY_TH})")
    if (els.size() > 0) {
      val ou = els.get(0).siblingElements().first()
      ou.childNodes().stream().filter(n => n.toString != "<br>")
                              .forEach(n => output.organUprawniony :+= Organ.get("isap", n.toString))
    }

    // AKTY_POWIAZANE
    val aps = item.getElementsByClass(IsapProcessor.AKTY_POWIAZANE_CLASS).stream().map[Element](el => el.parent())
    aps.forEach { apGroup =>
      var apa: ArraySeq[AktPowiazany] = ArraySeq()
      val href = apGroup.attr("onclick").split("'")(1)

      val typ = AktPowiazanyTyp.get("isap", apGroup.text())

      val apJsoup = scraper.get(IsapReader.BASE_URL + href)
      val aps = apJsoup.getElementsByClass(IsapProcessor.AKT_POWIAZANY_CLASS).stream()
      if(typ.name == "DYREKTYWY_EUROPEJSKIE") {
        aps.forEach { ap =>
          val apo = new AktPowiazany()
          apo.tytul = ap.text
          val tds = ap.parent.parent.getElementsByTag("td")

          apo.dyrektywa = tds.get(0).text()
          apo.data = IsapProcessor.dateParser.parse(tds.get(1).text())
          apo.eurlex = tds.get(3).getElementsByTag("a").attr("href")
                          .replace("javascript: newDyrektywy('", "")
                          .replace(":PL:NOT');", "")

          apa = apa :+ apo
        }
      } else {
        aps.forEach { ap =>
          val apo = new AktPowiazany()
          apo.tytul = ap.text()
          val tds = ap.parent().parent().getElementsByTag("td")
          apo.status = StatusAktuPrawnego.get("isap", tds.get(1).text())
          apo.adres_publikacyjny = tds.get(0).getElementsByTag("a").get(0).text()
          apo.id = tds.get(0).getElementsByTag("a").attr("href").split("id=")(1).replaceAll("\\+", " +").split(" ")(0)
          apa = apa :+ apo
        }
      }

      output.aktyPowiazane(typ) = apa
    }

    output
  }

  def downloadPdf(doc: Document, th: String) : Option[String] = {
    val els = doc.select(f"th:contains(${th})")
    if(els.size() > 0) {
      val path = els.get(0).siblingElements().first().getElementsByTag("a").attr("href")
      val fileName = els.get(0).siblingElements().first().text().substring(1)
      return new Some(scraper.dowloadFile(IsapReader.BASE_URL + path, System.getProperty("java.io.tmpdir") + "/" + fileName))
    }
    return None
  }
}
