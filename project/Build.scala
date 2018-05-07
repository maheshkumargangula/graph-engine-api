import sbt._
import Keys._
import play.Play.autoImport._
import PlayKeys._
import play.sbt._

object ApplicationBuild extends Build {

    val core = Project("graph-engine-api-core", file("graph-engine-api-core"))
        .settings(
            version := Pom.version(baseDirectory.value),
            libraryDependencies ++= Pom.dependencies(baseDirectory.value))

    val root = Project("graph-engine-api", file("graph-engine-api"))
        .dependsOn(core)
        .settings(
            version := Pom.version(baseDirectory.value),
            libraryDependencies ++= Pom.dependencies(baseDirectory.value).filterNot(d => d.name == core.id))

    override def rootProject = Some(root)
}