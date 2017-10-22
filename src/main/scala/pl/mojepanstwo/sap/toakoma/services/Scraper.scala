package pl.mojepanstwo.sap.toakoma.services

import java.io.File
import java.net.URL

import com.gargoylesoftware.htmlunit._
import org.apache.commons.io.FileUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

trait Scraper {
  def get(url: String) : Document
  def downloadFile(fileUrl:String, filePath:String) : String
}

class DefaultScraperService extends Scraper {

  val webClient = new WebClient

  def get(url: String) : Document = {
      webClient.setRefreshHandler((_, url: URL, _) => webClient.getPage(url))
      val apPage: Page = webClient.getPage(url)
      Jsoup.parse(apPage.getWebResponse.getContentAsString)
  }

  def downloadFile(fileUrl:String, filePath:String) : String = {
    val url = new URL(fileUrl)
    val tmp = new File(filePath)
    FileUtils.copyURLToFile(url, tmp)
    tmp.getAbsolutePath
  }

}

