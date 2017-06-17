package pl.mojepanstwo.sap.toakoma.processors

import pl.mojepanstwo.sap.toakoma._
import org.jsoup.Jsoup
import scala.io.Source
import java.util.Date
import java.text.SimpleDateFormat
import pl.mojepanstwo.sap.toakoma.readers.IsapReader

class IsapProcessorSpec extends UnitSpec {

  "A IsapProcessor" should "parse html" in {
    val document = new IsapReader("WDU20170000001").read
    val model = new IsapProcessor().process(document)

    assert(model.id            == "Dz.U. 2017 poz. 1")
    assert(model.dziennik.name == "DZIENNIK_USTAW")
    assert(model.year          == "2017")
    assert(model.dataOgloszenia.compareTo(new SimpleDateFormat("yyyy-MM-dd").parse("2017-01-02")) == 0)
    val ape = model.aktyPowiazane.keysIterator
    var ap = ape.next
    assert(ap.name == "PODSTAWA_PRAWNA")
    assert(model.aktyPowiazane.get(ap).get(0).tytul == "Ustawa z dnia 28 kwietnia 2011 r. o systemie informacji w ochronie zdrowia")
    ap = ape.next
    assert(ap.name == "ODESLANIA")
    assert(model.aktyPowiazane.get(ap).get(0).status.isap == "akt posiada tekst jednolity")
    ap = ape.next
    assert(ap.name == "PODSTAWA_PRAWNA_Z_ART")
    assert(model.aktyPowiazane.get(ap).get(0).adres_publikacyjny == "Dz.U. 2011 nr 113 poz. 657 art. 30a ust. 5")
    assert(model.dataUchylenia == null)
    assert(model.dataWejsciaWZycie.compareTo(new SimpleDateFormat("yyyy-MM-dd").parse("2017-01-03")) == 0)
    assert(model.dataWydania.compareTo(new SimpleDateFormat("yyyy-MM-dd").parse("2016-12-23")) == 0)
    assert(model.dataWygasniecia == null)
    assert(model.linksPdf.get(Pdf.TEKST_OGLOSZONY) == Some("/tmp/D20170001.pdf"))
    assert(model.number == null)
    assert(model.organUprawniony.size == 0)
    assert(model.organWydajacy(0).isap == "MIN. ZDROWIA")
    assert(model.organZobowiazany.size == 0)
    assert(model.position == "1")
    assert(model.statusAktuPrawnego.isap == "obowiązujący")
    assert(model.title == "Rozporządzenie Ministra Zdrowia z dnia 23 grudnia 2016 r. w sprawie minimalnej funkcjonalności Systemu Obsługi List Refundacyjnych")
    assert(model.uwagi == null)
  }

}