package pl.mojepanstwo.sap.toakoma.processors

import org.springframework.batch.item.ItemProcessor
import pl.mojepanstwo.sap.toakoma.readers.IsapModel

class MetadataProcessor extends ItemProcessor[IsapModel, IsapModel] {

  override def process(item:IsapModel): IsapModel = {
    //        val factory = new ObjectFactory()
    //        val an = factory.createAkomaNtoso(null)
    item
  }
}
