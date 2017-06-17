package pl.mojepanstwo.sap.toakoma.processors

import pl.mojepanstwo.sap.toakoma._
import org.jsoup.Jsoup
import scala.io.Source
import java.util.Date
import java.text.SimpleDateFormat

class IsapProcessorSpec extends UnitSpec {

  "A IsapProcessor" should "parse html" in {
    val document = Jsoup.parse(Source.fromResource("isap/WDU20170000001.html").mkString)
    val model = new IsapProcessor().process(document)

    assert(model.id            == "Dz.U. 2017 poz. 1")
    assert(model.dziennik.name == "DZIENNIK_USTAW")
    assert(model.year          == "2017")
    assert(model.dataOgloszenia.compareTo(new SimpleDateFormat("yyyy-MM-dd").parse("2017-01-02")) == 0)
//    assert(model.aktyPowiazane.keySet == null)
    assert(model.dataUchylenia == null)
    assert(model.dataWejsciaWZycie.compareTo(new SimpleDateFormat("yyyy-MM-dd").parse("2017-01-03")) == 0)
//    println(model.dataWydania)
//    println(model.dataWygasniecia)
//    println(model.encrypted)
//    println(model.fontSizes)
//    println(model.isapUrl)
//    println(model.linksHtml)
//    println(model.linksPdf)
//    println(model.number)
//    println(model.organUprawniony)
//    println(model.organWydajacy)
//    println(model.organZobowiazany)
//    println(model.position)
//    println(model.statusAktuPrawnego)
//    println(model.texts)
//    println(model.title)
//    println(model.uwagi)
//    println(model.xmlPath)
  }

}