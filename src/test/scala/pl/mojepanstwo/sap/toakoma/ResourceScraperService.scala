package pl.mojepanstwo.sap.toakoma

import pl.mojepanstwo.sap.toakoma.services.Scraper
import org.jsoup.nodes.Document
import org.jsoup.Jsoup
import scala.io.Source
import java.io.File
import java.nio.file.Files
import org.apache.commons.io.IOUtils
import java.io.FileOutputStream

class ResourceScraperService extends Scraper {

  def get(url: String) : Document = {
    val pattern = ".*id=(.*)&type=([0-9]+).*".r
    val pattern(id, docType) = url
    Jsoup.parse(Source.fromResource("isap/" + id + "/" + docType + ".html").mkString)
  }

  def dowloadFile(fileUrl:String, filePath:String) : String = {
    val pattern = ".*id=(.*)&type=([0-9]+).*".r
    val pattern(id, docType) = fileUrl
    val src = getClass.getResourceAsStream("/isap/" + id + "/" + docType + ".pdf")
    val dest = new File(filePath)
    val out = new FileOutputStream(dest)
    IOUtils.copy(src, out)
    src.close()
    out.close()
    dest.getAbsolutePath
  }

}
