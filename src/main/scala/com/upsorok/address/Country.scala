package com.upsorok.address

import enumeratum._

sealed class Country(val short: String, val long: String) extends EnumEntry

object Country extends Enum[Country] {

  val values = findValues

  case object US extends Country("US", "United States")

  case object Canada extends Country("CN", "Canada")

}
