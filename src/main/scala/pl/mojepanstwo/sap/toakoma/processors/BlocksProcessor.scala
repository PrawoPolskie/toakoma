package pl.mojepanstwo.sap.toakoma.processors

import net.sf.saxon.s9api.{QName, XdmAtomicValue, XsltTransformer}
import pl.mojepanstwo.sap.toakoma._

class BlocksProcessor extends XsltProcessor {

  val xsl: XsltTransformer = getXsl("blocks.xsl")

  override def process(item:Model): Model = {
    xsl.setParameter(new QName("main-font_size"), new XdmAtomicValue(item.mainFontSize))
    item.xmlPath = applyXsl(
      item.xmlPath,
      item.linkHtml,
      xsl,
      "blocks"
    ).getAbsolutePath
    item
  }
}
