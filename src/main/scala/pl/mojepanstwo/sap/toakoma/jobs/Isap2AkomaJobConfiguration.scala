package pl.mojepanstwo.sap.toakoma.jobs

import javax.sql.DataSource
import javax.xml.bind.JAXBElement

import org.jsoup.nodes.Document
import org.springframework.batch.core.configuration.annotation.{EnableBatchProcessing, JobBuilderFactory, StepBuilderFactory, StepScope}
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.{Job, Step}
import org.springframework.beans.factory._
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.context.annotation.{Bean, Configuration}
import pl.mojepanstwo.sap.toakoma._
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
      .next(stepTitle)
      .next(stepBlocks)
      .next(stepLines)
      .next(stepLeadingSpaces)
      .next(stepText2Lr)
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
  @Bean def stepTitle:              Step = stepModel2Model(currentMethodName, processorTitle)
  @Bean def stepBlocks:             Step = stepModel2Model(currentMethodName, processorBlocks)
  @Bean def stepLines:              Step = stepModel2Model(currentMethodName, processorLines)
  @Bean def stepLeadingSpaces:      Step = stepModel2Model(currentMethodName, processorLeadingSpaces)

  @Bean
  def stepText2Lr: Step =
    steps.get(currentMethodName)
      .chunk[Model, JAXBElement[AkomaNtosoType]](1)
      .reader(new ModelReader)
      .listener(listenerModelReader)
      .processor(processorText2Jaxb)
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
  @Bean def processorTitle:              Model2ModelProcessor = new TitleProcessor
  @Bean def processorBlocks:             Model2ModelProcessor = new BlocksProcessor
  @Bean def processorLines:              Model2ModelProcessor = new LinesProcessor
  @Bean def processorLeadingSpaces:      Model2ModelProcessor = new LeadingSpacesProcessor

  @Bean def processorText2Jaxb: Text2JaxbProcessor = new Text2JaxbProcessor

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
