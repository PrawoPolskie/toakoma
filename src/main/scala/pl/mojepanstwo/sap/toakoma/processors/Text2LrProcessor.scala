package pl.mojepanstwo.sap.toakoma.processors

import org.springframework.batch.item.ItemProcessor
import pl.mojepanstwo.sap.toakoma.xml.{AkomaNtosoType, ObjectFactory}
import pl.mojepanstwo.sap.toakoma.{IsapModel, Pdf}
import javax.xml.bind.{JAXBContext, Marshaller}

class Text2LrProcessor(pdf:Pdf.Value) extends ItemProcessor[IsapModel, IsapModel] {

  override def process(item:IsapModel): IsapModel = {
    val input = item.texts(pdf)
    val factory = new ObjectFactory()
    val xmlType = factory.createAkomaNtosoType()
    val output = factory.createAkomaNtoso(xmlType)

    val context = JAXBContext.newInstance(classOf[AkomaNtosoType])
    val marshaller = context.createMarshaller
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
    marshaller.marshal(output, System.out)
    item
  }
}
