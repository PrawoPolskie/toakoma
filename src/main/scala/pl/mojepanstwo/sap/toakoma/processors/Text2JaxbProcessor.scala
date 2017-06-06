package pl.mojepanstwo.sap.toakoma.processors

import java.text.SimpleDateFormat

import org.springframework.batch.item.ItemProcessor
import pl.mojepanstwo.sap.toakoma.xml.{AkomaNtosoType, ObjectFactory, VersionType}
import pl.mojepanstwo.sap.toakoma._
import javax.xml.bind.JAXBElement

import org.antlr.v4.runtime._
import org.antlr.v4.runtime.tree._
import pl.mojepanstwo.sap.toakoma.grammar._


class Text2JaxbProcessor(pdf:Pdf.Value) extends ItemProcessor[IsapModel, JAXBElement[AkomaNtosoType]] {

  val factory = new ObjectFactory

  override def process(item:IsapModel): JAXBElement[AkomaNtosoType] = {

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
    meta.getReferences.get(0)
      .getOriginalOrPassiveRefOrActiveRef.add(factory.createTLCOrganization(factory.createReferenceType()))

    act.setBody(factory.createBodyType())



    val input = CharStreams.fromFileName(item.xmlPath(pdf))
    parse(input, akoma)
  }

  def parse(input:CharStream, akoma:JAXBElement[AkomaNtosoType]) = {

    val lexer = new UstawaLexer(input)
    val tokens = new CommonTokenStream(lexer)
    val parser = new UstawaParser(tokens)

    val documentContext = parser.document()

    val walker = new ParseTreeWalker
    val listener = new UstawaParserListener {
      override def enterDocument(ctx: UstawaParser.DocumentContext): Unit = println(ctx)
      override def enterElement(ctx: UstawaParser.ElementContext): Unit = println(ctx)
      override def enterProlog(ctx: UstawaParser.PrologContext): Unit = println(ctx)
      override def exitContent(ctx: UstawaParser.ContentContext): Unit = println(ctx)
      override def exitProlog(ctx: UstawaParser.PrologContext): Unit = println(ctx)
      override def enterAttribute(ctx: UstawaParser.AttributeContext): Unit = println(ctx)
      override def exitReference(ctx: UstawaParser.ReferenceContext): Unit = println(ctx)
      override def enterContent(ctx: UstawaParser.ContentContext): Unit = println(ctx)
      override def exitAttribute(ctx: UstawaParser.AttributeContext): Unit = println(ctx)
      override def enterReference(ctx: UstawaParser.ReferenceContext): Unit = println(ctx)
      override def exitMisc(ctx: UstawaParser.MiscContext): Unit = println(ctx)
      override def exitChardata(ctx: UstawaParser.ChardataContext): Unit = println(ctx)
      override def exitElement(ctx: UstawaParser.ElementContext): Unit = println(ctx)
      override def enterChardata(ctx: UstawaParser.ChardataContext): Unit = println(ctx)
      override def exitDocument(ctx: UstawaParser.DocumentContext): Unit = println(ctx)
      override def enterMisc(ctx: UstawaParser.MiscContext): Unit = println(ctx)
      override def exitEveryRule(parserRuleContext: ParserRuleContext): Unit = println(parserRuleContext)
      override def enterEveryRule(parserRuleContext: ParserRuleContext): Unit = println(parserRuleContext)
      override def visitErrorNode(errorNode: ErrorNode): Unit = println(errorNode)
      override def visitTerminal(terminalNode: TerminalNode): Unit = println(terminalNode)
    }
    walker.walk(listener, documentContext)

    akoma.getValue.getAct.getBody.getComponentRefOrClauseOrSection.add(factory.createChapter(factory.createHierarchy()))

    akoma
  }
}
