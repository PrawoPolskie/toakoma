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
import pl.mojepanstwo.sap.toakoma.readers.{IsapReader, Text2JaxbReader}
import org.springframework.beans.factory.annotation.Value
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.FlowBuilder
import org.springframework.batch.core.job.flow.Flow
import org.springframework.core.task.SimpleAsyncTaskExecutor
import pl.mojepanstwo.sap.toakoma.deciders.StepText2LrDecider
import pl.mojepanstwo.sap.toakoma.{IsapModel, Pdf}
import pl.mojepanstwo.sap.toakoma.processors.{IsapProcessor, Text2JaxbProcessor}
import pl.mojepanstwo.sap.toakoma.writers.IsapWriter
import pl.mojepanstwo.sap.toakoma.xml.AkomaNtosoType

object Isap2AkomaJob {
  val NAME = "isap2akomaJob"
}

@Configuration
@EnableBatchProcessing
class Isap2AkomaJobConfiguration {

  @Autowired
  var jobs: JobBuilderFactory = _

  @Autowired
  var steps: StepBuilderFactory = _

  @Autowired
  var data: DataSource = _
    
  @Bean
	def isap2akomaJob: Job = {
    val jobBuilder = jobs.get(Isap2AkomaJob.NAME)

    val flowSplit = new FlowBuilder[Flow]("splitflow")
      .split(new SimpleAsyncTaskExecutor())
      .add(
        new FlowBuilder[Flow]("flowTekstAktu")
          .from(new StepText2LrDecider(Pdf.TEKST_AKTU))
            .on("EXIST").to(stepText2Lr(Pdf.TEKST_AKTU))
          .build,
        new FlowBuilder[Flow]("flowTekstUjednolicony")
          .from(new StepText2LrDecider(Pdf.TEKST_UJEDNOLICONY))
            .on("EXIST").to(stepText2Lr(Pdf.TEKST_UJEDNOLICONY))
          .build)
      .build

    val builder = jobBuilder
      .flow(stepRetrieveFromIsap)
      .next(flowSplit)
      .end
    builder.build
	}

  @Bean
  def stepRetrieveFromIsap: Step = {
	  steps.get("stepRetrieveFromIsap")
	       .chunk[Document, IsapModel](1)
	       .reader(readerRetrieveFromIsap(null))
	       .processor(processorRetrieveFromIsap)
         .writer(writerRetrieveFromIsap)
	       .build
	}

  @Bean
  @StepScope
  def readerRetrieveFromIsap(@Value("#{jobParameters[id]}") id: String): IsapReader = {
    new IsapReader(id)
  }

  @Bean
  def processorRetrieveFromIsap: IsapProcessor = {
    new IsapProcessor()
  }

  @Bean
  def writerRetrieveFromIsap: IsapWriter = {
    new IsapWriter()
  }


  def stepText2Lr(pdf: Pdf.Value): Step = {
    steps.get("stepText2Lr: " + pdf)
         .chunk[IsapModel, JAXBElement[AkomaNtosoType]](1)
         .reader(readerText2Lr)
         .processor(processorText2Jaxb(pdf))
         .build
  }

  def readerText2Lr: Text2JaxbReader = {
    new Text2JaxbReader()
  }

  def processorText2Jaxb(pdf: Pdf.Value): Text2JaxbProcessor = {
    new Text2JaxbProcessor(pdf)
  }
}