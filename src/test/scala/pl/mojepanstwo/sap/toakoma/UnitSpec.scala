package pl.mojepanstwo.sap.toakoma

import org.scalatest._

abstract class UnitSpec extends FlatSpec with Matchers
                                         with OptionValues
                                         with Inside
                                         with Inspectors