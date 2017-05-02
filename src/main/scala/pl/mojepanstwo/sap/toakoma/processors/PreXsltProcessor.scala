package pl.mojepanstwo.sap.toakoma.processors

import java.io.File
import javax.xml.transform.stream.StreamSource

import org.springframework.batch.item.ItemProcessor
import pl.mojepanstwo.sap.toakoma.IsapModel
import net.sf.saxon.s9api.{Processor, Serializer}

class PreXsltProcessor extends ItemProcessor[IsapModel, IsapModel] {

  val processor = new Processor(false)

  val compiler = processor.newXsltCompiler()

  val xsl_1 = compiler.compile(new StreamSource(classOf[PreXsltProcessor].getResourceAsStream("1.xsl"))).load

  override def process(item:IsapModel): IsapModel = {
    item.linksHtml.foreach { case (key, dirPath) =>
      val input = new File(dirPath + "/output.html")
      val source = processor.newDocumentBuilder.build(new StreamSource(input))

      val out = processor.newSerializer(new File(dirPath + "/1.xml"))
      out.setOutputProperty(Serializer.Property.METHOD, "xml")
      out.setOutputProperty(Serializer.Property.INDENT, "yes")

      xsl_1.setInitialContextNode(source)
      xsl_1.setDestination(out)
      xsl_1.transform()
    }
    item
  }

}
