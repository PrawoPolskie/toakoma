package pl.mojepanstwo.sap.toakoma.processors

import org.springframework.batch.item.ItemProcessor
import pl.mojepanstwo.sap.toakoma.Model

trait Model2ModelProcessor extends ItemProcessor[Model, Model]
