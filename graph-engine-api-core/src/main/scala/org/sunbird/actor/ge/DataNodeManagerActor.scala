package org.sunbird.actor.ge

import org.sunbird.actor.core.BaseAPIActor
import org.sunbird.actor.ge.DefinitionManagerActor.{getRequestBody, invalidAPIResponseSerialized, readDefinition, saveDefinition, sender}
import org.sunbird.api.{APIIds, Request}

/**
  * @author Mahesh Kumar Gangula
  */

object DataNodeManagerActor extends BaseAPIActor {

  override def onReceive(apiRequest: Request): Unit = {
    val result = apiRequest.apiId match {
      case APIIds.DATANODE_CREATE =>
        createDataNode(apiRequest);
      case APIIds.DATANODE_READ =>
        readDataNode(apiRequest);
      case _ =>
        invalidAPIResponseSerialized(apiRequest.apiId);
    }
    sender() ! result;
  }

  private def createDataNode(apiRequest: Request): String = {
    val objectType = "teacher";
    val body = getRequestBody(apiRequest);
    val teacherMap =body.request.getOrElse(objectType, Map()).asInstanceOf[Map[String, AnyRef]];
    ""

  }

  private def readDataNode(apiRequest: Request): String = {
    ""
  }

}
