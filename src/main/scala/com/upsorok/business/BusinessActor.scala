package com.upsorok.business

import java.time.Instant
import java.util.UUID

import akka.actor.{Actor, ActorLogging, Props}
import com.upsorok.address.Address
import com.upsorok.datastore.DataStore
import com.upsorok.exception.{BusinessNotFoundException, FailedToSaveBusinessException, ReviewNotFoundException, UserNotFoundException}

import scala.util.{Failure, Success, Try}

object BusinessActor {
  final case class GetBusiness(uuid: UUID)
  final case object GetAllBusinesses
  final case class SearchBusiness(name: Option[String], address: Option[Address])
  final case class SaveBusiness(name: String, address: Address)

  def props: Props = Props[BusinessActor]
}

class BusinessActor extends Actor with ActorLogging {

  import BusinessActor._

  def receive: Receive = {
    case GetBusiness(uuid) => sender() ! loadBusiness(uuid)
    case GetAllBusinesses => sender() ! getAllBusinesses()
    case SaveBusiness(name: String, address: Address) =>
      sender() ! saveBusiness(name, address)
    case SearchBusiness(Some(name), None) =>
    case SearchBusiness(None, Some(address)) =>
    case SearchBusiness(Some(name), Some(address)) =>
    case SearchBusiness(None, None) =>
  }

  private def loadBusiness(uuid: UUID): Try[Business] = {
    DataStore.businessDataStore.get(uuid)
      .map(business => Success(business))
      .getOrElse(Failure(BusinessNotFoundException(uuid)))
  }

  private def saveBusiness(name: String, address: Address): Try[String] = {
    DataStore.businessDataStore.save(Business(None, name, address))
      .map(uuid => Success("Successfully added business " + uuid))
      .getOrElse(Failure(FailedToSaveBusinessException(name)))
  }

  private def getAllBusinesses(): Try[Businesses] = {
    Success(Businesses(DataStore.businessDataStore.getAll().toSeq))
  }
}