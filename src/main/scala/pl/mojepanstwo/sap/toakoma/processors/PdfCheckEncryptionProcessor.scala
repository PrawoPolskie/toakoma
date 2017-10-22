package pl.mojepanstwo.sap.toakoma.processors

import java.io.File

import org.apache.pdfbox.cos.COSDocument
import org.apache.pdfbox.io.RandomAccessFile
import org.apache.pdfbox.pdfparser.PDFParser
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import pl.mojepanstwo.sap.toakoma._

import scala.util.control.Breaks._


class PdfCheckEncryptionProcessor extends Model2ModelProcessor {

  override def process(item:Model): Model = {

    breakable {
      val filePath = item.linksPdf(item.pdf)
      if(filePath.isEmpty) break

      var pdDoc: PDDocument = null
      var cosDoc: COSDocument = null
      val file: File = new File(filePath.get)
      try {
        val pdfStripper: PDFTextStripper = new PDFTextStripper
        val parser: PDFParser = new PDFParser(new RandomAccessFile(file, "rw"))
        cosDoc = parser.getDocument
        item.encrypted = cosDoc.isEncrypted
      } catch {
        case e: Exception => e.printStackTrace
      }
    }

    item
  }
}
