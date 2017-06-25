package pl.mojepanstwo.sap.toakoma.processors

import java.io.File

import org.apache.pdfbox.cos.COSDocument
import org.apache.pdfbox.io.RandomAccessFile
import org.apache.pdfbox.pdfparser.PDFParser
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.springframework.batch.item.ItemProcessor
import pl.mojepanstwo.sap.toakoma._

import scala.util.control.Breaks._


class PdfCheckEncryptionProcessor extends ItemProcessor[Model, Model] {

  override def process(item:Model): Model = {

    item.linksPdf.foreach { case (key, filePath) =>
      breakable {
        if(filePath.isEmpty) break

        var pdDoc: PDDocument = null
        var cosDoc: COSDocument = null
        val file: File = new File(filePath.get)
        try {
          val pdfStripper: PDFTextStripper = new PDFTextStripper
          val parser: PDFParser = new PDFParser(new RandomAccessFile(file, "rw"))
          cosDoc = parser.getDocument
          item.encrypted += (key -> cosDoc.isEncrypted)
        } catch {
          case e: Exception => e.printStackTrace
        }
      }
    }

    item
  }
}
