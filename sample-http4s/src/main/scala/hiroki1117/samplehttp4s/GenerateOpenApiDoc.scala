package hiroki1117.samplehttp4s

import sttp.apispec.openapi.OpenAPI
import sttp.apispec.openapi.circe.yaml._
import sttp.apispec.openapi.Info
import sttp.apispec.openapi.Server
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import hiroki1117.samplehttp4s.adapter.http.endpoint.{UsersEndpoint, TasksEndpoint}
import java.nio.file.{Files, Paths, StandardOpenOption}
import java.nio.charset.StandardCharsets

/** OpenAPI仕様書を生成してファイルに出力 */
object GenerateOpenApiDoc:

  def generate(): Unit =
    // すべてのエンドポイントを集約
    val allEndpoints = UsersEndpoint.all ++ TasksEndpoint.all

    // OpenAPI仕様を生成
    val openApiDocs: OpenAPI = OpenAPIDocsInterpreter()
      .toOpenAPI(
        allEndpoints,
        Info(
          title = "Sample HTTP4s API",
          version = "1.0.0",
          description = Some("HTTP4s + Tapir sample API documentation")
        )
      )
      .servers(List(
        Server(url = "http://localhost:8081", description = Some("Local development server"))
      ))

    // YAML形式で出力
    val yamlContent = openApiDocs.toYaml

    // ファイルに書き込み
    val outputPath = Paths.get("docs/openapi.yaml")
    
    // ディレクトリを作成（存在しない場合）
    Files.createDirectories(outputPath.getParent)
    
    // ファイルに書き込み
    Files.write(
      outputPath,
      yamlContent.getBytes(StandardCharsets.UTF_8),
      StandardOpenOption.CREATE,
      StandardOpenOption.TRUNCATE_EXISTING
    )

    println(s"✅ OpenAPI仕様書を生成しました: ${outputPath.toAbsolutePath}")
    println(s"📄 Swagger UI で確認: https://editor.swagger.io/")
