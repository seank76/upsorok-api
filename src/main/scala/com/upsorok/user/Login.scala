package com.upsorok.user

import java.time.Instant
import java.util.UUID

import com.upsorok.datastore.WithUUID

case class Login(email: Email, password: Password)

case class Session(session_uuid: UUID, user: User, startTime: Instant, endTime: Instant) extends WithUUID[Session] {
  override def uuid: Option[UUID] = Some(session_uuid)
  override def withUUID(uuid: UUID): Session = copy(session_uuid = uuid)
}
