package com.upsorok.exception

import java.util.UUID

case class ReviewNotFoundException(uuid: UUID) extends Exception(s"Review ${uuid} is not found")

case class BusinessNotFoundException(uuid: UUID) extends Exception(s"Business ${uuid} is not found")

case class UserNotFoundException(uuid: UUID) extends Exception(s"User ${uuid} is not found")

case class FailedToSaveBusinessException(name: String) extends Exception(s"Could not save business ${name}")

case class AuthenticationFailedException(msg: String) extends Exception("Authentication failed: " + msg)
