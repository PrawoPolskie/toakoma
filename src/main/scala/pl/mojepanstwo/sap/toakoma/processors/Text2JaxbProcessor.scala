package pl.mojepanstwo.sap.toakoma.processors

import org.springframework.batch.item.ItemProcessor
import pl.mojepanstwo.sap.toakoma.xml.{AkomaNtosoType, ObjectFactory}
import pl.mojepanstwo.sap.toakoma.{IsapModel, Pdf}
import javax.xml.bind.JAXBElement

class Text2JaxbProcessor(pdf:Pdf.Value) extends ItemProcessor[IsapModel, JAXBElement[AkomaNtosoType]] {

  override def process(item:IsapModel): JAXBElement[AkomaNtosoType] = {
    val input = item.texts(pdf)
    val factory = new ObjectFactory()
    val xmlType = factory.createAkomaNtosoType()
    val output = factory.createAkomaNtoso(xmlType)
    output
  }
}
