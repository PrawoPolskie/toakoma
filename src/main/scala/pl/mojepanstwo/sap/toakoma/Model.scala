package pl.mojepanstwo.sap.toakoma

import java.util.Date

import scala.collection.mutable.ArraySeq
import scala.collection.mutable.Map
import scala.io.Source
import scala.reflect.runtime.universe._

trait Enum[A <: {def name: String}] {
  trait Value { self: A => _values :+= this }
  private var _values = List.empty[A]
  def values = _values
}

case class Dziennik(
  name: String,
  isap: String,
  eli: String
)

object Dziennik {
  val _values = ObjectCSV().readCSV[Dziennik]("model/Dziennik.csv")
  def get(field:String, value:String) : Dziennik = {
    _values.find { d =>
      d.getClass.getDeclaredFields.find { f =>
        f.setAccessible(true)
        f.getName == field
      }.get.get(d) == value
    }.get
  }
}

object Pdf extends Enumeration {
  val TEKST_AKTU,
      TEKST_OGLOSZONY,
      TEKST_UJEDNOLICONY = Value
}

case class StatusAktuPrawnego(
  name: String,
  isap: String
)

object StatusAktuPrawnego {
  val _values = ObjectCSV().readCSV[StatusAktuPrawnego]("model/StatusAktuPrawnego.csv")
  def get(field:String, value:String) : StatusAktuPrawnego = {
    _values.find { d =>
      d.getClass.getDeclaredFields.find { f =>
        f.setAccessible(true)
        f.getName == field
      }.get.get(d) == value
    }.get
  }
}

case class Organ(
  isap: String
)

object Organ {
  val _values = ObjectCSV().readCSV[Organ]("model/Organ.csv")
  def get(field:String, value:String) : Organ = {
    _values.find { d =>
      d.getClass.getDeclaredFields.find { f =>
        f.setAccessible(true)
        f.getName == field
      }.get.get(d) == value
    }.get
  }
}

case class AktPowiazanyTyp(
  name: String,
  isap: String
)

object AktPowiazanyTyp {
  val _values = ObjectCSV().readCSV[AktPowiazanyTyp]("model/AktPowiazanyTyp.csv")
  def get(field:String, value:String) : AktPowiazanyTyp = {
    _values.find { d =>
      d.getClass.getDeclaredFields.find { f =>
        f.setAccessible(true)
        f.getName == field
      }.get.get(d) == value
    }.get
  }
}

class AktPowiazany {
  var tytul: String = _
  var status: StatusAktuPrawnego = _
  var adres_publikacyjny: String = _
  var id: String = _

  var dyrektywa: String = _
  var data: Date = _
  var eurlex: String = _
}

class Model {
  var id: String = _
  var dziennik: Dziennik = _
  var year: String = _
  var number: String = _
  var position: String = _

  var isapUrl: String = _

  var title: String = _

  var linksPdf: Map[Pdf.Value, Option[String]] = Map()
  var pdf: Pdf.Value = _
  var linkHtml: String = _
  var xmlPath: String = _
  var encrypted: Boolean = false
  var fontSizes: Map[String, Int] = Map()

  var statusAktuPrawnego: StatusAktuPrawnego = _
  var dataOgloszenia: Date = _
  var dataWydania: Date = _
  var dataWejsciaWZycie: Date = _
  var dataWygasniecia: Date = _
  var dataUchylenia: Date = _
  var uwagi: String = _
  var organWydajacy: Array[Organ] = Array()
  var organZobowiazany: Array[Organ] = Array()
  var organUprawniony: Array[Organ] = Array()

  var aktyPowiazane:Map[AktPowiazanyTyp, ArraySeq[AktPowiazany]] = Map()
}
