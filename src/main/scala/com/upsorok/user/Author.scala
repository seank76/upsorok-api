package com.upsorok.user

import java.util.UUID

import com.upsorok.datastore.WithUUID

case class Author(
  uuid: Option[UUID],
  name: Name) extends WithUUID[Author] {

  override def withUUID(uuid: UUID): Author = copy(uuid = Some(uuid))
}
