package com.upsorok.address

case class Address(address1: String, address2: Option[String], city: String, state: USState, country: Country)
