package pl.mojepanstwo.sap.toakoma.processors

import net.sf.saxon.s9api.XsltTransformer
import pl.mojepanstwo.sap.toakoma._

class HeadersProcessor extends XsltProcessor {

  val xsl: XsltTransformer = getXsl("headers.xsl")

  override def process(item:Model): Model = {
    item.xmlPath = applyXsl(
      item.xmlPath,
      item.linkHtml,
      xsl,
      "headers"
    ).getAbsolutePath
    item
  }
}
