package pl.mojepanstwo.sap.toakoma

import java.io.{File, FileOutputStream}

import org.apache.commons.io.IOUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pl.mojepanstwo.sap.toakoma.services.Scraper

import scala.io.Source

class ResourceScraperService extends Scraper {

  def get(url: String) : Document = {
    val pattern = ".*id=(.*)&type=([0-9]+).*".r
    val pattern(id, docType) = url
    Jsoup.parse(Source.fromResource("isap/" + id + "/" + docType + ".html").mkString)
  }

  def downloadFile(fileUrl:String, filePath:String) : String = {
    val pattern = ".*id=(.*)&type=([0-9]+).*".r
    val pattern(id, docType) = fileUrl
    val src = getClass.getResourceAsStream("/isap/" + id + "/" + docType + ".pdf")
    val dest = new File(filePath)
    val out = new FileOutputStream(dest)
    IOUtils.copy(src, out)
    src.close
    out.close
    dest.getAbsolutePath
  }

}
