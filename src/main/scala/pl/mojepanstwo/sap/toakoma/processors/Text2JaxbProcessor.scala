package pl.mojepanstwo.sap.toakoma.processors

import java.text.SimpleDateFormat

import org.springframework.batch.item.ItemProcessor
import pl.mojepanstwo.sap.toakoma.xml.{AkomaNtosoType, ObjectFactory, VersionType}
import pl.mojepanstwo.sap.toakoma._
import javax.xml.bind.JAXBElement

import org.antlr.v4.runtime._
import org.antlr.v4.runtime.tree._
import pl.mojepanstwo.sap.toakoma.grammar._


class Text2JaxbProcessor(pdf:Pdf.Value) extends ItemProcessor[Model, JAXBElement[AkomaNtosoType]] {

  val factory = new ObjectFactory

  override def process(item:Model): JAXBElement[AkomaNtosoType] = {

    val akoma = factory.createAkomaNtoso(factory.createAkomaNtosoType())
    akoma.getValue.setAct(factory.createHierarchicalStructure())

    val act = akoma.getValue.getAct

    act.setName(item.title)
    act.setContains(VersionType.ORIGINAL_VERSION)

    act.setMeta(factory.createMeta())

    val meta = act.getMeta

    meta.setIdentification(factory.createIdentification())
    meta.getIdentification.setSource("#rcl")
    meta.getIdentification.setFRBRWork(factory.createFRBRWork())
    meta.getIdentification.getFRBRWork.setFRBRthis(factory.createValueType())

    var eli = "/eli/" + item.dziennik.eli +"/" + new SimpleDateFormat("yyyy").format(item.dataWydania) + "/"
    if(item.number != null) eli += item.number + "/"
    eli += item.position

    meta.getIdentification.getFRBRWork.getFRBRthis.setValue(eli)

    meta.setPublication(factory.createPublication())
    meta.getPublication.setDate(new SimpleDateFormat("yyyy-MM-dd").format(item.dataWydania))

    meta.getReferences.add(factory.createRefItems())

    meta.getReferences.get(0).setSource("#rcl")

    val tlcorgRCL = factory.createTLCOrganization(factory.createReferenceType())
    tlcorgRCL.getValue.setEId("#rcl")
    tlcorgRCL.getValue.setHref("http://www.rcl.gov.pl/")
    tlcorgRCL.getValue.setShowAs("Rządowe Centrum Legislacji")
    meta.getReferences.get(0)
      .getOriginalOrPassiveRefOrActiveRef.add(tlcorgRCL)

    act.setBody(factory.createBodyType())



    val input = CharStreams.fromFileName(item.xmlPath(pdf))
    parse(input, akoma)
  }

  def parse(input:CharStream, akoma:JAXBElement[AkomaNtosoType]) = {

    val lexer = new ActLexer(input)
    val tokens = new CommonTokenStream(lexer)
    val parser = new ActParser(tokens)

    val documentContext = parser.act()

    val walker = new ParseTreeWalker
    val listener = new ActParserListener {
      def enterAct(ctx : ActParser.ActContext) = ???
      def exitAct(ctx : ActParser.ActContext) = ???

      def enterTitle(ctx : ActParser.TitleContext) = ???
      def exitTitle(ctx : ActParser.TitleContext) = ???
      def enterPreface(ctx: ActParser.PrefaceContext): Unit = ???
      def exitPreface(ctx: ActParser.PrefaceContext): Unit = ???

      def enterMain(ctx : ActParser.MainContext) = ???
      def exitMain(ctx : ActParser.MainContext) = ???
      def enterPreamble(ctx: ActParser.PreambleContext): Unit = ???
      def exitPreamble(ctx: ActParser.PreambleContext): Unit = ???
      def enterParagraph(ctx: ActParser.ParagraphContext): Unit = ???
      def exitParagraph(ctx: ActParser.ParagraphContext): Unit = ???

      def visitTerminal(node : TerminalNode) = ???
      def visitErrorNode(node : ErrorNode) = ???
      def enterEveryRule(ctx: ParserRuleContext) = ???
      def exitEveryRule(ctx : ParserRuleContext) = ???
    }
//    walker.walk(listener, documentContext)

    akoma.getValue.getAct.getBody.getComponentRefOrClauseOrSection.add(factory.createChapter(factory.createHierarchy()))

    akoma
  }
}
