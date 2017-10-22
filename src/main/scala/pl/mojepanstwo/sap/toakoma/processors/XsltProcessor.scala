package pl.mojepanstwo.sap.toakoma.processors

import java.io.File
import javax.xml.transform.stream.StreamSource

import net.sf.saxon.s9api._

abstract class XsltProcessor extends Model2ModelProcessor {
  val _processor = new Processor(false)
  def processor = _processor

  val xsltCompiler: XsltCompiler = processor.newXsltCompiler
  val queryCompiler: XQueryCompiler = processor.newXQueryCompiler

  queryCompiler.setLanguageVersion("3.1")

  def getXsl(name: String): XsltTransformer =
    xsltCompiler.compile(
      new StreamSource(classOf[RemoveSpansProcessor].getResourceAsStream(name))).load

  def getQuery(name: String): XQueryEvaluator =
    queryCompiler.compile(
      classOf[PreXsltProcessor].getResourceAsStream(name)).load

  def applyXsl(input: String, dirPath: String, xsl: XsltTransformer, name: String): File = {
    val source = processor.newDocumentBuilder.build(new StreamSource(new File(input)))
    val out = processor.newSerializer(new File(dirPath + "/after_" + name + ".xml"))
    out.setOutputProperty(Serializer.Property.METHOD, "xml")
    out.setOutputProperty(Serializer.Property.INDENT, "yes")

    xsl.setInitialContextNode(source)
    xsl.setDestination(out)
    xsl.transform
    new File(dirPath + "/after_" + name + ".xml")
  }
}
