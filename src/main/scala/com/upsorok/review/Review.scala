package com.upsorok.review

import java.time.Instant
import java.util.UUID

import com.upsorok.business.Business
import com.upsorok.datastore.WithUUID
import com.upsorok.user.User

case class Review(
  uuid: Option[UUID],
  author: User,
  business: Business,
  visitedDate: Instant,
  text: String,
  created: Instant,
  updated: Instant) extends WithUUID[Review] {

  override def withUUID(uuid: UUID): Review = copy(uuid = Some(uuid))
}

case class Reviews(reviews: Seq[Review])
