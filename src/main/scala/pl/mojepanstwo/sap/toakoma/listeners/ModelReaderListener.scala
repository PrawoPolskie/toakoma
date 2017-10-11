package pl.mojepanstwo.sap.toakoma.listeners

import org.springframework.batch.core.ItemReadListener
import pl.mojepanstwo.sap.toakoma.Model

class ModelReaderListener extends ItemReadListener[Model] {

	override def beforeRead = {
	}

	override def afterRead(item : Model) = {
	}

	override def onReadError(ex : Exception) {
		println("ItemReadListener - onReadError")
	}
}