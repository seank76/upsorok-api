package com.upsorok.user

//#user-registry-actor
import java.time.{Duration, Instant}
import java.util.UUID

import akka.actor.{Actor, ActorLogging, Props}
import com.upsorok.datastore.DataStoreHub
import com.upsorok.exception.{AuthenticationFailedException, UserNotFoundException}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.Success

final case class Users(users: Seq[User])

object UserActor {
  final case class ActionPerformed(description: String)
  final case class GetUsers(promise: Promise[Users])
  final case class CreateUser(promise: Promise[User], user: User)
  final case class GetUser(promise: Promise[User], uuid: UUID)
  final case class DeleteUser(uuid: UUID)
  final case class Authenticate(promise: Promise[Session], login: Login)

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

    case Authenticate(promise, login) =>
      authenticate(login).map(session => promise.complete(Success(session)))
          .recover {
            case ex => promise.failure(ex)
          }
  }

  private def authenticate(login: Login): Future[Session] = {
    dataStore.userDataStore.getByEmail(login.email).map(_.flatMap(user => {
      if (user.password == login.password) {
        val session = Session(UUID.randomUUID(), user, Instant.now, Instant.now.plus(Duration.ofDays(14)))
        dataStore.sessionDataStore.save(session)
        Some(session)
      } else {
        None
      }
    }).getOrElse(throw AuthenticationFailedException(s"Authentication failed for ${login.email}")))
  }
}
