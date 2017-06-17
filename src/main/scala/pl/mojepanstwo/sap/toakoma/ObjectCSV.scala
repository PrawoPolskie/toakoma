package pl.mojepanstwo.sap.toakoma

import java.io.{FileWriter, Writer}

import com.github.tototoshi.csv._

import scala.reflect.runtime.{universe => ru}
import scala.io.Source

case class Config(header: String = "#",
                  delimiter: Char = defaultCSVFormat.delimiter,
                  quoteChar: Char = defaultCSVFormat.quoteChar,
                  treatEmptyLineAsNil: Boolean = defaultCSVFormat.treatEmptyLineAsNil,
                  escapeChar: Char = defaultCSVFormat.escapeChar,
                  lineTerminator: String = defaultCSVFormat.lineTerminator,
                  quoting: Quoting = defaultCSVFormat.quoting) extends CSVFormat

object ObjectCSV {
  def apply(config: Config = new Config()) = new ObjectCSV(config)
}

protected class ObjectCSV(config: Config) {

  def readCSV[T: ru.TypeTag](resPath: String): IndexedSeq[T] = {
    val objectConverter = new ObjectConverter
    val csvReader = CSVReader.open(Source.fromResource(resPath))(config)
    val data = csvReader.all()
    val header = data.head
    if (!header.head.startsWith(config.header)) {
      throw new Exception("Expected a commented out header. Found: " + header)
    }
    val headerWithoutComments = Array(header.head.substring(1)) ++ header.tail
    val objects = data.view.tail.map(row => objectConverter.toObject[T](row, headerWithoutComments))
    objects.toVector
  }
}