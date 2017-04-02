package pl.mojepanstwo.sap.toakoma.readers

import java.util.Date

object Dziennik extends Enumeration {
  type Dziennik = Value

  val DZIENNIK_USTAW = Value("Dz.U.")
  val MONITOR_POLSKI = Value("M.P.")
}

object StatusAktuPrawnego extends Enumeration {
  type StatusAktuPrawnego = Value

  val WYGASNIECIE_AKTU            = Value("wygaśnięcie aktu")
  val OBOWIAZUJACY                = Value("obowiązujący")
  val AKT_POSIADA_TEKST_JEDNOLITY = Value("akt posiada tekst jednolity")
}

object Organ extends Enumeration {
  type Organ = Value

  val MARSZAŁEK_SEJMU              = Value("MARSZAŁEK SEJMU")
  val RADA_MINISTRÓW               = Value("RADA MINISTRÓW")
  val SEJM                         = Value("SEJM")
  val MIN_OBRONY_NARODOWEJ         = Value("MIN. OBRONY NARODOWEJ")
  val MIN_WLASCIWY_DS_WEWNETRZNYCH = Value("MIN. WŁAŚCIWY DS WEWNĘTRZNYCH")
  val MIN_ROZWOJU_I_FINANSÓW       = Value("MIN. ROZWOJU I FINANSÓW")
}

object AktPowiazanyTyp extends Enumeration {
  type AktPowiazanyTyp = Value

  val AKTY_ZMIENIONE          = Value("Akty zmienione")
  val AKTY_WYKONAWCZE         = Value("Akty wykonawcze")
  val AKTY_ZMIENIAJACE        = Value("Akty zmieniające")
  val AKTY_UCHYLONE           = Value("Akty uchylone")
  val ORZECZENIE_TK           = Value("Orzeczenie TK")
  val AKTY_UZNANE_ZA_UCHYLONE = Value("Akty uznane za uchylone")
  val INFORMACJA_O_TEKSCIE_JEDNOLITYM = Value("Informacja o tekście jednolitym")
  val PODSTAWA_PRAWNA_Z_ART   = Value("Podstawa prawna z art.")
  val UCHYLENIA_WYNIKAJACE_Z  = Value("Uchylenia wynikające z")
  val PODSTAWA_PRAWNA         = Value("Podstawa prawna")
  val ODESLANIA               = Value("Odesłania")
  val DYREKTYWY_EUROPEJSKIE   = Value("Dyrektywy europejskie")
}

class AktPowiazany {

}

class IsapModel {
  var id: String = _
  var dziennik: Dziennik.Value = _
  var year: String = _
  var number: String = _
  var position: String = _

  var isapUrl: String = _

  var title: String = _

  var linkTekstAktu: String = _
  var linkTekstOgloszony: String = _
  var linkTekstUjednolicony: String = _

  var statusAktuPrawnego: StatusAktuPrawnego.Value = _
  var dataOgloszenia: Date = _
  var dataWydania: Date = _
  var dataWejsciaWZycie: Date = _
  var dataWygasniecia: Date = _
  var organWydajacy: Organ.Value = _
  var organZobowiązany: Organ.Value = _

  var aktyPowiazane: Array[AktPowiazany] = Array()
}
