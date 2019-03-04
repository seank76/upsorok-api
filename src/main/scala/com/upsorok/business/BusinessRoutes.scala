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

  lazy val businessRoutes: Route =
    concat(
      path("business" / JavaUUID) { uuid =>
        get {
          val promise = Promise[Business]()
          businessActor ! GetBusiness(promise, uuid)
          complete(promise.future)
        }
      },
      path("businesses") {
        get {
          val resp: Future[(StatusCode, Option[Businesses])] =
            (businessActor ? GetAllBusinesses).mapTo[Try[Businesses]]
            .map(_ match {
              case Success(businesses) => (StatusCodes.OK, Some(businesses))
              case Failure(ex) => (StatusCodes.NotFound, None)
            })
          complete(resp)
        }
      },
      path( "add_business") {
        post {
          entity(as[SaveBusiness]) { sb =>
            val promise = Promise[Business]()
            businessActor ! SaveBusinessWithPromise(promise, sb)

            complete(promise.future)
          }
        }
      }
    )
}
