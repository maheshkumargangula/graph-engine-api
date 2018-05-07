package org.sunbird.actor.core

import java.util.UUID

import akka.actor.UntypedActor
import akka.pattern.Patterns
import org.ekstep.graph.common.enums.GraphHeaderParams
import org.ekstep.graph.engine.router.GraphEngineActorPoolMgr
import org.joda.time.{DateTime, DateTimeZone}
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import org.sunbird.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.collection.JavaConverters._


/**
  * @author Mahesh Kumar Gangula
  */

abstract class BaseAPIActor extends UntypedActor {

  @transient val df: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ").withZoneUTC();
  val API_VERSION = "1.0";

  @throws(classOf[Exception])
  def onReceive(request: org.sunbird.api.Request);

  override def onReceive(message: Any): Unit = {
    val request = message.asInstanceOf[org.sunbird.api.Request];
    try {
      onReceive(request);
    } catch {
      case ex : Exception =>
        ex.printStackTrace();
        val response = errorResponseSerialized(request.apiId, ResponseCode.SERVER_ERROR.toString, "Something went wrong while processing request.", ResponseCode.SERVER_ERROR.toString);
        sender() ! response;
    }
  }

  @throws(classOf[Exception])
  def getRequestBody(message: Request): RequestBody = {
    JSONUtils.deserialize[RequestBody](message.body.getOrElse("{}"));
  }

  def getResponse(apiId: String, request: org.ekstep.common.dto.Request): String = {
    val response = getGraphEngineResponse(request);
    if ("OK".equals(response.getResponseCode.name())) {
      val map = response.getResult().asScala.mapValues(x => x).toMap;
      JSONUtils.serialize(OK(apiId, map));
    } else {
      errorResponseSerialized(apiId, response.getParams.getErr, response.getParams.getErrmsg, response.getResponseCode.name());
    }
  }

  private def errorResponse(apiId: String, err: String, errMsg: String, responseCode: String): Response = {
    Response(apiId, "1.0", df.print(System.currentTimeMillis()),
      Params(UUID.randomUUID().toString(), null, err, "failed", errMsg),
      responseCode, None);
  }

  def errorResponseSerialized(apiId: String, err: String, errMsg: String, responseCode: String): String = {
    JSONUtils.serialize(errorResponse(apiId, err, errMsg, responseCode));
  }

  def OK(apiId: String, result: Map[String, AnyRef]): Response = {
    Response(apiId, API_VERSION, df.print(DateTime.now(DateTimeZone.UTC).getMillis), Params(UUID.randomUUID().toString(), null, null, "successful", null), ResponseCode.OK.toString(), Option(result));
  }

  def invalidAPIResponseSerialized(apiId: String): String = {
    JSONUtils.serialize(errorResponse(apiId, "INVALID_API_ID", "Invalid API id.", ResponseCode.SERVER_ERROR.toString));
  }

  def getGraphEngineRequest(graphId: String, manager: String, method: String): org.ekstep.common.dto.Request = {
    val request = new org.ekstep.common.dto.Request();
    request.getContext.put(GraphHeaderParams.graph_id.name, graphId)
    request.setManagerName(manager)
    request.setOperation(method)
    return request;
  }

  def getGraphEngineResponse(request: org.ekstep.common.dto.Request): org.ekstep.common.dto.Response = {
    val future = Patterns.ask(GraphEngineActorPoolMgr.getRequestRouter, request, 5000);
    val result = Await.result(future, Duration.create("10 second"));
    result.asInstanceOf[org.ekstep.common.dto.Response];
  }
}
