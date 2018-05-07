package org.sunbird.actor.ge

import org.ekstep.graph.common.enums.GraphEngineParams
import org.ekstep.graph.dac.enums.GraphDACParams
import org.ekstep.graph.engine.router.GraphEngineManagers
import org.sunbird.actor.core.BaseAPIActor
import org.sunbird.api._

import scala.collection.JavaConverters._

/**
  * @author Mahesh Kumar Gangula
  *
  */

object DefinitionManagerActor extends BaseAPIActor {

  val graphId = "domain";

  @throws(classOf[Exception])
  override def onReceive(apiRequest: org.sunbird.api.Request): Unit = {

    val result = apiRequest.apiId match {
      case APIIds.DEFINITION_SAVE =>
        saveDefinition(apiRequest);
      case APIIds.DEFINITION_READ =>
        readDefinition(apiRequest);
      case _ =>
        invalidAPIResponseSerialized(apiRequest.apiId);
    }
    sender() ! result;
  }

  private def saveDefinition(apiRequest: org.sunbird.api.Request): String = {
    val body = getRequestBody(apiRequest);
    val request = getGraphEngineRequest(graphId, GraphEngineManagers.NODE_MANAGER, "importDefinitions");
    val is = body.request;
    request.put(GraphEngineParams.input_stream.name(), JSONUtils.serialize(is));
    getResponse(apiRequest.apiId, request);
  }

  private def readDefinition(apiRequest: org.sunbird.api.Request): String = {
    val objectType = apiRequest.params.getOrElse(Map()).getOrElse("objectType", "");
    println("Object Type: ", objectType);
    val request = getGraphEngineRequest(graphId, GraphEngineManagers.SEARCH_MANAGER, "getNodeDefinition");
    request.put(GraphDACParams.object_type.name(), objectType);
    getResponse(apiRequest.apiId, request);
  }
}
