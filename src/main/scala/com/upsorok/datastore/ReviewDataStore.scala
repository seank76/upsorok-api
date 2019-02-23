package com.upsorok.datastore

import java.util.UUID

import com.upsorok.review.Review

import scala.util.{Success, Try}

object ReviewDataStore {
  val reviews = collection.mutable.Map[UUID, Review]()

  def get(uuid: UUID): Option[Review] = {
    reviews.get(uuid)
  }

  def save(review: Review): Try[UUID] = {
    val uuid = UUID.randomUUID();
    reviews.put(uuid, review.copy(uuid = uuid))
    Success(uuid)
  }
}
