package pl.mojepanstwo.sap.toakoma.processors

import pl.mojepanstwo.sap.toakoma.UnitSpec
import java.io._
import scala.util.Random
import scala.io.Source
import org.apache.commons.io._

class Pdf2HtmlProcessorSpec extends UnitSpec {

  "A Pdf2HtmlProcessor" should "convert pdf to html" in {
    val rS = getClass.getResourceAsStream("/isap/WDU20170000001/2.pdf")
    val in = new File(System.getProperty("java.io.tmpdir") + "/" + Random.nextString(5))
    val inS = new FileOutputStream(in)
    IOUtils.copy(rS, inS)
    inS.close()
    rS.close()
    val out = new File(System.getProperty("java.io.tmpdir") + "/" + Random.nextString(5))
    out.mkdirs()
    new Pdf2HtmlProcessor().convert(in.getAbsolutePath, out.getAbsolutePath)

    assert(Source.fromResource("isap/WDU20170000001/output.html").mkString ==
           FileUtils.readFileToString(new File(out.getAbsolutePath + "/output.html"), "UTF-8"))
  }
}