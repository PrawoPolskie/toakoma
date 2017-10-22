package pl.mojepanstwo.sap.toakoma.processors

import java.io.File

import org.springframework.batch.item.ItemProcessor
import pl.mojepanstwo.sap.toakoma._

import sys.process._
import util.control.Breaks._


class Pdf2HtmlProcessor extends ItemProcessor[Model, Model] {

  override def process(item:Model): Model = {
    breakable {
      if(item.encrypted) break
      val path = item.linksPdf(item.pdf).get
      println(item.id)
      val dir = new File(System.getProperty("java.io.tmpdir") + "/" + item.id)
      dir.mkdir
      convert(path, dir.getAbsolutePath)
      item.linkHtml = dir.getAbsolutePath
      item.xmlPath = dir.getAbsolutePath + "/output.html"
    }

    item
  }

  def convert(filePath:String, dest:String) = {
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
      "--dest-dir " + dest + " " +
      filePath + " " +
      "output.html"
    cmd !!
  }
}
