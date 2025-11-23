val Http4sVersion = "0.23.30"
val CirceVersion = "0.14.14"
val MunitVersion = "1.1.1"
val LogbackVersion = "1.5.18"
val MunitCatsEffectVersion = "2.1.0"
val TapirVersion = "1.12.3"
val OpenapiCirceYamlVersion = "0.11.3"

val dependencies = Seq(
  // Http4s
  "org.http4s" %% "http4s-ember-server" % Http4sVersion,
  "org.http4s" %% "http4s-ember-client" % Http4sVersion,
  "org.http4s" %% "http4s-circe" % Http4sVersion,
  "org.http4s" %% "http4s-dsl" % Http4sVersion,
  // Circe
  "io.circe" %% "circe-generic" % CirceVersion,
  // Tapir
  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % TapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % TapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % TapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % TapirVersion,
  "com.softwaremill.sttp.apispec" %% "openapi-circe-yaml" % OpenapiCirceYamlVersion,
  // Test
  "org.scalameta" %% "munit" % MunitVersion % Test,
  "org.typelevel" %% "munit-cats-effect" % MunitCatsEffectVersion % Test,
  // Logging
  "ch.qos.logback" % "logback-classic" % LogbackVersion % Runtime
)

lazy val root = (project in file("."))
  .settings(
    organization := "hiroki1117",
    name := "sample-http4s",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "3.7.4",
    Compile / mainClass := Some("hiroki1117.samplehttp4s.Main"),
    libraryDependencies ++= dependencies,
    // Scalafix
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    assembly / assemblyMergeStrategy := {
      case "module-info.class" => MergeStrategy.discard
      case x => (assembly / assemblyMergeStrategy).value.apply(x)
    }
  )

// OpenAPI仕様書を生成するカスタムタスク
lazy val generateApiDocs = taskKey[Unit]("Generate OpenAPI documentation")

generateApiDocs := {
  (Compile / runMain).toTask(" hiroki1117.samplehttp4s.GenerateOpenApiDoc").value
}
