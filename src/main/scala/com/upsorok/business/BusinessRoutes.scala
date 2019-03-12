package com.upsorok.business

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.pattern.ask
import akka.util.Timeout
import com.upsorok.JsonSupport
import com.upsorok.business.BusinessActor.{GetAllBusinesses, GetBusiness, SaveBusiness, SaveBusinessWithPromise}
import com.upsorok.user.Session

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success, Try}

trait BusinessRoutes extends JsonSupport {

  implicit def system: ActorSystem

  private lazy val log = Logging(system, classOf[BusinessRoutes])

  // other dependencies that UserRoutes use
  def businessActor: ActorRef

  // Required by the `ask` (?) method below
  implicit def timeout: Timeout

  implicit def executionContext: ExecutionContext

  def businessRoutes(session: Future[Session]): Route =
    concat(
      path("business" / JavaUUID) { uuid =>
        get {
          val fut = session.flatMap(_ => {
            val promise = Promise[Business]()
            businessActor ! GetBusiness(promise, uuid)
            promise.future
          })
          complete(fut)
        }
      },
      path("businesses") {
        get {
          val fut = session.flatMap(_ => {
            val promise = Promise[Businesses]()
            businessActor ! GetAllBusinesses(promise)
            promise.future
          })
          complete(fut)
        }
      },
      path( "add_business") {
        post {
          entity(as[SaveBusiness]) { sb =>
            val fut = session.flatMap(_ => {
              val promise = Promise[Business]()
              businessActor ! SaveBusinessWithPromise(promise, sb)
              promise.future
            })
            complete(fut)
          }
        }
      }
    )
}
