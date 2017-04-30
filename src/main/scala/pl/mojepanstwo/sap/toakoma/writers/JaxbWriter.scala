package pl.mojepanstwo.sap.toakoma.writers

import javax.xml.bind.{JAXBContext, JAXBElement, Marshaller, PropertyException}

import com.sun.xml.bind.marshaller.NamespacePrefixMapper
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemWriter
import pl.mojepanstwo.sap.toakoma.xml.AkomaNtosoType

object JaxbWriter {
  val URI = "http://docs.oasis-open.org/legaldocml/ns/akn/3.0/WD17"
}

class JaxbWriter extends ItemWriter[JAXBElement[AkomaNtosoType]] {
  
  val logger = LoggerFactory.getLogger(this.getClass())
  
  @Override
	def write(items: java.util.List[_ <: JAXBElement[AkomaNtosoType]] ) = {
    val context = JAXBContext.newInstance(classOf[AkomaNtosoType])
    val marshaller = context.createMarshaller
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
    try
      marshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", new NamespacePrefixMapper {
        override def getPreferredPrefix(namespaceUri: String, suggestion: String, requirePrefix: Boolean): String = {
          if(JaxbWriter.URI.equals(namespaceUri)) return ""
          return suggestion
        }
      })
    catch {
      case e: PropertyException =>
    }
    marshaller.marshal(items.get(0), System.out)
  }
}