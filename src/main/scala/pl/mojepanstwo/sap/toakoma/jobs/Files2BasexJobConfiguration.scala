package pl.mojepanstwo.sap.toakoma.jobs

import javax.sql.DataSource
import javax.xml.bind.JAXBElement

import org.jsoup.nodes.Document
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.context.annotation.Configuration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.context.annotation.Bean
import org.springframework.batch.core.{Job, Step}
import pl.mojepanstwo.sap.toakoma.readers.{IsapReader, ModelReader}
import org.springframework.beans.factory.annotation.Value
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.FlowBuilder
import org.springframework.batch.core.job.flow.Flow
import org.springframework.core.task.SimpleAsyncTaskExecutor
import pl.mojepanstwo.sap.toakoma.deciders.StepText2LrDecider
import pl.mojepanstwo.sap.toakoma._
import pl.mojepanstwo.sap.toakoma.processors._
import pl.mojepanstwo.sap.toakoma.writers.{JaxbWriter, ModelWriter}
import pl.mojepanstwo.sap.toakoma.services.DefaultScraperService
import pl.mojepanstwo.sap.toakoma.xml._

object Files2BasexJob {
  val NAME = "files2BasexJob"
}

@Configuration
@EnableBatchProcessing
class Files2BasexJobConfiguration {

  @Autowired
  var jobs: JobBuilderFactory = _

  @Autowired
  var steps: StepBuilderFactory = _

  @Bean
	def isap2akomaJob: Job = {
    val jobBuilder = jobs.get(Files2BasexJob.NAME)
    val builder = jobBuilder.start(null : Step)
    builder.build
  }
}
