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

  val xsl_remove_spans = compiler.compile(new StreamSource(classOf[PreXsltProcessor].getResourceAsStream("remove_spans.xsl"))).load
  val xsl_join_breaks  = compiler.compile(new StreamSource(classOf[PreXsltProcessor].getResourceAsStream("join_breaks.xsl"))).load
  val xsl_pages        = compiler.compile(new StreamSource(classOf[PreXsltProcessor].getResourceAsStream("pages.xsl"))).load
  val xsl_headers      = compiler.compile(new StreamSource(classOf[PreXsltProcessor].getResourceAsStream("headers.xsl"))).load
  val xsl_footers      = compiler.compile(new StreamSource(classOf[PreXsltProcessor].getResourceAsStream("footers.xsl"))).load
  val xsl_join_pages   = compiler.compile(new StreamSource(classOf[PreXsltProcessor].getResourceAsStream("join_pages.xsl"))).load
  val xsl_footnotes    = compiler.compile(new StreamSource(classOf[PreXsltProcessor].getResourceAsStream("footnotes.xsl"))).load
  val xsl_title        = compiler.compile(new StreamSource(classOf[PreXsltProcessor].getResourceAsStream("title.xsl"))).load
  val xsl_blocks       = compiler.compile(new StreamSource(classOf[PreXsltProcessor].getResourceAsStream("blocks.xsl"))).load

  val xq_fonts = qcompiler.compile(classOf[PreXsltProcessor].getResourceAsStream("fonts.xq")).load

  override def process(item:Model): Model = {
    item.linksHtml.foreach { case (key, dirPath) =>
      var input = new File(item.xmlPath(key))

      var source = processor.newDocumentBuilder.build(new StreamSource(input))
      var out = processor.newSerializer(new File(dirPath + "/after_remove_spans.xml"))
      out.setOutputProperty(Serializer.Property.METHOD, "xml")
      out.setOutputProperty(Serializer.Property.INDENT, "yes")

      xsl_remove_spans.setInitialContextNode(source)
      xsl_remove_spans.setDestination(out)
      xsl_remove_spans.transform()
      input = new File(dirPath + "/after_remove_spans.xml")


      source = processor.newDocumentBuilder.build(new StreamSource(input))
      out = processor.newSerializer(new File(dirPath + "/after_pages.xml"))
      out.setOutputProperty(Serializer.Property.METHOD, "xml")
      out.setOutputProperty(Serializer.Property.INDENT, "yes")

      xsl_pages.setInitialContextNode(source)
      xsl_pages.setDestination(out)
      xsl_pages.transform()
      input = new File(dirPath + "/after_pages.xml")


      source = processor.newDocumentBuilder.build(new StreamSource(input))
      out = processor.newSerializer(new File(dirPath + "/after_headers.xml"))
      out.setOutputProperty(Serializer.Property.METHOD, "xml")
      out.setOutputProperty(Serializer.Property.INDENT, "yes")

      xsl_headers.setInitialContextNode(source)
      xsl_headers.setDestination(out)
      xsl_headers.transform()
      input = new File(dirPath + "/after_headers.xml")


      source = processor.newDocumentBuilder.build(new StreamSource(input))
      out = processor.newSerializer(new File(dirPath + "/after_footers.xml"))
      out.setOutputProperty(Serializer.Property.METHOD, "xml")
      out.setOutputProperty(Serializer.Property.INDENT, "yes")

      xsl_footers.setInitialContextNode(source)
      xsl_footers.setDestination(out)
      xsl_footers.transform()
      input = new File(dirPath + "/after_footers.xml")


      source = processor.newDocumentBuilder.build(new StreamSource(input))
      xq_fonts.setContextItem(source)
      val font_sizes = xq_fonts.evaluate.asInstanceOf[XdmFunctionItem]
      val main_font_size = StreamSupport.stream(font_sizes.getUnderlyingValue.asInstanceOf[HashTrieMap].spliterator, false)
                                        .iterator.asScala
                                        .reduceLeft((x:KeyValuePair, y:KeyValuePair) =>
                                          if(x.value.asInstanceOf[Int64Value].longValue > y.value.asInstanceOf[Int64Value].longValue) x else y)
                                        .key.toString


      source = processor.newDocumentBuilder.build(new StreamSource(input))
      out = processor.newSerializer(new File(dirPath + "/after_footnotes.xml"))
      out.setOutputProperty(Serializer.Property.METHOD, "xml")
      out.setOutputProperty(Serializer.Property.INDENT, "yes")

      xsl_footnotes.setInitialContextNode(source)
      xsl_footnotes.setDestination(out)
      xsl_footnotes.setParameter(new QName("main-font_size"), new XdmAtomicValue(main_font_size))
      xsl_footnotes.setParameter(new QName("font_sizes"), font_sizes)
      xsl_footnotes.setParameter(new QName("mode"), new XdmAtomicValue(key.toString))
      xsl_footnotes.transform()
      input = new File(dirPath + "/after_footnotes.xml")


      source = processor.newDocumentBuilder.build(new StreamSource(input))
      out = processor.newSerializer(new File(dirPath + "/after_join_pages.xml"))
      out.setOutputProperty(Serializer.Property.METHOD, "xml")
      out.setOutputProperty(Serializer.Property.INDENT, "yes")

      xsl_join_pages.setInitialContextNode(source)
      xsl_join_pages.setDestination(out)
      xsl_join_pages.transform()
      input = new File(dirPath + "/after_join_pages.xml")


      source = processor.newDocumentBuilder.build(new StreamSource(input))
      out = processor.newSerializer(new File(dirPath + "/after_join_breaks.xml"))
      out.setOutputProperty(Serializer.Property.METHOD, "xml")
      out.setOutputProperty(Serializer.Property.INDENT, "yes")

      xsl_join_breaks.setInitialContextNode(source)
      xsl_join_breaks.setDestination(out)
      xsl_join_breaks.transform()
      input = new File(dirPath + "/after_join_breaks.xml")


      source = processor.newDocumentBuilder.build(new StreamSource(input))
      out = processor.newSerializer(new File(dirPath + "/after_title.xml"))
      out.setOutputProperty(Serializer.Property.METHOD, "xml")
      out.setOutputProperty(Serializer.Property.INDENT, "yes")

      xsl_title.setInitialContextNode(source)
      xsl_title.setDestination(out)
      xsl_title.setParameter(new QName("title"), new XdmAtomicValue(item.title))
      xsl_title.transform()
      input = new File(dirPath + "/after_title.xml")


      source = processor.newDocumentBuilder.build(new StreamSource(input))
      out = processor.newSerializer(new File(dirPath + "/after_blocks.xml"))
      out.setOutputProperty(Serializer.Property.METHOD, "xml")
      out.setOutputProperty(Serializer.Property.INDENT, "yes")

      xsl_blocks.setInitialContextNode(source)
      xsl_blocks.setDestination(out)
      xsl_blocks.setParameter(new QName("main-font_size"), new XdmAtomicValue(main_font_size))
      xsl_blocks.transform()
      input = new File(dirPath + "/after_blocks.xml")


      item.xmlPath(key) = input.getAbsolutePath
    }
    item
  }
}
