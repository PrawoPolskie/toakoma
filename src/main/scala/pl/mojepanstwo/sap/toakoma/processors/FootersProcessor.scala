package pl.mojepanstwo.sap.toakoma.processors

import net.sf.saxon.s9api.XsltTransformer
import pl.mojepanstwo.sap.toakoma._

class FootersProcessor extends XsltProcessor {

  val xsl: XsltTransformer = getXsl("footers.xsl")

  override def process(item:Model): Model = {
    item.xmlPath = applyXsl(
      item.xmlPath,
      item.linkHtml,
      xsl,
      "footers"
    ).getAbsolutePath
    item
  }
}
