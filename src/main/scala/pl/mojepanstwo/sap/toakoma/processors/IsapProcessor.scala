package pl.mojepanstwo.sap.toakoma.processors

import java.text.SimpleDateFormat

import org.jsoup.nodes.Document
import org.springframework.batch.item.ItemProcessor
import pl.mojepanstwo.sap.toakoma._
import pl.mojepanstwo.sap.toakoma.readers.IsapReader
import pl.mojepanstwo.sap.toakoma.services.Scraper

import scala.collection.mutable.ArraySeq

object IsapProcessor {
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
  val AKTY_POWIAZANE_CLASS       = "doc-references"
  val AKT_POWIAZANY_CLASS        = "cel_p"

  val dateParser = new SimpleDateFormat("yyyy-MM-dd")
}

class IsapProcessor(scraper:Scraper) extends ItemProcessor[Document, Model] {

  override def process(item:Document): Model = {
    val output = new Model
    try {

      // ID
      val isapId = item.getElementsByTag("h1").text
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
      output.isapUrl = item.location

      // TITLE
      output.title = item.getElementsByTag("h2").text

      // LINKS
      output.linksPdf += (Pdf.TEKST_AKTU         -> downloadPdf(item, IsapProcessor.LINK_TEKST_AKTU_TH))
      output.linksPdf += (Pdf.TEKST_OGLOSZONY    -> downloadPdf(item, IsapProcessor.LINK_TEKST_OGLOSZONY_TH))
      output.linksPdf += (Pdf.TEKST_UJEDNOLICONY -> downloadPdf(item, IsapProcessor.LINK_TEKST_UJEDNOLICONY_TH))

      output.pdf = if (output.linksPdf.keySet.exists(_ == Pdf.TEKST_AKTU)) Pdf.TEKST_AKTU else Pdf.TEKST_OGLOSZONY

      // STATUS
      var els = item.select(f"div:containsOwn(${IsapProcessor.STATUS_TH})")
      if (els.size() > 0)
        output.statusAktuPrawnego = StatusAktuPrawnego.get("isap", els.get(0).siblingElements().first().text())

      // DATA_OGLOSZENIA
      els = item.select(f"div:containsOwn(${IsapProcessor.DATA_OGLOSZENIA_TH})")
      if (els.size() > 0)
        output.dataOgloszenia = IsapProcessor.dateParser.parse(els.get(0).siblingElements().first().text())

      // DATA_WYDANIA
      els = item.select(f"div:containsOwn(${IsapProcessor.DATA_WYDANIA_TH})")
      if (els.size() > 0)
        output.dataWydania = IsapProcessor.dateParser.parse(els.get(0).siblingElements().first().text())

      // DATA_WEJSCIA_W_ZYCIE
      els = item.select(f"div:containsOwn(${IsapProcessor.DATA_WEJSCIA_W_ZYCIE_TH})")
      if (els.size() > 0)
        output.dataWejsciaWZycie = IsapProcessor.dateParser.parse(els.get(0).siblingElements().first().text())
      println(output.dataWejsciaWZycie)

      // DATA_WYGASNIECIA
      els = item.select(f"div:containsOwn(${IsapProcessor.DATA_WYGASNIECIA_TH})")
      if (els.size() > 0)
        output.dataWygasniecia = IsapProcessor.dateParser.parse(els.get(0).siblingElements().first().text())

      // DATA UCHYLENIA
      els = item.select(f"div:containsOwn(${IsapProcessor.DATA_UCHYLENIA_TH})")
      if (els.size() > 0)
        output.dataUchylenia = IsapProcessor.dateParser.parse(els.get(0).siblingElements().first().text())

      // UWAGI
      els = item.select(f"div:containsOwn(${IsapProcessor.UWAGI_TH})")
      if (els.size() > 0)
        output.uwagi = els.get(0).siblingElements.first.text

      // ORGAN_WYDAJACY
      els = item.select(f"div:containsOwn(${IsapProcessor.ORGAN_WYDAJACY_TH})")
      if (els.size() > 0) {
        val o = els.get(0).siblingElements.first
        o.childNodes().stream().filter(n => n.toString != "<br>" && n.toString != "")
                               .forEach(n => output.organWydajacy :+= Organ.get("isap", n.toString.trim()))
      }

      // ORGAN_ZOBOWIAZANY
      els = item.select(f"div:containsOwn(${IsapProcessor.ORGAN_ZOBOZWIAZANY_TH})")
      if (els.size() > 0) {
        val ou = els.get(0).siblingElements.first
        ou.childNodes().stream().filter(n => n.toString != "<br>" && n.toString != "")
                                .forEach(n => output.organZobowiazany :+= Organ.get("isap", n.toString.trim()))
      }

      // ORGAN_UPRAWNIONY
      els = item.select(f"div:containsOwn(${IsapProcessor.ORGAN_UPRAWNIONY_TH})")
      if (els.size() > 0) {
        val ou = els.get(0).siblingElements().first()
        ou.childNodes().stream().filter(n => n.toString != "<br>" && n.toString != "")
                                .forEach(n => output.organUprawniony :+= Organ.get("isap", n.toString.trim()))
      }

      // AKTY_POWIAZANE
      val aps = item.getElementById("accordion")
      aps.getElementsByClass("panel").forEach { apGroup =>
        var apa: ArraySeq[AktPowiazany] = ArraySeq()
        val title = apGroup.getElementsByTag("h3").text.split(" \\(")(0)

        val typ = AktPowiazanyTyp.get("isap", title)

        if(typ.name == "DYREKTYWY_EUROPEJSKIE") {
          apGroup.getElementsByTag("tr").forEach { ap =>
            val tds = ap.parent().parent().getElementsByTag("td")
            if(tds.size() > 0) {
              val apo = new AktPowiazany()
              apo.dyrektywa = tds.get(0).text
              apo.data = IsapProcessor.dateParser.parse(tds.get(1).text())
              apo.eurlex = tds.get(3).getElementsByTag("a").attr("href")
                              .replace("javascript: newDyrektywy('", "")
                              .replace(":PL:NOT');", "")
              apa = apa :+ apo
            }
          }
        } else {
          apGroup.getElementsByTag("tr").forEach { ap =>
            val tds = ap.parent().parent().getElementsByTag("td")
            if(tds.size > 0) {
              val apo = new AktPowiazany()
              apo.tytul = tds.get(2).text
              apo.status = StatusAktuPrawnego.get("isap", tds.get(1).text())
              apo.adres_publikacyjny = tds.get(0).getElementsByTag("a").get(0).text()
              apo.id = tds.get(0).getElementsByTag("a").attr("href").split("id=")(1).replaceAll("\\+", " +").split(" ")(0)
              apa = apa :+ apo
            }
          }
        }

        output.aktyPowiazane(typ) = apa
      }
    } catch {
      case e: Exception => println("exception caught: " + e);
      e.printStackTrace()
    }
    output
  }

  def downloadPdf(doc: Document, div: String) : Option[String] = {
    val els = doc.select(f"div:containsOwn(${div})")
    if(els.size() > 0) {
      val path = els.get(0).siblingElements().first().getElementsByTag("a").attr("href")
      val fileName = els.get(0).siblingElements().first().text()
      return new Some(scraper.downloadFile(IsapReader.BASE_URL + path, System.getProperty("java.io.tmpdir") + "/" + fileName))
    }
    return None
  }
}
