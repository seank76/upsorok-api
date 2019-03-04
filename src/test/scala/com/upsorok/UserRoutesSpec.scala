package com.upsorok

//#user-routes-spec
//#test-top
import akka.actor.ActorRef
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.Timeout
import com.upsorok.datastore.DataStoreHub
import com.upsorok.user.{UserActor, UserRoutes}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

//#set-up
class UserRoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest
    with UserRoutes {

  implicit override val timeout: Timeout = Timeout(5.seconds)
  implicit override val executionContext: ExecutionContext = system.dispatcher

  lazy val dataSource = new DataStoreHub()(executionContext)
  // Here we need to implement all the abstract members of UserRoutes.
  // We use the real UserRegistryActor to test it while we hit the Routes, 
  // but we could "mock" it by implementing it in-place or by using a TestProbe() 
  override val userActor: ActorRef =
    system.actorOf(UserActor.props(dataSource)(executionContext), "userRegistry")

  lazy val routes = userRoutes


}
//#set-up
//#user-routes-spec
