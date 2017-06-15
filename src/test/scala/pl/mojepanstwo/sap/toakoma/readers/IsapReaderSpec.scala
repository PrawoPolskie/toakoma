package pl.mojepanstwo.sap.toakoma.readers

import pl.mojepanstwo.sap.toakoma._
import scala.io.Source

class IsapReaderSpec extends UnitSpec {

  val regexTrim     = """(?m)\s+$"""
  val regexJsession = """;jsessionid=[A-Z|0-9]*"""

  "A IsapReader" should "read html" in {
    val reader   = new IsapReader("WDU20170000001")
    val document = reader.read
    assert(document.toString.replaceAll(regexTrim, "")
                            .replaceAll(regexJsession, "")
                            .filter(_ >= ' ') ==
           Source.fromResource("isap/WDU20170000001.html").mkString.replaceAll(regexTrim, "")
                                                                   .replaceAll(regexJsession, "")
                                                                   .filter(_ >= ' '))
  }

  it should "produce NoSuchDocumentException when id is unknown" in {
    assertThrows[NoSuchDocumentException] {
      new IsapReader("WDU30170000000").read
    }
  }
}