package com.upsorok.review

import java.time.Instant
import java.util.UUID

import akka.actor.{Actor, ActorLogging, Props}
import com.upsorok.address.Address
import com.upsorok.datastore.DataStoreHub
import com.upsorok.user.User

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.Success

object ReviewActor {
  final case class GetReview(promise: Promise[Review], uuid: UUID)
  final case class SearchReview(author: Option[User], location: Option[Address])
  final case class SaveReview(authorUUID: UUID, businessUUID: UUID, review: String, visitedDate: Instant)
  final case class SaveReviewWithPromise(promise: Promise[Review], review: SaveReview)

  def props(dataStore: DataStoreHub)(implicit executionContext: ExecutionContext): Props =
    Props(classOf[ReviewActor], dataStore, executionContext)
}

class ReviewActor(dataStore: DataStoreHub, implicit val executionContext: ExecutionContext)
  extends Actor with ActorLogging {

  import ReviewActor._

  def receive: Receive = {
    case GetReview(promise, uuid) => loadReview(uuid).map(review => promise.complete(Success(review)))
        .recover {
          case ex => promise.failure(ex)
        }
    case SaveReviewWithPromise(promise, sr) =>
      saveReview(sr)
        .map(review => promise.complete(Success(review)))
        .recover {
          case ex => promise.failure(ex)
        }
    case SearchReview(Some(author), None) =>
    case SearchReview(None, Some(location)) =>
    case SearchReview(Some(author), Some(location)) =>
    case SearchReview(None, None) =>
  }

  private def loadReview(uuid: UUID): Future[Review] = {
    dataStore.reviewDataStore.get(uuid)
  }

  private def saveReview(saveReview: SaveReview): Future[Review] = {
    dataStore.userDataStore.get(saveReview.authorUUID).flatMap(author =>
      dataStore.businessDataStore.get(saveReview.businessUUID).flatMap(business =>
        dataStore.reviewDataStore.save(
          Review(None, author, business, saveReview.visitedDate, saveReview.review, Instant.now(), Instant.now()))
      )
    )
  }
}
