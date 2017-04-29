package pl.mojepanstwo.sap.toakoma.readers

import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemReader

import org.jsoup.Jsoup

import org.jsoup.nodes.Document
import com.gargoylesoftware.htmlunit.WebClient
import pl.mojepanstwo.sap.toakoma._

object IsapReader {
  val BASE_URL = "http://isap.sejm.gov.pl"
  val URL      = BASE_URL + "/DetailsServlet?id="
}

class IsapReader(val id: String) extends ItemReader[Document] {

  val logger = LoggerFactory.getLogger(this.getClass())

  var last = false

  def read : Document = {
    logger.trace("read")

    if(last) return null

    if(id == Cli.ID_ALL) {
      return null
    } else {
      this.last = true
      val isapUrl = IsapReader.URL + id
      return Jsoup.connect(isapUrl).get()
    }
  }
}