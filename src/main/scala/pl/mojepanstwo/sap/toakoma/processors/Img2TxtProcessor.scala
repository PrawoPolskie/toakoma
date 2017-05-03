package pl.mojepanstwo.sap.toakoma.processors

import org.springframework.batch.item.ItemProcessor
import pl.mojepanstwo.sap.toakoma.IsapModel

import scala.xml.XML

class Img2TxtProcessor extends ItemProcessor[IsapModel, IsapModel] {

  override def process(item:IsapModel): IsapModel = {

    item.linksHtml.foreach { case (key, dirPath) =>
      try {
        val xml = XML.load(new java.io.InputStreamReader(new java.io.FileInputStream(dirPath + "/1.xml"), "UTF-8"))
        XML.save(dirPath + "/2.xml", xml, "UTF-8", true)
      } catch {
        case e: Throwable => println(e.printStackTrace())
      }
    }

    item
  }
}
