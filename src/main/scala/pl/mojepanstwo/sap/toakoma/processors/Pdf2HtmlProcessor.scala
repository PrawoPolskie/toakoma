package pl.mojepanstwo.sap.toakoma.processors

import java.io.File

import org.springframework.batch.item.ItemProcessor
import pl.mojepanstwo.sap.toakoma.IsapModel

import sys.process._
import util.control.Breaks._


class Pdf2HtmlProcessor extends ItemProcessor[IsapModel, IsapModel] {

  override def process(item:IsapModel): IsapModel = {

    item.linksPdf.foreach { case (key, filePath) =>
      breakable {
        if(filePath == null) break

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
          "--dest-dir " + dir.getAbsolutePath + " " +
          filePath + " " +
          "output.html"
        cmd !!

        item.linksHtml(key) = dir.getAbsolutePath
      }
    }

    item
  }
}
