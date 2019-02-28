package com.upsorok.user

//#user-registry-actor
import java.util.UUID

import akka.actor.{Actor, ActorLogging, Props}
import com.upsorok.datastore.DataStore
import com.upsorok.exception.UserNotFoundException

final case class Users(users: Seq[User])

object UserActor {
  final case class ActionPerformed(description: String)
  final case object GetUsers
  final case class CreateUser(user: User)
  final case class GetUser(uuid: UUID)
  final case class DeleteUser(uuid: UUID)

  def props: Props = Props[UserActor]
}

class UserActor extends Actor with ActorLogging {
  import UserActor._

  def receive: Receive = {
    case GetUsers =>
      sender() ! Users(DataStore.userDataStore.getAll().toSeq)

    case CreateUser(user) =>
      DataStore.userDataStore.save(user).map(uuid => {
        sender() ! ActionPerformed(s"User ${uuid} saved")
      }).recover {
        case ex => sender() ! ex
      }

    case GetUser(uuid) =>
      DataStore.userDataStore.get(uuid).map(user => {
        sender() ! user
      }).getOrElse {
        sender() ! UserNotFoundException(uuid)
      }

    case DeleteUser(uuid) =>
      if (DataStore.userDataStore.delete(uuid))
        sender() ! ActionPerformed(s"User ${uuid} deleted.")
      else
        sender() ! UserNotFoundException(uuid)
  }
}
