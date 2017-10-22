package pl.mojepanstwo.sap.toakoma.processors

import net.sf.saxon.s9api.{QName, XdmAtomicValue, XdmValue, XsltTransformer}
import pl.mojepanstwo.sap.toakoma._

class FootnotesProcessor extends XsltProcessor {

  val xsl: XsltTransformer = getXsl("footnotes.xsl")

  override def process(item:Model): Model = {
    xsl.setParameter(new QName("main-font_size"), new XdmAtomicValue(item.mainFontSize))
    xsl.setParameter(new QName("font_sizes"), new XdmValue(item.fontSizes))
    xsl.setParameter(new QName("mode"), new XdmAtomicValue(item.pdf.toString))

    item.xmlPath = applyXsl(
      item.xmlPath,
      item.linkHtml,
      xsl,
      "footnotes"
    ).getAbsolutePath
    item
  }
}
