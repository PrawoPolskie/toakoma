package pl.mojepanstwo.sap.toakoma.processors

import java.io.File
import java.util.stream.StreamSupport
import javax.xml.transform.stream.StreamSource

import net.sf.saxon.ma.map.{HashTrieMap, KeyValuePair}
import net.sf.saxon.s9api.{XQueryEvaluator, XdmFunctionItem}
import net.sf.saxon.value.Int64Value
import pl.mojepanstwo.sap.toakoma._

import scala.collection.JavaConverters._

class GetFontsProcessor extends XsltProcessor {

  val xq_fonts: XQueryEvaluator = getQuery("fonts.xq")

  override def process(item:Model): Model = {
    val source = processor.newDocumentBuilder.build(new StreamSource(new File(item.xmlPath)))
    xq_fonts.setContextItem(source)
    item.fontSizes = xq_fonts.evaluate
    item.mainFontSize = StreamSupport.stream(item.fontSizes.asInstanceOf[XdmFunctionItem].getUnderlyingValue.asInstanceOf[HashTrieMap].spliterator, false)
      .iterator.asScala
      .reduceLeft((x:KeyValuePair, y:KeyValuePair) =>
        if(x.value.asInstanceOf[Int64Value].longValue > y.value.asInstanceOf[Int64Value].longValue) x else y)
      .key.toString
    item
  }
}
