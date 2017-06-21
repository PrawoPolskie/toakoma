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

    val lexer = new UstawaLexer(input)
    val tokens = new CommonTokenStream(lexer)
    val parser = new UstawaParser(tokens)

    val documentContext = parser.act()

    val walker = new ParseTreeWalker
    val listener = new UstawaParserListener {
      override def enterAct(ctx: UstawaParser.ActContext): Unit = println("enterAct")
      override def enterTitle(ctx: UstawaParser.TitleContext): Unit = println("enterTitle")
      override def exitTitle(ctx: UstawaParser.TitleContext): Unit = println("exitTitle")
      override def enterMain(ctx: UstawaParser.MainContext): Unit = println("enterMain")
      override def exitMain(ctx: UstawaParser.MainContext): Unit = println("exitMain")
      override def enterFootnotes(ctx: UstawaParser.FootnotesContext): Unit = println("enterFootnotes")
      override def enterFootnote(ctx: UstawaParser.FootnoteContext): Unit = println("enterFootnote")
      override def exitFootnote(ctx: UstawaParser.FootnoteContext): Unit = println("exitFootnote")
      override def exitFootnotes(ctx: UstawaParser.FootnotesContext): Unit = println("exitFootnotes")
      override def exitAct(ctx: UstawaParser.ActContext): Unit = println("exitAct")
      override def exitEveryRule(ctx: ParserRuleContext): Unit = println("exitEveryRule")
      override def enterEveryRule(ctx: ParserRuleContext): Unit = println("enterEveryRule")
      override def visitErrorNode(errorNode: ErrorNode): Unit = println("visitErrorNode")
      override def visitTerminal(terminalNode: TerminalNode): Unit = println("visitTerminal")
    }
    walker.walk(listener, documentContext)

    akoma.getValue.getAct.getBody.getComponentRefOrClauseOrSection.add(factory.createChapter(factory.createHierarchy()))

    akoma
  }
}
