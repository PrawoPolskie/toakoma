package pl.mojepanstwo.sap.toakoma.processors

import net.sf.saxon.s9api.XsltTransformer
import pl.mojepanstwo.sap.toakoma._

class JoinBreaksProcessor extends XsltProcessor {

  val xsl: XsltTransformer = getXsl("join_breaks.xsl")

  override def process(item:Model): Model = {
    item.xmlPath = applyXsl(
      item.xmlPath,
      item.linkHtml,
      xsl,
      "join_breaks"
    ).getAbsolutePath
    item
  }
}
