package com.upsorok.user

//#user-registry-actor
import java.time.{Duration, Instant}
import java.util.UUID

import akka.actor.{Actor, ActorLogging, Props}
import com.upsorok.datastore.DataStoreHub
import com.upsorok.exception.{AuthenticationFailedException, UserNotFoundException}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success, Try}

final case class Users(users: Seq[User])

object UserActor {
  final case class ActionPerformed(description: String)
  final case class GetUsers(promise: Promise[Users])
  final case class CreateUser(promise: Promise[User], user: User)
  final case class GetUser(promise: Promise[User], uuid: UUID)
  final case class DeleteUser(uuid: UUID)
  final case class Authenticate(login: Login)

  def props(dataStore: DataStoreHub)(implicit executionContext: ExecutionContext): Props =
    Props(classOf[UserActor], dataStore, executionContext)
}

class UserActor(dataStore: DataStoreHub, implicit val executionContext: ExecutionContext)
  extends Actor with ActorLogging {

  import UserActor._

  def receive: Receive = {
    case GetUsers(promise) =>
      dataStore.userDataStore.getAll().map(users => promise.complete(Success(Users(users.toSeq))))
        .recover {
          case ex => promise.failure(ex)
        }

    case CreateUser(promise, user) =>
      dataStore.userDataStore.save(user).map(savedUser => promise.complete(Success(savedUser)))
        .recover {
          case ex => promise.failure(ex)
        }

    case GetUser(promise, uuid) =>
      dataStore.userDataStore.get(uuid).map(user => promise.complete(Success(user)))
        .recover {
          case ex => promise.failure(ex)
        }

    case DeleteUser(uuid) =>
      dataStore.userDataStore.delete(uuid) map {
        case true =>
          sender() ! ActionPerformed(s"User ${uuid} deleted.")
        case false =>
          sender() ! UserNotFoundException(uuid)
      }

    case Authenticate(login) =>
      authenticate(login) map {
        case Success(session) => sender() ! session
        case Failure(ex) => sender() ! AuthenticationFailedException(ex.getMessage)
      }
  }

  private def authenticate(login: Login): Future[Try[Session]] = {
    dataStore.userDataStore.getByEmail(login.email).map(_.map(user => {
      if (user.password == login.password) {
        Success(
          Session(user, Instant.now, Instant.now.plus(Duration.ofDays(14)), UUID.randomUUID())
        )
      } else {
        Failure(AuthenticationFailedException(s"Failed to authenticate ${login.email}"))
      }
    }).getOrElse(Failure(AuthenticationFailedException(s"User with email ${login.email} is not found")))
    ).recover {
      case ex => Failure(ex)
    }
  }
}
