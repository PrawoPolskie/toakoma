package pl.mojepanstwo.sap.toakoma

import org.scalamock.scalatest._
import org.scalatest._

abstract class UnitSpec extends FlatSpec with Matchers
                                         with OptionValues
                                         with Inside
                                         with Inspectors
                                         with MockFactory
