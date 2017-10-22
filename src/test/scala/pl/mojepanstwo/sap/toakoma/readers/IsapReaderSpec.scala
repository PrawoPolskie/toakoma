package pl.mojepanstwo.sap.toakoma.readers

import org.jsoup._
import pl.mojepanstwo.sap.toakoma._

import scala.io.Source

class IsapReaderSpec extends UnitSpec {

  val replaceSession = """SessionID=.*\""""
  val replaceValue = """value=\".*\""""

  "A IsapReader" should "read html" in {
    val reader   = new IsapReader("WDU20170000001")
    val document = reader.read
    document.toString.replaceAll(replaceSession, "")
                     .replaceAll(replaceValue, "")
                     .replaceAll("\\s", "") should be
      Source.fromResource("isap/WDU20170000001.html").mkString.replaceAll(replaceSession, "")
                                                              .replaceAll(replaceValue, "")
                                                              .replaceAll("\\s", "")
  }

  it should "produce HttpStatusException when id is unknown" in {
    an [HttpStatusException] should be thrownBy new IsapReader("WDU20170000000").read
  }
}