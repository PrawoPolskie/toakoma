package pl.mojepanstwo.sap.toakoma.processors

import net.sf.saxon.s9api.{QName, XdmAtomicValue, XsltTransformer}
import pl.mojepanstwo.sap.toakoma._

class LinesProcessor extends XsltProcessor {

  val xsl: XsltTransformer = getXsl("lines.xsl")

  override def process(item:Model): Model = {
    item.xmlPath = applyXsl(
      item.xmlPath,
      item.linkHtml,
      xsl,
      "lines"
    ).getAbsolutePath
    item
  }
}
