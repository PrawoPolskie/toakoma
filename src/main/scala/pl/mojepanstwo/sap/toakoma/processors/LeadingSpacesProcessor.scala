package pl.mojepanstwo.sap.toakoma.processors

import net.sf.saxon.s9api.XsltTransformer
import pl.mojepanstwo.sap.toakoma._

class LeadingSpacesProcessor extends XsltProcessor {

  val xsl: XsltTransformer = getXsl("leading_spaces.xsl")

  override def process(item:Model): Model = {
    item.xmlPath = applyXsl(
      item.xmlPath,
      item.linkHtml,
      xsl,
      "leading_spaces"
    ).getAbsolutePath
    item
  }
}
