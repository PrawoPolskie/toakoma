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
	def isap2akomaJob: Job =
    jobs.get(Isap2AkomaJob.NAME)
      .incrementer(new RunIdIncrementer)
      .flow(stepRetrieveFromIsap)
      .next(stepPdfCheckEncryption)
      .next(stepPdf2Html)
      .next(stepImg2Txt)
      .next(stepRemoveSpans)
      .next(stepClassAttrs)
      .next(stepPages)
      .next(stepHeaders)
      .next(stepFooters)
      .next(stepGetFonts)
      .next(stepFootnotes)
      .next(stepJoinPages)
      .next(stepJoinBreaks)
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
      .build

  @Bean
  def stepRetrieveFromIsap: Step =
	  steps.get(currentMethodName)
	    .chunk[Document, Model](1)
	    .reader(readerRetrieveFromIsap(null))
	    .processor(processorRetrieveFromIsap)
      .writer(new ModelWriter)
	    .build

  @Bean def stepPdfCheckEncryption: Step = stepModel2Model(currentMethodName, processorPdfCheckEncryption)
  @Bean def stepPdf2Html:           Step = stepModel2Model(currentMethodName, processorPdf2Html)
  @Bean def stepHtmlPreXslt:        Step = stepModel2Model(currentMethodName, processorPreXslt)
  @Bean def stepImg2Txt:            Step = stepModel2Model(currentMethodName, processorImg2Txt)
  @Bean def stepRemoveSpans:        Step = stepModel2Model(currentMethodName, processorRemoveSpans)
  @Bean def stepClassAttrs:         Step = stepModel2Model(currentMethodName, processorClassAttrs)
  @Bean def stepPages:              Step = stepModel2Model(currentMethodName, processorPages)
  @Bean def stepHeaders:            Step = stepModel2Model(currentMethodName, processorHeaders)
  @Bean def stepFooters:            Step = stepModel2Model(currentMethodName, processorFooters)
  @Bean def stepGetFonts:           Step = stepModel2Model(currentMethodName, processorGetFonts)
  @Bean def stepFootnotes:          Step = stepModel2Model(currentMethodName, processorFootnotes)
  @Bean def stepJoinPages:          Step = stepModel2Model(currentMethodName, processorJoinPages)
  @Bean def stepJoinBreaks:         Step = stepModel2Model(currentMethodName, processorJoinBreaks)

  @Bean
  def stepText2Lr(pdf: Pdf.Value): Step =
    steps.get("stepText2Lr: " + pdf)
      .chunk[Model, JAXBElement[AkomaNtosoType]](1)
      .reader(new ModelReader)
      .listener(listenerModelReader)
      .processor(processorText2Jaxb(pdf))
      .writer(writerText2Jaxb)
      .build

  @Bean
  @StepScope
  def readerRetrieveFromIsap(@Value("#{jobParameters[id]}") id: String): IsapReader =
    new IsapReader(id)

  @Bean def listenerModelReader = new ModelReaderListener

  @Bean def writerText2Jaxb: JaxbWriter = new JaxbWriter

  @Bean def processorRetrieveFromIsap: IsapProcessor = new IsapProcessor(new DefaultScraperService)

  @Bean def processorPdfCheckEncryption: Model2ModelProcessor = new PdfCheckEncryptionProcessor
  @Bean def processorPdf2Html:           Model2ModelProcessor = new Pdf2HtmlProcessor
  @Bean def processorImg2Txt:            Model2ModelProcessor = new Img2TxtProcessor
  @Bean def processorRemoveSpans:        Model2ModelProcessor = new RemoveSpansProcessor
  @Bean def processorClassAttrs:         Model2ModelProcessor = new ClassAttrsProcessor
  @Bean def processorPages:              Model2ModelProcessor = new PagesProcessor
  @Bean def processorHeaders:            Model2ModelProcessor = new HeadersProcessor
  @Bean def processorFooters:            Model2ModelProcessor = new FootersProcessor
  @Bean def processorGetFonts:           Model2ModelProcessor = new GetFontsProcessor
  @Bean def processorFootnotes:          Model2ModelProcessor = new FootnotesProcessor
  @Bean def processorJoinPages:          Model2ModelProcessor = new JoinPagesProcessor
  @Bean def processorJoinBreaks:         Model2ModelProcessor = new JoinBreaksProcessor

  @Bean
  def processorPreXslt: PreXsltProcessor = {
    new PreXsltProcessor
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

  def stepModel2Model(name: String, processor: Model2ModelProcessor): Step =
    steps.get(name)
      .chunk[Model, Model](1)
      .reader(new ModelReader)
      .listener(listenerModelReader)
      .processor(processor)
      .writer(new ModelWriter)
      .build

  def currentMethodName : String = Thread.currentThread.getStackTrace()(2).getMethodName
}
