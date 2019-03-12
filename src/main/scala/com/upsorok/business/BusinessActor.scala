package com.upsorok.business

import java.util.UUID

import akka.actor.{Actor, ActorLogging, Props}
import com.upsorok.address.Address
import com.upsorok.datastore.DataStoreHub

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Success, Try}

object BusinessActor {
  final case class GetBusiness(promise: Promise[Business], uuid: UUID)
  final case class GetAllBusinesses(promise: Promise[Businesses])
  final case class SearchBusiness(name: Option[String], address: Option[Address])
  final case class SaveBusiness(name: String, address: Address)
  final case class SaveBusinessWithPromise(promise: Promise[Business], saveBusiness: SaveBusiness)

  def props(dataStore: DataStoreHub)(implicit executionContext: ExecutionContext): Props =
    Props(classOf[BusinessActor], dataStore, executionContext)
}

class BusinessActor(dataStore: DataStoreHub, implicit val executionContext: ExecutionContext)
  extends Actor with ActorLogging {

  import BusinessActor._

  def receive: Receive = {
    case GetBusiness(promise, uuid) => loadBusiness(uuid).map(business =>
      promise.complete(Success(business)))
      .recover {
        case ex => promise.failure(ex)
      }
    case GetAllBusinesses(promise) => getAllBusinesses().map(ab => promise.complete(Success(ab)))
        .recover {
          case ex => promise.failure(ex)
        }
    case SaveBusinessWithPromise(promise, sb) =>
      saveBusiness(sb).map(business => promise.complete(Success(business)))
        .recover {
          case ex => promise.failure(ex)
        }
    case SearchBusiness(Some(name), None) =>
    case SearchBusiness(None, Some(address)) =>
    case SearchBusiness(Some(name), Some(address)) =>
    case SearchBusiness(None, None) =>
  }

  private def loadBusiness(uuid: UUID): Future[Business] = {
    dataStore.businessDataStore.get(uuid)
  }

  private def saveBusiness(sb: SaveBusiness): Future[Business] = {
    dataStore.businessDataStore.save(Business(None, sb.name, sb.address))
  }

  private def getAllBusinesses(): Future[Businesses] = {
    dataStore.businessDataStore.getAll().map(businesses =>
      Businesses(businesses.toSeq))
  }
}