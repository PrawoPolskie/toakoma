package pl.mojepanstwo.sap.toakoma.processors

import net.sf.saxon.s9api.{QName, XdmAtomicValue, XsltTransformer}
import pl.mojepanstwo.sap.toakoma._

class TitleProcessor extends XsltProcessor {

  val xsl: XsltTransformer = getXsl("title.xsl")

  override def process(item:Model): Model = {
    xsl.setParameter(new QName("title"), new XdmAtomicValue(item.title))
    item.xmlPath = applyXsl(
      item.xmlPath,
      item.linkHtml,
      xsl,
      "title"
    ).getAbsolutePath
    item
  }
}
