package pl.mojepanstwo.sap.toakoma.processors

import net.sf.saxon.s9api.XsltTransformer
import pl.mojepanstwo.sap.toakoma._

class ClassAttrsProcessor extends XsltProcessor {

  val xsl: XsltTransformer = getXsl("class_attrs.xsl")

  override def process(item:Model): Model = {
    item.xmlPath = applyXsl(
      item.xmlPath,
      item.linkHtml,
      xsl,
      "class_attrs"
    ).getAbsolutePath
    item
  }
}
