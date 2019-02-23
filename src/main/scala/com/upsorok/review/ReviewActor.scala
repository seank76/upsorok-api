package com.upsorok.review

import java.time.Instant
import java.util
import java.util.UUID

import akka.actor.{Actor, ActorLogging, Props}
import com.upsorok.address.{Address, Country, USState}
import com.upsorok.business.Business
import com.upsorok.datastore.ReviewDataStore
import com.upsorok.exception.ItemNotFoundException
import com.upsorok.user.{Author, Name}

import scala.util.{Failure, Success, Try}

object ReviewActor {
  final case class GetReview(uuid: UUID)
  final case class SearchReview(author: Option[Author], location: Option[Address])
  final case class SaveReview(review: Review)

  def props: Props = Props[ReviewActor]
}

class ReviewActor extends Actor with ActorLogging {

  import ReviewActor._

  def receive: Receive = {
    case GetReview(uuid) => sender() ! loadReview(uuid)
    case SaveReview(review) => sender() ! saveReview(review)
    case SearchReview(Some(author), None) =>
    case SearchReview(None, Some(location)) =>
    case SearchReview(Some(author), Some(location)) =>
    case SearchReview(None, None) =>
  }

  private def loadReview(uuid: UUID): Try[Review] = {
    ReviewDataStore.get(uuid)
      .map(review => Success(review))
      .getOrElse(Failure(ItemNotFoundException("Could not find review " + uuid)))
  }

  private def saveReview(review: Review): Try[String] = {
    ReviewDataStore.save(review)
        .map(uuid => "Successfully added review " + uuid)
  }
}