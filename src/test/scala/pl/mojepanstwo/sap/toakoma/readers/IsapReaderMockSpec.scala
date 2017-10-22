package pl.mojepanstwo.sap.toakoma.readers

import org.jsoup.Jsoup
import pl.mojepanstwo.sap.toakoma._

import scala.io.Source

class IsapReaderMockSpec extends UnitSpec {

  "A Mock for IsapReader" should "read html from file" in {
    val isapReaderMock = stub[IsapReader]
    (isapReaderMock.read _).when.returns(Jsoup.parse(Source.fromResource("isap/WDU20170000001.html").mkString))

    val document = isapReaderMock.read

    document.toString.replaceAll("\\s", "") should be
      Source.fromResource("isap/WDU20170000001.html").mkString.replaceAll("\\s", "")
  }

}