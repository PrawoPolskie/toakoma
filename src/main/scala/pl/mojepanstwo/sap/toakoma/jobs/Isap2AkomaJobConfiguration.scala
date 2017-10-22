package pl.mojepanstwo.sap.toakoma.jobs

import javax.sql.DataSource
import javax.xml.bind.JAXBElement

import org.jsoup.nodes.Document
import org.springframework.batch.core.configuration.annotation.{EnableBatchProcessing, JobBuilderFactory, StepBuilderFactory, StepScope}
import org.springframework.batch.core.job.builder.FlowBuilder
import org.springframework.batch.core.job.flow.Flow
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.{Job, Step}
import org.springframework.beans.factory._
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.core.task.SimpleAsyncTaskExecutor
import pl.mojepanstwo.sap.toakoma._
import pl.mojepanstwo.sap.toakoma.deciders.StepText2LrDecider
import pl.mojepanstwo.sap.toakoma.listeners.ModelReaderListener
import pl.mojepanstwo.sap.toakoma.processors._
import pl.mojepanstwo.sap.toakoma.readers.{IsapReader, ModelReader}
import pl.mojepanstwo.sap.toakoma.services.DefaultScraperService
import pl.mojepanstwo.sap.toakoma.writers.{JaxbWriter, ModelWriter}
import pl.mojepanstwo.sap.toakoma.xml._

object Isap2AkomaJob {
  val NAME = "isap2akomaJob"
}

@Configuration
@EnableBatchProcessing
class Isap2AkomaJobConfiguration {

  @Autowired var jobs:  JobBuilderFactory  = _
  @Autowired var steps: StepBuilderFactory = _
  @Autowired var data:  DataSource         = _
  @Autowired var beans: BeanFactory        = _


  @Bean
	def isap2akomaJob: Job = {
    val jobBuilder = jobs.get(Isap2AkomaJob.NAME)
                         .incrementer(new RunIdIncrementer())

    val builder = jobBuilder
      .flow(stepRetrieveFromIsap)
      .next(stepPdfCheckEncryption)
      .next(stepPdf2Html)
      .next(stepImg2Txt)
      .next(stepHtmlPreXslt)
      .next(new FlowBuilder[Flow]("splitflow")
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
        .build)
      .end
    builder.build
	}

  @Bean
  def stepRetrieveFromIsap: Step = {
	  steps.get(currentMethodName)
	    .chunk[Document, Model](1)
	    .reader(readerRetrieveFromIsap(null))
	    .processor(processorRetrieveFromIsap)
      .writer(new ModelWriter)
	    .build
	}

  @Bean
  def stepPdfCheckEncryption: Step = {
    steps.get(currentMethodName)
      .chunk[Model, Model](1)
      .reader(getModelReader(currentMethodName.replace("step", "reader")))
      .listener(listenerModelReader)
      .processor(processorPdfCheckEncryption)
      .writer(new ModelWriter)
      .build
  }

  @Bean
  def stepPdf2Html: Step = {
    steps.get(currentMethodName)
      .chunk[Model, Model](1)
      .reader(getModelReader(currentMethodName.replace("step", "reader")))
      .listener(new ModelReaderListener)
      .processor(processorPdf2Html)
      .writer(new ModelWriter)
      .build
  }

  @Bean
  def stepHtmlPreXslt: Step = {
    steps.get(currentMethodName)
      .chunk[Model, Model](1)
      .reader(getModelReader(currentMethodName.replace("step", "reader")))
      .listener(listenerModelReader)
      .processor(processorPreXslt)
      .writer(new ModelWriter)
      .build
  }

  @Bean
  def stepImg2Txt: Step = {
    steps.get(currentMethodName)
      .chunk[Model, Model](1)
      .reader(getModelReader(currentMethodName.replace("step", "reader")))
      .listener(listenerModelReader)
      .processor(processorImg2Txt)
      .writer(new ModelWriter)
      .build
  }

  @Bean
  def stepText2Lr(pdf: Pdf.Value): Step = {
    steps.get("stepText2Lr: " + pdf)
      .chunk[Model, JAXBElement[AkomaNtosoType]](1)
      .reader(getModelReader(currentMethodName.replace("step", "reader")))
      .listener(listenerModelReader)
      .processor(processorText2Jaxb(pdf))
      .writer(writerText2Jaxb)
      .build
  }

  @Bean
  @StepScope
  def readerRetrieveFromIsap(@Value("#{jobParameters[id]}") id: String): IsapReader = {
    new IsapReader(id)
  }

  @Bean
  def listenerModelReader = {
    new ModelReaderListener
  }

  @Bean
  def writerText2Jaxb: JaxbWriter = {
    new JaxbWriter
  }

  @Bean
  def processorRetrieveFromIsap: IsapProcessor = {
    new IsapProcessor(new DefaultScraperService)
  }

  @Bean
  def processorPdfCheckEncryption: PdfCheckEncryptionProcessor = {
    new PdfCheckEncryptionProcessor
  }

  @Bean
  def processorPdf2Html: Pdf2HtmlProcessor = {
    new Pdf2HtmlProcessor
  }

  @Bean
  def processorPreXslt: PreXsltProcessor = {
    new PreXsltProcessor
  }

  @Bean
  def processorImg2Txt: Img2TxtProcessor = {
    new Img2TxtProcessor
  }

  @Bean
  def processorText2Jaxb(pdf: Pdf.Value): Text2JaxbProcessor = {
    pdf match {
      case Pdf.TEKST_UJEDNOLICONY  => new Text2JaxbProcessor(pdf)
      case Pdf.TEKST_AKTU          => new Text2JaxbProcessor(pdf)
      case Pdf.TEKST_OGLOSZONY     => new Text2JaxbProcessor(pdf)
      case _  => println("Unexpected case")
        new Text2JaxbProcessor(pdf)
    }
  }

  def getModelReader(name: String): ModelReader = {
    val configurableBeanFactory: ConfigurableBeanFactory = beans.asInstanceOf[ConfigurableBeanFactory]
    try {
      return configurableBeanFactory.getBean(name).asInstanceOf[ModelReader]
    } catch {
      case e: Exception => {
        val mr = new ModelReader
        configurableBeanFactory.registerSingleton(name, mr)
        return mr
      }
    }
  }

  def currentMethodName() : String = Thread.currentThread.getStackTrace()(2).getMethodName
}
