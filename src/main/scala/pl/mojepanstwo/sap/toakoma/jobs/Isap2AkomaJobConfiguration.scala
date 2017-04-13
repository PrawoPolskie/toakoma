package pl.mojepanstwo.sap.toakoma.jobs

import javax.sql.DataSource

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.context.annotation.Configuration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.context.annotation.Bean
import org.springframework.batch.core.{Job, Step}
import pl.mojepanstwo.sap.toakoma.readers.IsapReader
import org.springframework.beans.factory.annotation.Value
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.flow.Flow
import org.springframework.core.task.SimpleAsyncTaskExecutor
import pl.mojepanstwo.sap.toakoma.IsapModel
import pl.mojepanstwo.sap.toakoma.processors.{Pdf2TextProcessor, Text2LrProcessor}

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
		    .start(stepRetrieveFromIsap)
        .split(new SimpleAsyncTaskExecutor()).add(flowTekstAktu, flowTekstUjednolicony)
        .end()
		    .build()
	}
  
	def stepRetrieveFromIsap: Step = {
	  steps.get("stepRetrieveFromIsap")
	       .chunk[IsapModel, IsapModel](1)
	       .reader(readerFromIsap(null))
	       .processor(processorPdf2Text)
	       .build()
	}

  def flowTekstAktu: Flow = {
    null
  }

  def flowTekstUjednolicony: Flow = {
    null
  }

  def stepText2Lr: Step = {
    steps.get("stepText2Lr")
         .chunk[IsapModel, IsapModel](1)
         .processor(processorText2Lr)
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

  def processorText2Lr: Text2LrProcessor = {
    new Text2LrProcessor()
  }
}