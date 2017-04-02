package pl.mojepanstwo.sap.toakoma.readers

import org.scalatest._

class IsapReaderSpec extends FlatSpec {

  "A IsapReader" should "download html to tmp dir" in {
    val reader = new IsapReader("WDU20150002058")
    reader.read()
  }

}