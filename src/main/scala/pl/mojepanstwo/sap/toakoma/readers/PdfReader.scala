package pl.mojepanstwo.sap.toakoma.readers

import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemReader
import pl.mojepanstwo.sap.toakoma.xml.AkomaNtosoType
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Value
import org.springframework.batch.core.configuration.annotation.StepScope
import scala.annotation.meta.getter
import org.springframework.context.annotation.Bean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.context.annotation.ComponentScan
import org.springframework.boot.autoconfigure.EnableAutoConfiguration

class PdfReader(filePath: String) extends ItemReader[String] {
  
  val logger = LoggerFactory.getLogger(this.getClass())
    
	def read() : String = {
    null
  }
}