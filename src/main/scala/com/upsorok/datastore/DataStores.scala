package com.upsorok.datastore

import java.util.UUID

import com.upsorok.business.Business
import com.upsorok.review.Review
import com.upsorok.user.User

import scala.util.{Success, Try}

trait WithUUID[T <: WithUUID[T]] {
  def uuid: Option[UUID]
  def withUUID(uuid: UUID): T
}

case class DataStore[T <: WithUUID[T]]() {
  val store = collection.mutable.Map[UUID, T]()

  def get(uuid: UUID): Option[T] = {
    store.get(uuid)
  }

  def getAll(): Iterable[T] = {
    store.values
  }

  def save(entry: T): Try[UUID] = {
    val uuid = UUID.randomUUID();
    store.put(uuid, entry.withUUID(uuid))
    Success(uuid)
  }

  def delete(uuid: UUID): Boolean = {
    store.remove(uuid).isDefined
  }
}

object DataStore {
  val reviewDataStore = DataStore[Review]()
  val userDataStore = DataStore[User]()
  val businessDataStore = DataStore[Business]()
}