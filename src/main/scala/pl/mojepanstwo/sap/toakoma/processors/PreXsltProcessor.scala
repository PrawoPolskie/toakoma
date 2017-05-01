package pl.mojepanstwo.sap.toakoma.processors

import org.springframework.batch.item.ItemProcessor
import pl.mojepanstwo.sap.toakoma.IsapModel

class PreXsltProcessor extends ItemProcessor[IsapModel, IsapModel] {

  override def process(item:IsapModel): IsapModel = {
    item
  }

}
