package pl.mojepanstwo.sap.toakoma.readers

import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemReader
import pl.mojepanstwo.sap.toakoma.xml.AkomaNtosoType
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Value
import org.springframework.batch.core.configuration.annotation.StepScope
import scala.annotation.meta.getter
import org.springframework.context.annotation.Bean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.context.annotation.ComponentScan
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.cos.COSDocument
import java.io.File
import java.io.FileInputStream
import org.apache.pdfbox.pdfparser.PDFParser
import org.apache.pdfbox.io.RandomAccessFile
import java.io.IOException

class PdfReader extends ItemReader[String] {
  
  val logger = LoggerFactory.getLogger(this.getClass())
  
  def this(filePath: String) {
    this()
    var pdfStripper : PDFTextStripper = new PDFTextStripper()
    var pdDoc : PDDocument = null
    var cosDoc : COSDocument = null
    val file : File = new File(filePath)
    try {
      val parser : PDFParser = new PDFParser(new RandomAccessFile(file, "rw"))
      parser.parse();
      cosDoc = parser.getDocument()
      pdDoc = new PDDocument(cosDoc)
      pdfStripper.setStartPage(1)
      pdfStripper.setEndPage(5)
      var parsedText : String = pdfStripper.getText(pdDoc)
      System.out.println(parsedText);
    } catch {
      case e: IOException => e.printStackTrace
    } 
  }
  
	def read() : String = {

    null
  }
}