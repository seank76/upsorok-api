package com.upsorok.user

import java.time.Instant
import java.util.UUID

case class Login(email: Email, password: Password)

case class Session(user: User, startTime: Instant, endTime: Instant, uuid: UUID)
