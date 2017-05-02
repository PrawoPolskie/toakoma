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
import pl.mojepanstwo.sap.toakoma.{IsapModel, Pdf}
import pl.mojepanstwo.sap.toakoma.processors._
import pl.mojepanstwo.sap.toakoma.writers.{JaxbWriter, ModelWriter}
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
        new FlowBuilder[Flow]("flowTekstOgloszony")
          .from(new StepText2LrDecider(Pdf.TEKST_OGLOSZONY))
          .on("EXIST").to(stepText2Lr(Pdf.TEKST_OGLOSZONY))
          .build,
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
      .next(stepPdf2Txt)
      .next(stepPdf2Html)
      .next(stepHtmlPreXslt)
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
      .writer(writerModel2Context)
	    .build
	}

  @Bean
  @StepScope
  def readerRetrieveFromIsap(@Value("#{jobParameters[id]}") id: String): IsapReader = {
    new IsapReader(id)
  }

  @Bean
  def processorRetrieveFromIsap: IsapProcessor = {
    new IsapProcessor
  }

  @Bean
  def writerModel2Context: ModelWriter = {
    new ModelWriter
  }

  def stepPdf2Txt: Step = {
    steps.get("stepPdf2Txt")
      .chunk[IsapModel, IsapModel](1)
      .reader(readerModelFromContext)
      .processor(processorPdf2Txt)
      .writer(writerModel2Context)
      .build
  }

  def processorPdf2Txt: Pdf2TxtProcessor = {
    new Pdf2TxtProcessor
  }

  def readerModelFromContext: ModelReader = {
    new ModelReader
  }

  def stepPdf2Html: Step = {
    steps.get("stepPdf2Html")
      .chunk[IsapModel, IsapModel](1)
      .reader(readerModelFromContext)
      .processor(processorPdf2Html)
      .writer(writerModel2Context)
      .build
  }

  def processorPdf2Html: Pdf2HtmlProcessor = {
    new Pdf2HtmlProcessor
  }

  def stepHtmlPreXslt: Step = {
    steps.get("stepHtmlPreXslt")
      .chunk[IsapModel, IsapModel](1)
      .reader(readerModelFromContext)
      .processor(processorPreXslt)
      .writer(writerModel2Context)
      .build
  }

  def processorPreXslt: PreXsltProcessor = {
    new PreXsltProcessor
  }

  def stepText2Lr(pdf: Pdf.Value): Step = {
    steps.get("stepText2Lr: " + pdf)
      .chunk[IsapModel, JAXBElement[AkomaNtosoType]](1)
      .reader(readerText2Lr)
      .processor(processorText2Jaxb(pdf))
      .writer(writerText2Jaxb)
      .build
  }

  def readerText2Lr: ModelReader = {
    new ModelReader()
  }

  def processorText2Jaxb(pdf: Pdf.Value): Text2JaxbProcessor = {
    pdf match {
      case Pdf.TEKST_UJEDNOLICONY  => new Text2JaxbProcessor(pdf)
      case Pdf.TEKST_AKTU          => new Text2JaxbProcessor(pdf)
      case Pdf.TEKST_OGLOSZONY     => new Text2JaxbProcessor(pdf)
      case _  => println("Unexpected case")
        new Text2JaxbProcessor(pdf)
    }
  }

  def writerText2Jaxb: JaxbWriter = {
    new JaxbWriter
  }

}