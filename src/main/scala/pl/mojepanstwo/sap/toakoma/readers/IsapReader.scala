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

    this.last = true
    val isapUrl = IsapReader.URL + id
    val rsp = Jsoup.connect(isapUrl).get
    if(rsp.body.text.contains("Brak aktu prawnego o podanym adresie publikacyjnym !"))
      throw new NoSuchDocumentException
    return rsp
  }
}