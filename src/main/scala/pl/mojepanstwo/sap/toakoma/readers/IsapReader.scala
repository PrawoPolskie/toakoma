package pl.mojepanstwo.sap.toakoma.readers

import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemReader
import pl.mojepanstwo.sap.toakoma.xml.AkomaNtosoType
import org.springframework.context.annotation.Scope
import org.springframework.beans.factory.annotation.Value
import org.springframework.batch.core.configuration.annotation.StepScope
import scala.annotation.meta.getter
import org.springframework.context.annotation.Bean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.context.annotation.ComponentScan
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.cos.COSDocument
import java.io.File
import java.io.FileInputStream
import org.apache.pdfbox.pdfparser.PDFParser
import org.apache.pdfbox.io.RandomAccessFile
import java.io.IOException
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import org.apache.commons.io.FileUtils
import java.net.URL
import org.jsoup.nodes.Document

object IsapReader {
  val BASE_URL                   = "http://isap.sejm.gov.pl"
  val URL                        = BASE_URL + "/DetailsServlet?id="
  val TITLE_CLASS                = "cel_p"
  val STATUS_TH                  = "Status aktu prawnego:"
  val TEKST_AKTU_TH              = "Tekst aktu:"
  val TEKST_OGLOSZONY_TH         = "Tekst ogłoszony:"
  val TEKST_UJEDNOLICONY_TH      = "Tekst ujednolicony:"
  val DATA_OGLOSZENIA_TH         = "Data ogłoszenia:"
  val DATA_WYDANIA_TH            = "Data wydania:"
  val DATA_WEJSCIA_W_ZYCIE_TH    = "Data wejścia w życie:"
  val DATA_WYGASNIECIA_TH        = "Data wygaśnięcia:"
  val ORGAN_WYDAJACY_TH          = "Organ wydający:"
  val ORGAN_ZOBOZWIAZANY_TH      = "Organ zobowiązany:"
  val LINK_TEKST_AKTU_TH         = "Tekst aktu:"
  val LINK_TEKST_OGLOSZONY_TH    = "Tekst ogłoszony:"
  val LINK_TEKST_UJEDNOLICONY_TH = "Tekst ujednolicony:"

  val dateParser = new SimpleDateFormat("yyyy-MM-dd")
}

class IsapReader(val id: String) extends ItemReader[IsapModel] {

  val logger = LoggerFactory.getLogger(this.getClass())

  var executed = false

  def read() : IsapModel = {
    if(executed) return null

    val output = new IsapModel()
    val isapUrl = IsapReader.URL + id
    val doc = Jsoup.connect(isapUrl).get()

    // ID
    val isapId = doc.getElementsByClass("h1").text()
    output.id = isapId
    val idSplit: Array[String] = isapId.split(" ")

    // DZIENNIK
    output.dziennik = Dziennik.withName(idSplit.head)

    // ROK
    output.year = idSplit(1)

    // NUMER POZYCJA
    if(idSplit(2) == "nr") {
      output.number = idSplit(3)
      output.position = idSplit(5)
    } else output.position = idSplit(3)

    // ISAP_URL
    output.isapUrl = isapUrl

    // TITLE
    output.title = doc.getElementsByClass(IsapReader.TITLE_CLASS).text()

    // LINK TEKST AKTU
    output.linkTekstAktu = downloadPdf(doc, IsapReader.LINK_TEKST_AKTU_TH)

    // LINK TEKST OGLOSZONY
    output.linkTekstOgloszony = downloadPdf(doc, IsapReader.LINK_TEKST_OGLOSZONY_TH)

    // LINK TEKST UJEDNOLICONY
    output.linkTekstUjednolicony = downloadPdf(doc, IsapReader.LINK_TEKST_UJEDNOLICONY_TH)

    // STATUS
    var els = doc.select(f"th:contains(${IsapReader.STATUS_TH})")
    if(els.size() > 0)
      output.statusAktuPrawnego = StatusAktuPrawnego.withName(els.get(0).siblingElements().first().text())

    // DATA_OGLOSZENIA
    els = doc.select(f"th:contains(${IsapReader.DATA_OGLOSZENIA_TH})")
    if(els.size() > 0)
      output.dataOgloszenia = IsapReader.dateParser.parse(els.get(0).siblingElements().first().text())

    // DATA_WYDANIA
    els = doc.select(f"th:contains(${IsapReader.DATA_WYDANIA_TH})")
    if(els.size() > 0)
      output.dataWydania = IsapReader.dateParser.parse(els.get(0).siblingElements().first().text())

    // DATA_WEJSCIA_W_ZYCIE
    els = doc.select(f"th:contains(${IsapReader.DATA_WEJSCIA_W_ZYCIE_TH})")
    if(els.size() > 0)
      output.dataWejsciaWZycie = IsapReader.dateParser.parse(els.get(0).siblingElements().first().text())

    // DATA_WYGASNIECIA
    els = doc.select(f"th:contains(${IsapReader.DATA_WYGASNIECIA_TH})")
    if(els.size() > 0)
      output.dataWygasniecia = IsapReader.dateParser.parse(els.get(0).siblingElements().first().text())

    // ORGAN_WYDAJACY
    els = doc.select(f"th:contains(${IsapReader.ORGAN_WYDAJACY_TH})")
    if(els.size() > 0)
      output.organWydajacy = Organ.withName(els.get(0).siblingElements().first().text())

    // ORGAN_ZOBOWIAZANY
    els = doc.select(f"th:contains(${IsapReader.ORGAN_ZOBOZWIAZANY_TH})")
    if(els.size() > 0)
      output.organZobowiązany = Organ.withName(els.get(0).siblingElements().first().text())

    // AKTY_POWIAZANE


    this.executed = true

    output
  }

  def downloadPdf(doc: Document, th: String) : String = {
    var els = doc.select(f"th:contains(${th})")
    if(els.size() > 0) {
      var path = els.get(0).siblingElements().first().getElementsByTag("a").attr("href")
      var fileName = els.get(0).siblingElements().first().text()
      var url = new URL(IsapReader.BASE_URL + path)
      var tmp = new File(System.getProperty("java.io.tmpdir") + "/" + fileName)
      FileUtils.copyURLToFile(url, tmp)
      return tmp.getAbsolutePath()
    }
    return null
  }
}