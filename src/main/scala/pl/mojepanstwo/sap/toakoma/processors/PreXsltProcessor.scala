package pl.mojepanstwo.sap.toakoma.processors

import java.io.File
import java.util.stream.StreamSupport
import javax.xml.transform.stream.StreamSource

import scala.collection.JavaConverters._
import net.sf.saxon.ma.map.{HashTrieMap, KeyValuePair}
import org.springframework.batch.item.ItemProcessor
import pl.mojepanstwo.sap.toakoma._
import net.sf.saxon.s9api._
import net.sf.saxon.value.Int64Value

class PreXsltProcessor extends ItemProcessor[Model, Model] {

  val processor = new Processor(false)

  val compiler  = processor.newXsltCompiler
  val qcompiler = processor.newXQueryCompiler()

  qcompiler.setLanguageVersion("3.1")

  val xsl_remove_spans   = compiler.compile(new StreamSource(classOf[PreXsltProcessor].getResourceAsStream("remove_spans.xsl"))).load
  val xsl_class_attrs    = compiler.compile(new StreamSource(classOf[PreXsltProcessor].getResourceAsStream("class_attrs.xsl"))).load
  val xsl_join_breaks    = compiler.compile(new StreamSource(classOf[PreXsltProcessor].getResourceAsStream("join_breaks.xsl"))).load
  val xsl_pages          = compiler.compile(new StreamSource(classOf[PreXsltProcessor].getResourceAsStream("pages.xsl"))).load
  val xsl_headers        = compiler.compile(new StreamSource(classOf[PreXsltProcessor].getResourceAsStream("headers.xsl"))).load
  val xsl_footers        = compiler.compile(new StreamSource(classOf[PreXsltProcessor].getResourceAsStream("footers.xsl"))).load
  val xsl_join_pages     = compiler.compile(new StreamSource(classOf[PreXsltProcessor].getResourceAsStream("join_pages.xsl"))).load
  val xsl_footnotes      = compiler.compile(new StreamSource(classOf[PreXsltProcessor].getResourceAsStream("footnotes.xsl"))).load
  val xsl_title          = compiler.compile(new StreamSource(classOf[PreXsltProcessor].getResourceAsStream("title.xsl"))).load
  val xsl_blocks         = compiler.compile(new StreamSource(classOf[PreXsltProcessor].getResourceAsStream("blocks.xsl"))).load
  val xsl_lines          = compiler.compile(new StreamSource(classOf[PreXsltProcessor].getResourceAsStream("lines.xsl"))).load
  val xsl_leading_spaces = compiler.compile(new StreamSource(classOf[PreXsltProcessor].getResourceAsStream("leading_spaces.xsl"))).load

  val xq_fonts = qcompiler.compile(classOf[PreXsltProcessor].getResourceAsStream("fonts.xq")).load

  override def process(item:Model): Model = {
    println("PreXsltProcessor: process : start")

    val dirPath = item.linkHtml
    var input = new File(item.xmlPath)

    input = applyXsl(input, dirPath, xsl_remove_spans, "remove_spans")
    input = applyXsl(input, dirPath, xsl_class_attrs,  "class_attrs")
    input = applyXsl(input, dirPath, xsl_pages,        "pages")
    input = applyXsl(input, dirPath, xsl_headers,      "headers")
    input = applyXsl(input, dirPath, xsl_footers,      "footers")

    var source = processor.newDocumentBuilder.build(new StreamSource(input))
    xq_fonts.setContextItem(source)
    val font_sizes = xq_fonts.evaluate.asInstanceOf[XdmFunctionItem]
    val main_font_size = StreamSupport.stream(font_sizes.getUnderlyingValue.asInstanceOf[HashTrieMap].spliterator, false)
                                      .iterator.asScala
                                      .reduceLeft((x:KeyValuePair, y:KeyValuePair) =>
                                        if(x.value.asInstanceOf[Int64Value].longValue > y.value.asInstanceOf[Int64Value].longValue) x else y)
                                      .key.toString

    xsl_footnotes.setParameter(new QName("main-font_size"), new XdmAtomicValue(main_font_size))
    xsl_footnotes.setParameter(new QName("font_sizes"), font_sizes)
    xsl_footnotes.setParameter(new QName("mode"), new XdmAtomicValue(item.pdf.toString))
    input = applyXsl(input, dirPath, xsl_footnotes,      "footnotes")
    input = applyXsl(input, dirPath, xsl_join_pages,     "join_pages")
    input = applyXsl(input, dirPath, xsl_join_breaks,    "join_breaks")
    xsl_title.setParameter(new QName("title"), new XdmAtomicValue(item.title))
    input = applyXsl(input, dirPath, xsl_title,          "title")
    xsl_blocks.setParameter(new QName("main-font_size"), new XdmAtomicValue(main_font_size))
    input = applyXsl(input, dirPath, xsl_blocks,         "blocks")
    input = applyXsl(input, dirPath, xsl_lines,          "lines")
    input = applyXsl(input, dirPath, xsl_leading_spaces, "leading_spaces")

    item.xmlPath = input.getAbsolutePath

    println("PreXsltProcessor: process : end")
    item
  }

  def applyXsl(input:File, dirPath:String, xsl:XsltTransformer, name:String) : File = {
    var source = processor.newDocumentBuilder.build(new StreamSource(input))
    var out = processor.newSerializer(new File(dirPath + "/after_" + name + ".xml"))
    out.setOutputProperty(Serializer.Property.METHOD, "xml")
    out.setOutputProperty(Serializer.Property.INDENT, "yes")

    xsl.setInitialContextNode(source)
    xsl.setDestination(out)
    xsl.transform
    new File(dirPath + "/after_" + name + ".xml")
  }
}
