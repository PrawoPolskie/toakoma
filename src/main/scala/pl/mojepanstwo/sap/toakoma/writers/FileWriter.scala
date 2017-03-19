package pl.mojepanstwo.sap.toakoma.writers

import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemWriter
import pl.mojepanstwo.sap.toakoma.xml.AkomaNtosoType

class FileWriter extends ItemWriter[AkomaNtosoType] {
  
  val logger = LoggerFactory.getLogger(this.getClass())
  
  @Override
	def write(items: java.util.List[_ <: AkomaNtosoType] ) = {
    
  }
}