package com.upsorok

//#quick-start-server
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.upsorok.review.{ReviewActor, ReviewRoutes}
import com.upsorok.user.{UserActor, UserRoutes}
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import com.upsorok.business.{BusinessActor, BusinessRoutes}

import scala.concurrent.duration._

//#main-class
object UpSoRokAPIServer extends App
  with UserRoutes with ReviewRoutes with BusinessRoutes {

  // set up ActorSystem and other dependencies here
  //#main-class
  //#server-bootstrapping
  implicit val system: ActorSystem = ActorSystem("upsorokAkkaHttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit override val timeout: Timeout = Timeout(5.seconds) // usually we'd obtain the timeout from the system's configuration
  //#server-bootstrapping

  val userActor: ActorRef = system.actorOf(UserActor.props, "userActor")
  val reviewActor: ActorRef = system.actorOf(ReviewActor.props, "reviewActor")
  val businessActor: ActorRef = system.actorOf(BusinessActor.props, "businessActor")

  //#main-class
  lazy val routes: Route = concat(userRoutes, reviewRoutes, businessRoutes)
  //#main-class

  //#http-server
  val serverBinding: Future[Http.ServerBinding] = Http().bindAndHandle(routes, "localhost", 1818)

  serverBinding.onComplete {
    case Success(bound) =>
      println(s"Server online at http://${bound.localAddress.getHostString}:${bound.localAddress.getPort}/")
    case Failure(e) =>
      Console.err.println(s"Server could not start!")
      e.printStackTrace()
      system.terminate()
  }

  Await.result(system.whenTerminated, Duration.Inf)
  //#http-server
  //#main-class
}
//#main-class
//#quick-start-server
