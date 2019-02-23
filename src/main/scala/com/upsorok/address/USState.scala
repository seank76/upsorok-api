package com.upsorok.address

import enumeratum._

sealed class USState(val short: String, val long: String) extends EnumEntry

object USState extends Enum[USState] {

  val values = findValues

  case object AK extends USState("AK", "Alaska")

  case object AL extends USState("AL", "Alabama")

  case object AR extends USState("AR", "Arkansas")

  case object AZ extends USState("AZ", "Arizona")

  case object CA extends USState("CA", "California")

  case object CO extends USState("CO", "Colorado")

  case object CT extends USState("CT", "Connecticut")

  case object DE extends USState("DE", "Delaware")

  case object FL extends USState("FL", "Florida")

  case object GA extends USState("GA", "Georgia")

  case object HI extends USState("HI", "Hawaii")

  case object IA extends USState("IA", "Iowa")

  case object ID extends USState("ID", "Idaho")

  case object IL extends USState("IL", "Illinois")

  case object IN extends USState("IN", "Indiana")

  case object KS extends USState("KS", "Kansas")

  case object KY extends USState("KY", "Kentucky")

  case object LA extends USState("LA", "Louisiana")

  case object MA extends USState("MA", "Massachusetts")

  case object MD extends USState("MD", "Maryland")

  case object ME extends USState("ME", "Maine")

  case object MI extends USState("MI", "Michigan")

  case object MN extends USState("MN", "Minnesota")

  case object MO extends USState("MO", "Missouri")

  case object MS extends USState("MS", "Mississippi")

  case object MT extends USState("MT", "Montana")

  case object NC extends USState("NC", "North Carolina")

  case object ND extends USState("ND", "North Dakota")

  case object NE extends USState("NE", "Nebraska")

  case object NH extends USState("NH", "New Hampshire")

  case object NJ extends USState("NJ", "New Jersey")

  case object NM extends USState("NM", "New Mexico")

  case object NV extends USState("NV", "Nevada")

  case object NY extends USState("NY", "New York")

  case object OH extends USState("OH", "Ohio")

  case object OK extends USState("OK", "Oklahoma")

  case object OR extends USState("OR", "Oregon")

  case object PA extends USState("PA", "Pennsylvania")

  case object RI extends USState("RI", "Rhode Island")

  case object SC extends USState("SC", "South Carolina")

  case object SD extends USState("SD", "South Dakota")

  case object TN extends USState("TN", "Tennessee")

  case object TX extends USState("TX", "Texas")

  case object UT extends USState("UT", "Utah")

  case object VA extends USState("VA", "Virginia")

  case object VT extends USState("VT", "Vermont")

  case object WA extends USState("WA", "Washington")

  case object WI extends USState("WI", "Wisconsin")

  case object WV extends USState("WV", "West Virginia")

  case object WY extends USState("WY", "Wyoming")

}