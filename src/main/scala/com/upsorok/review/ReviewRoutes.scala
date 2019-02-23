package com.upsorok.review

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.pattern.ask
import akka.util.Timeout
import com.upsorok.JsonSupport
import com.upsorok.review.ReviewRegistryActor._

import scala.concurrent.Future

trait ReviewRoutes extends JsonSupport {

  implicit def system: ActorSystem

  private lazy val log = Logging(system, classOf[ReviewRoutes])

  // other dependencies that UserRoutes use
  def reviewRegistryActor: ActorRef

  // Required by the `ask` (?) method below
  implicit def timeout: Timeout

  lazy val reviewRoutes: Route =
    path("review" / JavaUUID) { uuid =>
      get {
        val review: Future[Review] =
          (reviewRegistryActor ? GetReview(uuid)).mapTo[Review]
        complete(review)
      }
    }
}
