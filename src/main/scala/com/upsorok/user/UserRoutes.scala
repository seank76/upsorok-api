package com.upsorok.user

import akka.actor._
import akka.event.Logging
import akka.pattern.ask
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.{delete, get, post}
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.util.Timeout
import com.upsorok.JsonSupport
import com.upsorok.exception.UserNotFoundException
import com.upsorok.user.UserActor._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

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
        val users: Future[Users] =
          (userActor ? GetUsers).mapTo[Users]
        complete(users)
      }
    },
    path("add_user") {
      post {
        entity(as[User]) { user =>
          val fut: Future[(StatusCode, String)] = (userActor ? CreateUser(user)).map {
            case ActionPerformed(msg) =>
              (StatusCodes.Created, msg)
            case ex: Exception =>
              (StatusCodes.BadRequest, ex.toString)
          }
          complete(fut)
        }
      }
    },
    path("user" / JavaUUID) { uuid =>
      get {
        val fut: Future[(StatusCode, Option[User])] = (userActor ? GetUser(uuid)).map {
          case user: User => (StatusCodes.OK, Some(user))
          case UserNotFoundException(userUUID) => (StatusCodes.NotFound, None)
        }
        complete(fut)
      }
    }
  )
  //#all-routes
}
