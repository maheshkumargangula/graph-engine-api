package controllers

import play.api.mvc._
import play.api.libs.json._

import scala.concurrent.Future
import javax.inject.Singleton
import javax.inject.Inject
import akka.actor.ActorSystem
import akka.pattern._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import akka.actor.Props
import akka.routing.FromConfig
import org.sunbird.actor.ge.DefinitionManagerActor
import org.sunbird.api.{APIIds, Request}

/**
 * @author mahesh
 */

@Singleton
class Application @Inject() (system: ActorSystem) extends BaseController {
	implicit val className = "controllers.Application";

	val definitionActor = system.actorOf(Props(DefinitionManagerActor).withRouter(FromConfig), name = "definitionManagerActor")

	def checkAPIhealth() = Action.async { implicit request =>
    	val result = Future { """{"result": "successful"}""" };
			result.map(x => Ok(x).withHeaders(CONTENT_TYPE -> "application/json"))
	}

	def saveDefinition() = Action.async { implicit request =>
		val body: String = Json.stringify(request.body.asJson.get);
		val result = ask(definitionActor, Request(APIIds.DEFINITION_SAVE, Some(body))).mapTo[String];
		result.map(response => Ok(response).withHeaders(CONTENT_TYPE -> "application/json"));
	}

	def readDefinition(objectType: String) = Action.async { implicit request =>
    val result = ask(definitionActor, Request(APIIds.DEFINITION_READ, None, Some(Map("objectType" -> objectType)))).mapTo[String];
    result.map(response => Ok(response).withHeaders(CONTENT_TYPE -> "application/json"));
	}
}