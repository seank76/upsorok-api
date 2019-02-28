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
import com.upsorok.business.BusinessActor.{GetAllBusinesses, GetBusiness, SaveBusiness}

import scala.concurrent.{ExecutionContext, Future}
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
          val resp: Future[(StatusCode, Option[Business])] =
            (businessActor ? GetBusiness(uuid)).mapTo[Try[Business]]
                .map(_ match {
                  case Success(business) => (StatusCodes.OK, Some(business))
                  case Failure(ex) => (StatusCodes.NotFound, None)
                })
          complete(resp)
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
          entity(as[SaveBusiness]) { business =>
            val resp: Future[(StatusCode, String)] =
              (businessActor ? business).mapTo[Try[String]]
                  .map(_ match {
                    case Success(msg) => (StatusCodes.OK, msg)
                    case Failure(ex) => (StatusCodes.BadRequest, ex.getMessage)
                  })
            complete(resp)
          }
        }
      }
    )
}
