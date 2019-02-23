package com.upsorok.review

import java.time.Instant
import java.util
import java.util.UUID

import akka.actor.{Actor, ActorLogging, Props}
import com.upsorok.address.{Address, Country, USState}
import com.upsorok.business.Business
import com.upsorok.user.{Author, Name}

object ReviewRegistryActor {
  final case class GetReview(uuid: UUID)
  final case class SearchReview(author: Option[Author], location: Option[Address])

  def props: Props = Props[ReviewRegistryActor]
}

class ReviewRegistryActor extends Actor with ActorLogging {

  import ReviewRegistryActor._

  var reviews = new util.HashMap[UUID, Review]()

  def receive: Receive = {
    case GetReview(uuid) => sender() ! loadReview(uuid)
    case SearchReview(Some(author), None) =>
    case SearchReview(None, Some(location)) =>
    case SearchReview(Some(author), Some(location)) =>
    case SearchReview(None, None) =>
  }

  private def loadReview(uuid: UUID): Review = {
    Review(
      uuid,
      Author(UUID.randomUUID(), Name("Test", "M", "Author")),
      Business(UUID.randomUUID(), "Test Business", Address("123 Test st.", "#23", "Wyckoff", USState.NJ, Country.US)),
      Instant.now,
      "I visited the place and love it. Really",
      Instant.now,
      Instant.now
    )
  }
}