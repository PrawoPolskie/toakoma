package pl.mojepanstwo.sap.toakoma.processors

import java.io.File

import org.apache.pdfbox.cos.COSDocument
import org.apache.pdfbox.io.RandomAccessFile
import org.apache.pdfbox.pdfparser.PDFParser
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.springframework.batch.item.ItemProcessor
import pl.mojepanstwo.sap.toakoma.IsapModel

import scala.util.control.Breaks._


class Pdf2TxtProcessor extends ItemProcessor[IsapModel, IsapModel] {

  override def process(item:IsapModel): IsapModel = {

    item.linksPdf.foreach { case (key, filePath) =>
      breakable {
        if(filePath == null) break

        var pdDoc: PDDocument = null
        var cosDoc: COSDocument = null
        val file: File = new File(filePath)
        try {
          val pdfStripper: PDFTextStripper = new PDFTextStripper()
          val parser: PDFParser = new PDFParser(new RandomAccessFile(file, "rw"))
          parser.parse()
          cosDoc = parser.getDocument()
          if(cosDoc.isEncrypted)
            item.encrypted += (key -> true)
          else {
            item.encrypted += (key -> false)
            pdDoc = new PDDocument(cosDoc)
            val parsedText: String = pdfStripper.getText(pdDoc)
            item.texts += (key -> parsedText)
          }
        } catch {
          case e: Exception => e.printStackTrace
        }
      }
    }

    item
  }
}
