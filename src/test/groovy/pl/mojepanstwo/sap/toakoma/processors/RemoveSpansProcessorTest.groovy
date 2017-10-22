package pl.mojepanstwo.sap.toakoma.processors

import javax.xml.transform.stream.StreamSource

import org.xmlunit.diff.ElementSelectors
import org.xmlunit.diff.DefaultNodeMatcher
import org.xmlunit.builder.DiffBuilder
import net.sf.saxon.s9api.Serializer
import spock.lang.*

class RemoveSpansProcessorTest extends Specification {

  def "remove_spans"() {
	expect:
	  def control = new StreamSource(this.getClass()
		                                 .getResourceAsStream('/isap/' + id + '/after_remove_spans.xml'))
	  def test = new StreamSource(this.getClass()
		                                 .getResourceAsStream('/isap/' + id + '/output.html'))
	  def p = new RemoveSpansProcessor()
	  p.xsl.setInitialContextNode(p._processor().newDocumentBuilder().build(test))
	  def sw = new StringWriter()
	  def out = p._processor().newSerializer(sw)
	  out.setOutputProperty(Serializer.Property.METHOD, "xml")
	  out.setOutputProperty(Serializer.Property.INDENT, "yes")
      p.xsl.setDestination(out)
	  p.xsl.transform()

	  def diff = DiffBuilder.compare(control).withTest(sw.toString())
	                                         .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byName))
	                                         .checkForSimilar()
	                                         .build()
	  !diff.hasDifferences()

	where:
	  id << ['WDU20170000001']
  }

}
