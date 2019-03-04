package com.upsorok.review

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
import com.upsorok.review.ReviewActor._

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success, Try}

trait ReviewRoutes extends JsonSupport {

  implicit def system: ActorSystem

  private lazy val log = Logging(system, classOf[ReviewRoutes])

  // other dependencies that UserRoutes use
  def reviewActor: ActorRef

  // Required by the `ask` (?) method below
  implicit def timeout: Timeout

  implicit def executionContext: ExecutionContext

  lazy val reviewRoutes: Route =
    concat(
      path("review" / JavaUUID) { uuid =>
        get {
          val promise = Promise[Review]()
          reviewActor ! GetReview(promise, uuid)

          complete(promise.future)
        }
      },
      path( "add_review") {
        post {
          entity(as[SaveReview]) { review =>
            val promise = Promise[Review]()
            reviewActor ! SaveReviewWithPromise(promise, review)
            complete(promise.future)
          }
        }
      }
    )
}
