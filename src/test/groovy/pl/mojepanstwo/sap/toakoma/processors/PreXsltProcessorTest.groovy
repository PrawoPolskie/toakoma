package pl.mojepanstwo.sap.toakoma.processors

import javax.xml.transform.stream.StreamSource

import org.xmlunit.diff.Diff
import org.xmlunit.diff.ElementSelectors
import org.xmlunit.diff.DefaultNodeMatcher
import org.xmlunit.matchers.CompareMatcher
import org.xmlunit.builder.DiffBuilder
import org.xmlunit.builder.Input
import org.xmlunit.builder.Transform
import net.sf.saxon.s9api.Processor
import net.sf.saxon.s9api.Serializer
import net.sf.saxon.s9api.XdmDestination

import static org.hamcrest.CoreMatchers.*

import spock.lang.*

class PreXsltProcessorTest extends Specification {

  def "remove_spans"() {
	expect:
	  def control = new StreamSource(this.getClass()
		                                 .getResourceAsStream('/isap/' + id + '/after_remove_spans.xml'))
	  def test = new StreamSource(this.getClass()
		                                 .getResourceAsStream('/isap/' + id + '/output.html'))
	  def p = new PreXsltProcessor()
	  p.xsl_remove_spans.setInitialContextNode(p.processor.newDocumentBuilder().build(test))
	  def sw = new StringWriter()
	  def out = p.processor.newSerializer(sw)
	  out.setOutputProperty(Serializer.Property.METHOD, "xml")
	  out.setOutputProperty(Serializer.Property.INDENT, "yes")
      p.xsl_remove_spans.setDestination(out)
	  p.xsl_remove_spans.transform()

	  def diff = DiffBuilder.compare(control).withTest(sw.toString())
	                                         .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byName))
	                                         .checkForSimilar()
	                                         .build()
	  !diff.hasDifferences()

	where:
	  id << ['WDU20170000001']
  }

}
