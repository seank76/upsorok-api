package com.upsorok.review

import java.time.Instant
import java.util.UUID

import akka.actor.{Actor, ActorLogging, Props}
import com.upsorok.address.Address
import com.upsorok.exception.{BusinessNotFoundException, ReviewNotFoundException, UserNotFoundException}
import com.upsorok.user.User

import scala.util.{Failure, Success, Try}

object ReviewActor {
  final case class GetReview(uuid: UUID)
  final case class SearchReview(author: Option[User], location: Option[Address])
  final case class SaveReview(authorUUID: UUID, businessUUID: UUID, review: String, visitedDate: Instant)

  def props: Props = Props[ReviewActor]
}

class ReviewActor extends Actor with ActorLogging {

  import ReviewActor._
  import com.upsorok.datastore.DataStore._

  def receive: Receive = {
    case GetReview(uuid) => sender() ! loadReview(uuid)
    case SaveReview(authorUUID, businessUUID, review, visitedDate) =>
      sender() ! saveReview(authorUUID, businessUUID, review, visitedDate)
    case SearchReview(Some(author), None) =>
    case SearchReview(None, Some(location)) =>
    case SearchReview(Some(author), Some(location)) =>
    case SearchReview(None, None) =>
  }

  private def loadReview(uuid: UUID): Try[Review] = {
    reviewDataStore.get(uuid)
      .map(review => Success(review))
      .getOrElse(Failure(ReviewNotFoundException(uuid)))
  }

  private def saveReview(authorUUID: UUID, businessUUID: UUID, reviewText: String, visitedDate: Instant): Try[String] = {
    userDataStore.get(authorUUID).map(author => {
      businessDataStore.get(businessUUID).map(business => {
        reviewDataStore.save(Review(None, author, business, visitedDate, reviewText, Instant.now(), Instant.now()))
          .map(uuid => "Successfully added review " + uuid)
      }).getOrElse(Failure(BusinessNotFoundException(businessUUID)))
    }).getOrElse(Failure(UserNotFoundException(authorUUID)))
  }
}