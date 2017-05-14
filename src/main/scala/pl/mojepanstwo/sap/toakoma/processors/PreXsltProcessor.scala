package pl.mojepanstwo.sap.toakoma.processors

import java.io.File
import javax.xml.transform.stream.StreamSource

import org.springframework.batch.item.ItemProcessor
import pl.mojepanstwo.sap.toakoma.IsapModel
import net.sf.saxon.s9api.{Processor, Serializer}

class PreXsltProcessor extends ItemProcessor[IsapModel, IsapModel] {

  val processor = new Processor(false)

  val compiler = processor.newXsltCompiler()

  val xsl_remove_spans = compiler.compile(new StreamSource(classOf[PreXsltProcessor].getResourceAsStream("remove_spans.xsl"))).load
  val xsl_join_breaks  = compiler.compile(new StreamSource(classOf[PreXsltProcessor].getResourceAsStream("join_breaks.xsl"))).load

  override def process(item:IsapModel): IsapModel = {
    item.linksHtml.foreach { case (key, dirPath) =>
      var input = new File(dirPath + "/output.html")
      var source = processor.newDocumentBuilder.build(new StreamSource(input))

      var out = processor.newSerializer(new File(dirPath + "/after_remove_spans.xml"))
      out.setOutputProperty(Serializer.Property.METHOD, "xml")
      out.setOutputProperty(Serializer.Property.INDENT, "yes")

      xsl_remove_spans.setInitialContextNode(source)
      xsl_remove_spans.setDestination(out)
      xsl_remove_spans.transform()


      input = new File(dirPath + "/after_remove_spans.xml")
      source = processor.newDocumentBuilder.build(new StreamSource(input))

      out = processor.newSerializer(new File(dirPath + "/after_join_breaks.xml"))
      out.setOutputProperty(Serializer.Property.METHOD, "xml")
      out.setOutputProperty(Serializer.Property.INDENT, "yes")

      xsl_join_breaks.setInitialContextNode(source)
      xsl_join_breaks.setDestination(out)
      xsl_join_breaks.transform()


      item.xmlPath(key) = dirPath + "/after_join_breaks.xml"
    }
    item
  }

}
