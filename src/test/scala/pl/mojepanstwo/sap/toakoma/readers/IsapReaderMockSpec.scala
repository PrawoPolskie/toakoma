package pl.mojepanstwo.sap.toakoma.readers

import pl.mojepanstwo.sap.toakoma._
import scala.io.Source
import org.jsoup.Jsoup

class IsapReaderMockSpec extends IsapMockSpec {

  val regexTrim = """(?m)\s+$"""

  "A Mock for IsapReader" should "read html from file" in {
    val isapReaderMock = stub[IsapReader]
    (isapReaderMock.read _).when
                           .returns(Jsoup.parse(Source.fromResource("isap/WDU20170000001.html").mkString))

    val document = isapReaderMock.read

    assert(document.toString.replaceAll(regexTrim, "").filter(_ >= ' ') ==
           Source.fromResource("isap/WDU20170000001.html").mkString.replaceAll(regexTrim, "").filter(_ >= ' '))
  }

}