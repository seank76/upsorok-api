package com.upsorok.datastore

import java.util.UUID

import com.upsorok.business.Business
import com.upsorok.exception.{BusinessNotFoundException, ReviewNotFoundException, SessionNotFoundException, UserNotFoundException}
import com.upsorok.review.Review
import com.upsorok.user.{Email, Session, User}

import scala.concurrent.{ExecutionContext, Future}

trait WithUUID[T <: WithUUID[T]] {
  def uuid: Option[UUID]
  def withUUID(uuid: UUID): T
}

abstract class DataStore[T <: WithUUID[T]](implicit val executionContext: ExecutionContext) {

  val store = collection.mutable.Map[UUID, T]()

  def notFoundException(uuid: UUID): Exception

  def get(uuid: UUID): Future[T] = {
    store.get(uuid).map(Future(_))
      .getOrElse(Future.failed(notFoundException(uuid)))
  }

  def getAll(): Future[Iterable[T]] = {
    Future(store.values)
  }

  def save(entry: T): Future[T] = {
    val newEntity = if (entry.uuid.isDefined) {
      entry
    } else {
      entry.withUUID(UUID.randomUUID())
    }

    store.put(newEntity.uuid.get, newEntity)
    Future(newEntity)
  }

  def delete(uuid: UUID): Future[Boolean] = {
    Future(store.remove(uuid).isDefined)
  }
}

class DataStoreHub(implicit val executionContext: ExecutionContext) {
  val reviewDataStore = new DataStore[Review] {
    override def notFoundException(uuid: UUID): Exception = ReviewNotFoundException(uuid)
  }

  val userDataStore = new DataStore[User] {

    override def notFoundException(uuid: UUID): Exception = UserNotFoundException(uuid)

    def getByEmail(email: Email): Future[Option[User]] = {
      Future(store.values.filter(_.email == email).headOption)
    }
  }

  val businessDataStore = new DataStore[Business] {
    override def notFoundException(uuid: UUID): Exception = BusinessNotFoundException(uuid)
  }

  val sessionDataStore = new DataStore[Session] {
    override def notFoundException(uuid: UUID): Exception = SessionNotFoundException(uuid)
  }
}
