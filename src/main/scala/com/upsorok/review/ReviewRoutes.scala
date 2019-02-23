package com.upsorok.review

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.pattern.ask
import akka.util.Timeout
import com.upsorok.JsonSupport
import com.upsorok.review.ReviewActor._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

trait ReviewRoutes extends JsonSupport {

  implicit def system: ActorSystem

  private lazy val log = Logging(system, classOf[ReviewRoutes])

  // other dependencies that UserRoutes use
  def reviewRegistryActor: ActorRef

  // Required by the `ask` (?) method below
  implicit def timeout: Timeout

  implicit def executionContext: ExecutionContext

  lazy val reviewRoutes: Route =
    concat(
      path("review" / JavaUUID) { uuid =>
        get {
          val resp: Future[(StatusCode, Option[Review])] =
            (reviewRegistryActor ? GetReview(uuid)).mapTo[Try[Review]]
                .map(_ match {
                  case Success(review) => (StatusCodes.OK, Some(review))
                  case Failure(ex) => (StatusCodes.BadRequest, None)
                })
          complete(resp)
        }
      },
      path( "add_review") {
        post {
          entity(as[SaveReview]) { review =>
            val resp: Future[(StatusCode, String)] =
              (reviewRegistryActor ? review).mapTo[Try[String]]
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
