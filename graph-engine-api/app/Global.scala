import akka.actor.Props
import akka.routing.SmallestMailboxPool
import play.api._
import play.api.mvc._
import play.api.mvc.Results._

import scala.concurrent.Future
import com.typesafe.config.Config
import org.ekstep.graph.engine.router.{ActorBootstrap, GraphEngineActorPoolMgr, RequestRouter}

object Global extends WithFilters() {

    override def beforeStart(app: Application) {
        Logger.info("Caching content")
        val config: Config = play.Play.application.configuration.underlying()
        ActorBootstrap.getActorSystem()
        Logger.info("Application has started...")
    }

    override def onStop(app: Application) {
        Logger.info("Application shutdown...")
    }

}