package pl.mojepanstwo.sap.toakoma.processors

import net.sf.saxon.s9api.XsltTransformer
import pl.mojepanstwo.sap.toakoma._

class RemoveSpansProcessor extends XsltProcessor {

  val xsl: XsltTransformer = getXsl("remove_spans.xsl")

  override def process(item:Model): Model = {
    item.xmlPath = applyXsl(
      item.xmlPath,
      item.linkHtml,
      xsl,
      "remove_spans"
    ).getAbsolutePath
    item
  }
}
