package pl.mojepanstwo.sap.toakoma.processors

import org.springframework.batch.item.ItemProcessor
import pl.mojepanstwo.sap.toakoma.{IsapModel, Pdf}

class Text2LrProcessor(pdf:Pdf.Value) extends ItemProcessor[IsapModel, IsapModel] {

  override def process(item:IsapModel): IsapModel = {
    println(item)
    println(pdf)
    item
  }
}
