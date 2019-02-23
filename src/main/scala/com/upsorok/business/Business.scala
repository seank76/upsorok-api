package com.upsorok.business

import java.util.UUID

import com.upsorok.address.Address

case class Business(uuid: UUID, name: String, address: Address)
