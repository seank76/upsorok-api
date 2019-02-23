package com.upsorok.exception

import java.util.UUID

case class ReviewNotFoundException(uuid: UUID) extends Exception("Review " + uuid + " is not found")

case class BusinessNotFoundException(uuid: UUID) extends Exception("Business " + uuid + " is not found")

case class AuthorNotFoundException(uuid: UUID) extends Exception("Author " + uuid + " is not found")
