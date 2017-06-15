package pl.mojepanstwo.sap.toakoma

import org.scalatest._

abstract class IsapSpec extends FlatSpec with Matchers
                                         with OptionValues
                                         with Inside
                                         with Inspectors