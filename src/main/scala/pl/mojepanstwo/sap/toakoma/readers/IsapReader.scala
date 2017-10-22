package pl.mojepanstwo.sap.toakoma.readers

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemReader

object IsapReader {
  val BASE_URL = "http://isap.sejm.gov.pl"
  val URL      = BASE_URL + "/isap.nsf/DocDetails.xsp?id="
}

class IsapReader(val id: String) extends ItemReader[Document] {

  val logger = LoggerFactory.getLogger(this.getClass())

  var last = false

  def read : Document = {
    logger.trace("read")

    if(last) return null

    this.last = true
    Jsoup.connect(IsapReader.URL + id).get
  }
}