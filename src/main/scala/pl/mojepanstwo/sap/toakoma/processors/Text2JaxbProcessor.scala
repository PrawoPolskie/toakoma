package pl.mojepanstwo.sap.toakoma.processors

import org.springframework.batch.item.ItemProcessor
import pl.mojepanstwo.sap.toakoma.xml.{AkomaNtosoType, ObjectFactory}
import pl.mojepanstwo.sap.toakoma.{IsapModel, Pdf}
import javax.xml.bind.JAXBElement

import org.antlr.v4.runtime._
import org.antlr.v4.runtime.tree.{ErrorNode, TerminalNode}
import pl.mojepanstwo.sap.toakoma.grammar._

class Text2JaxbProcessor(pdf:Pdf.Value) extends ItemProcessor[IsapModel, JAXBElement[AkomaNtosoType]] {

  val factory = new ObjectFactory

  override def process(item:IsapModel): JAXBElement[AkomaNtosoType] = {
    val input = item.texts(pdf)
    parse(input)
  }

  def parse(input:String) = {
    val xmlType = factory.createAkomaNtosoType()
    val output = factory.createAkomaNtoso(xmlType)

    val l = new UstawaUjednoliconyLexer(new ANTLRInputStream(input))
    val p = new UstawaUjednoliconyParser(new CommonTokenStream(l))

    p.addParseListener(new UstawaUjednoliconyBaseListener {
      override def enterUstawa(ctx: UstawaUjednoliconyParser.UstawaContext): Unit = ???
      override def exitUstawa(ctx: UstawaUjednoliconyParser.UstawaContext): Unit = ???
      override def exitEveryRule(ctx: ParserRuleContext): Unit = ???
      override def visitErrorNode(node: ErrorNode): Unit = ???
      override def visitTerminal(node: TerminalNode): Unit = ???
      override def enterEveryRule(ctx: ParserRuleContext): Unit = ???
    })

    output
  }
}
