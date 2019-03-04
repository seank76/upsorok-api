package com.upsorok.user

import akka.actor._
import akka.event.Logging
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.{get, post}
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.pattern.ask
import akka.util.Timeout
import com.upsorok.JsonSupport
import com.upsorok.exception.AuthenticationFailedException
import com.upsorok.user.UserActor._

import scala.concurrent.{ExecutionContext, Future, Promise}

//#user-routes-class
trait UserRoutes extends JsonSupport {
  //#user-routes-class

  // we leave these abstract, since they will be provided by the App
  implicit def system: ActorSystem
  implicit def executionContext: ExecutionContext

  private lazy val log = Logging(system, classOf[UserRoutes])

  // other dependencies that UserRoutes use
  def userActor: ActorRef

  // Required by the `ask` (?) method below
  implicit def timeout: Timeout

  //#all-routes
  //#users-get-post
  //#users-get-delete
  lazy val userRoutes: Route =
  concat(
    path("users") {
      get {
        val promise = Promise[Users]()
        userActor ! GetUsers(promise)
        complete(promise.future)
      }
    },
    path("add_user") {
      post {
        entity(as[User]) { user =>
          val promise = Promise[User]()
          userActor ! CreateUser(promise, user)

          complete(promise.future)
        }
      }
    },
    path("user" / JavaUUID) { uuid =>
      get {
        val promise = Promise[User]()
        userActor ! GetUser(promise, uuid)
        complete(promise.future)
      }
    },
    path("login") {
      post {
        entity(as[Login]) { login =>
          val fut: Future[(StatusCode, Option[Session])] = (userActor ? Authenticate(login)).map {
            case session: Session => (StatusCodes.OK, Some(session))
            case AuthenticationFailedException(_) => (StatusCodes.Unauthorized, None)
          }
          complete(fut)
        }
      }
    }
  )
  //#all-routes
}
