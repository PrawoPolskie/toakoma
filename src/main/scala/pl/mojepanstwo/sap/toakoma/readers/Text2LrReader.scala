package pl.mojepanstwo.sap.toakoma.readers

import org.springframework.batch.item.ItemReader
import pl.mojepanstwo.sap.toakoma.{IsapModel, Pdf}

class Text2LrReader(pdf:Pdf.Value) extends ItemReader[IsapModel] {

  def read : IsapModel = {
    println("X")
    return null
  }
}
