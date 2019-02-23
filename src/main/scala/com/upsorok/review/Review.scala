package com.upsorok.review

import java.time.Instant
import java.util.UUID

import com.upsorok.business.Business
import com.upsorok.user.Author

case class Review(
  uuid: UUID,
  author: Author,
  business: Business,
  visitedDate: Instant,
  text: String,
  created: Instant,
  updated: Instant)

case class Reviews(reviews: Seq[Review])
