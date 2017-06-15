package pl.mojepanstwo.sap.toakoma.processors

import pl.mojepanstwo.sap.toakoma._
import org.jsoup.Jsoup
import scala.io.Source

class IsapProcessorSpec extends UnitSpec {

  "A IsapProcessor" should "parse html" in {
    val document = Jsoup.parse(Source.fromResource("isap/WDU20170000001.html").mkString)
    val model = new IsapProcessor().process(document)

    assert(model.id       == "Dz.U. 2017 poz. 1")
    assert(model.dziennik == Dziennik.DZIENNIK_USTAW)
    assert(model.year     == "2017")
  }

}