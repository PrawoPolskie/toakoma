package pl.mojepanstwo.sap.toakoma.processors

import java.io.File
import javax.xml.transform.stream.StreamSource

import net.sf.saxon.s9api._
import pl.mojepanstwo.sap.toakoma._

class PreXsltProcessor extends Model2ModelProcessor {

  val processor = new Processor(false)

  val compiler  = processor.newXsltCompiler

  val xsl_join_breaks    = compiler.compile(new StreamSource(classOf[PreXsltProcessor].getResourceAsStream("join_breaks.xsl"))).load
  val xsl_join_pages     = compiler.compile(new StreamSource(classOf[PreXsltProcessor].getResourceAsStream("join_pages.xsl"))).load
  val xsl_title          = compiler.compile(new StreamSource(classOf[PreXsltProcessor].getResourceAsStream("title.xsl"))).load
  val xsl_blocks         = compiler.compile(new StreamSource(classOf[PreXsltProcessor].getResourceAsStream("blocks.xsl"))).load
  val xsl_lines          = compiler.compile(new StreamSource(classOf[PreXsltProcessor].getResourceAsStream("lines.xsl"))).load
  val xsl_leading_spaces = compiler.compile(new StreamSource(classOf[PreXsltProcessor].getResourceAsStream("leading_spaces.xsl"))).load


  override def process(item:Model): Model = {

    val dirPath = item.linkHtml
    var input = new File(item.xmlPath)


    xsl_title.setParameter(new QName("title"), new XdmAtomicValue(item.title))
    input = applyXsl(input, dirPath, xsl_title,          "title")
    xsl_blocks.setParameter(new QName("main-font_size"), new XdmAtomicValue(item.mainFontSize))
    input = applyXsl(input, dirPath, xsl_blocks,         "blocks")
    input = applyXsl(input, dirPath, xsl_lines,          "lines")
    input = applyXsl(input, dirPath, xsl_leading_spaces, "leading_spaces")

    item.xmlPath = input.getAbsolutePath

    item
  }

  def applyXsl(input:File, dirPath:String, xsl:XsltTransformer, name:String) : File = {
    var source = processor.newDocumentBuilder.build(new StreamSource(input))
    var out = processor.newSerializer(new File(dirPath + "/after_" + name + ".xml"))
    out.setOutputProperty(Serializer.Property.METHOD, "xml")
    out.setOutputProperty(Serializer.Property.INDENT, "yes")

    xsl.setInitialContextNode(source)
    xsl.setDestination(out)
    xsl.transform
    new File(dirPath + "/after_" + name + ".xml")
  }
}
