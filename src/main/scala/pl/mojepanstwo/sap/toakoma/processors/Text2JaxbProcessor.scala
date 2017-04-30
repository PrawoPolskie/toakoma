package pl.mojepanstwo.sap.toakoma.processors

import org.springframework.batch.item.ItemProcessor
import pl.mojepanstwo.sap.toakoma.xml.{AkomaNtosoType, ObjectFactory}
import pl.mojepanstwo.sap.toakoma.{IsapModel, Pdf}
import javax.xml.bind.{JAXBContext, JAXBElement, Marshaller, PropertyException}

import com.sun.xml.bind.marshaller.NamespacePrefixMapper

object Text2JaxbProcessor {
  val URI = "http://docs.oasis-open.org/legaldocml/ns/akn/3.0/WD17"
}

class Text2JaxbProcessor(pdf:Pdf.Value) extends ItemProcessor[IsapModel, JAXBElement[AkomaNtosoType]] {

  override def process(item:IsapModel): JAXBElement[AkomaNtosoType] = {
    val input = item.texts(pdf)
    val factory = new ObjectFactory()
    val xmlType = factory.createAkomaNtosoType()
    val output = factory.createAkomaNtoso(xmlType)

    val context = JAXBContext.newInstance(classOf[AkomaNtosoType])
    val marshaller = context.createMarshaller
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
    try
      marshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", new NamespacePrefixMapper {
        override def getPreferredPrefix(namespaceUri: String, suggestion: String, requirePrefix: Boolean): String = {
          if(Text2JaxbProcessor.URI.equals(namespaceUri)) return ""
          return suggestion
        }
      })
    catch {
      case e: PropertyException =>
    }
    marshaller.marshal(output, System.out)
    output
  }
}
