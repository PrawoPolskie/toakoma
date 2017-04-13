package pl.mojepanstwo.sap.toakoma.jobs

import javax.sql.DataSource

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.context.annotation.Configuration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.context.annotation.Bean
import org.springframework.batch.core.{Job, Step}
import pl.mojepanstwo.sap.toakoma.readers.{IsapModel, IsapReader}
import org.springframework.beans.factory.annotation.Value
import org.springframework.batch.core.configuration.annotation.StepScope
import pl.mojepanstwo.sap.toakoma.processors.{MetadataProcessor, Pdf2TextProcessor}

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
		    .start(stepPdf2Text)
//        .next(step)
		    .build()
	}
  
	def stepPdf2Text: Step = {
	  steps.get("stepPdf2Text")
	       .chunk[IsapModel, IsapModel](1)
	       .reader(readerFromIsap(null))
	       .processor(processorPdf2Text)
	       .build()
	}
	
  @Bean
	@StepScope
	def readerFromIsap(@Value("#{jobParameters[id]}") id: String): IsapReader = {
    new IsapReader(id)
  }

  def processorPdf2Text: Pdf2TextProcessor = {
    new Pdf2TextProcessor()
  }
}