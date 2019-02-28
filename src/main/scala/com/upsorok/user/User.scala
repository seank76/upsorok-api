package com.upsorok.user

import java.util.UUID

import com.upsorok.datastore.WithUUID
import enumeratum._

sealed trait UserType extends EnumEntry

object UserType extends Enum[UserType] {
  val values = findValues

  case object Admin extends UserType
  case object Author extends UserType
  case object Reader extends UserType
}

case class User(uuid: Option[UUID],
                name: Name,
                userTypes: Set[UserType] = Set()) extends WithUUID[User] {

  override def withUUID(uuid: UUID): User = copy(uuid = Some(uuid))

  def withType(userType: UserType): User = copy(userTypes = userTypes + userType)
}
