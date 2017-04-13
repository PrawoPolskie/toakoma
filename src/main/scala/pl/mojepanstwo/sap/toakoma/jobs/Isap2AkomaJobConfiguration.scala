package pl.mojepanstwo.sap.toakoma.jobs

import javax.sql.DataSource
import javax.xml.bind.JAXBElement

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.context.annotation.Configuration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.context.annotation.Bean
import org.springframework.batch.core.{Job, Step}
import org.springframework.batch.item.{ItemProcessor, ItemReader}
import pl.mojepanstwo.sap.toakoma.readers.{IsapModel, IsapReader}
import org.springframework.beans.factory.annotation.Value
import org.springframework.batch.core.configuration.annotation.StepScope
import pl.mojepanstwo.sap.toakoma.processors.MetadataProcessor
import pl.mojepanstwo.sap.toakoma.xml.{AkomaNtosoType, ObjectFactory}

object Isap2AkomaJob {
  val NAME = "isap2akomaJob"
}

@Configuration
@EnableBatchProcessing
class Isap2AkomaJobConfiguration {
  
  import Isap2AkomaJob.NAME
  
  @Autowired
  var jobs: JobBuilderFactory = _

  @Autowired
  var steps: StepBuilderFactory = _

  @Autowired
  var data: DataSource = _
    
  @Bean
	def isap2akomaJob: Job = {
	  jobs.get(NAME)
		    .start(stepMetadata)
//        .next(step)
		    .build()
	}
  
	def stepMetadata: Step = {
	  steps.get("stepMetadata")
	       .chunk(1)
	       .reader(readerFromIsap(null))
	       .processor(processorMetadata)
	       .build()
	}
	
  @Bean
	@StepScope
	def readerFromIsap(@Value("#{jobParameters[id]}") id: String): IsapReader = {
    new IsapReader(id)
  }

  def processorMetadata: ItemProcessor[IsapModel, IsapModel] = {
    new MetadataProcessor()
  }
}