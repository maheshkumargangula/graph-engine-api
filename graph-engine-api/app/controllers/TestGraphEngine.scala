package controllers


import java.util

import org.ekstep.graph.engine.router.{ActorBootstrap, GraphEngineActorPoolMgr, GraphEngineManagers, RequestRouter}
import akka.actor.Props
import akka.routing.SmallestMailboxPool
import org.ekstep.common.dto.{Request, Response}
import org.ekstep.graph.common.enums.{GraphEngineParams, GraphHeaderParams}
import org.ekstep.graph.dac.enums.{GraphDACParams, SystemNodeTypes}
import org.ekstep.graph.dac.model.Node
import akka.pattern.Patterns
import scala.concurrent.Await
import scala.concurrent.duration.Duration


object TestGraphEngine {

  def main(args: Array[String]): Unit = {
    ActorBootstrap.getActorSystem();
//    readDefinition();
    createNode();
  }



  def readDefinition() = {
    val request = new Request();
    request.getContext.put(GraphHeaderParams.graph_id.name, "domain");
    request.setManagerName(GraphEngineManagers.SEARCH_MANAGER);
    request.setOperation("getNodeDefinition");
    request.put(GraphDACParams.object_type.name(), "Content");
    val future = Patterns.ask(GraphEngineActorPoolMgr.getRequestRouter, request, 5000);
    val result = Await.result(future, Duration.create("10 second"));
    val response = result.asInstanceOf[Response];
    println("ResponseCode:", response.getResponseCode);
    println("Params:", response.getParams);

  }

  def createDefinition() = {
    val request = new Request();
    request.getContext.put(GraphHeaderParams.graph_id.name, "domain")
    request.setManagerName(GraphEngineManagers.NODE_MANAGER)
    request.setOperation("importDefinitions")
    val body = """{}""";
    request.put(GraphEngineParams.input_stream.name(), body);
    println("Import Definition Request: ",request);
    val future = Patterns.ask(GraphEngineActorPoolMgr.getRequestRouter, request, 5000)
    val result = Await.result(future, Duration.create("10 second"))
    val response = result.asInstanceOf[Response]
    println("ResponseCode:", response.getResponseCode)
    println("Params:", response.getParams)
  }

  def createNode(): Unit = {
    var request = new Request();
    request.getContext.put(GraphHeaderParams.graph_id.name, "domain")
    request.setManagerName(GraphEngineManagers.NODE_MANAGER)
    request.setOperation("createDataNode")
    var node = new Node("test_content", SystemNodeTypes.DATA_NODE.name(), "Teacher")
    node.setGraphId("domain")
    var metadata = new util.HashMap[String, Object]()
    metadata.put("name", "Cotnent by Mahesh")
    node.setMetadata(metadata)
    request.put(GraphDACParams.node.name, node)
    val future = Patterns.ask(GraphEngineActorPoolMgr.getRequestRouter, request, 5000)
    val result = Await.result(future, Duration.create("10 second"))
    val response = result.asInstanceOf[Response]
    println("ResponseCode:", response.getResponseCode)
    println("Params:", response.getParams)
  }
}
