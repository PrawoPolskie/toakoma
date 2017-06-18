package pl.mojepanstwo.sap.toakoma.services

import java.net.URL
import java.io.File
import org.apache.commons.io.FileUtils
import org.jsoup.nodes.Document
import com.gargoylesoftware.htmlunit._
import org.jsoup.Jsoup

trait Scraper {
  def get(url: String) : Document
  def dowloadFile(fileUrl:String, filePath:String) : String
}

class DefaultScraperService extends Scraper {

  val webClient = new WebClient

  def get(url: String) : Document = {
      webClient.setRefreshHandler(new RefreshHandler {
        override def handleRefresh(page: Page, url: URL, i: Int): Unit = webClient.getPage(url)
      })
      val apPage: Page = webClient.getPage(url)
      Jsoup.parse(apPage.getWebResponse.getContentAsString)
  }

  def dowloadFile(fileUrl:String, filePath:String) : String = {
    val url = new URL(fileUrl)
    val tmp = new File(filePath)
    FileUtils.copyURLToFile(url, tmp)
    tmp.getAbsolutePath()
  }

}

