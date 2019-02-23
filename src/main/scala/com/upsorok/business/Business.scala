package com.upsorok.business

import java.util.UUID

import com.upsorok.address.Address
import com.upsorok.datastore.WithUUID

case class Business(uuid: Option[UUID], name: String, address: Address) extends WithUUID[Business] {
  override def withUUID(uuid: UUID): Business = copy(uuid = Some(uuid))
}
