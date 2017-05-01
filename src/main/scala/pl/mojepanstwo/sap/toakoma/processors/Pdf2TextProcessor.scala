package pl.mojepanstwo.sap.toakoma.processors

import java.io.{File, FileReader}

import org.apache.pdfbox.cos.COSDocument
import org.apache.pdfbox.io.RandomAccessFile
import org.apache.pdfbox.pdfparser.PDFParser
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.springframework.batch.item.ItemProcessor
import pl.mojepanstwo.sap.toakoma.IsapModel

import sys.process._

class Pdf2TextProcessor extends ItemProcessor[IsapModel, IsapModel] {

  override def process(item:IsapModel): IsapModel = {

    item.links.foreach { case (key, filePath) =>
      val fileName = new File(filePath).getName.replaceAll("\\.[^.]*$", "")
      val dir = new File(System.getProperty("java.io.tmpdir") + "/" + fileName)
      dir.mkdir
      val cmd = "pdf2htmlEX " +
                "--fit-width 900 " +
                "--embed-css 0 " +
                "--css-filename output.css " +
                "--embed-font 0 " +
                "--embed-image 0 " +
                "--embed-javascript 0 " +
                "--optimize-text 0 " +
                "--embed-outline 0 " +
                "--process-outline 0 " +
                "--dest-dir " + dir + " " +
                filePath + " " +
                "output.html"
      cmd !

      var pdDoc: PDDocument = null
      var cosDoc: COSDocument = null
      val file: File = new File(filePath)
      try {
        val pdfStripper: PDFTextStripper = new PDFTextStripper()
        val parser: PDFParser = new PDFParser(new RandomAccessFile(file, "rw"))
        parser.parse()
        cosDoc = parser.getDocument()
        pdDoc = new PDDocument(cosDoc)
        val parsedText: String = pdfStripper.getText(pdDoc)
        item.texts += (key -> parsedText)
      } catch {
        case e: Exception => e.printStackTrace
      }
    }

    item
  }
}
