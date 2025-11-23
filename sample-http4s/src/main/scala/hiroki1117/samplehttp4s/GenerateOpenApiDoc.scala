package hiroki1117.samplehttp4s

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths, StandardOpenOption}

import hiroki1117.samplehttp4s.adapter.http.endpoint.{TasksEndpoint, UsersEndpoint}
import sttp.apispec.openapi.circe.yaml._
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter

/** OpenAPI仕様書を生成してファイルに出力 */
object GenerateOpenApiDoc:

  def main(args: Array[String]): Unit =
    // すべてのエンドポイントを集約
    val allEndpoints = UsersEndpoint.all ++ TasksEndpoint.all

    // OpenAPI仕様を生成
    val docs = OpenAPIDocsInterpreter()
      .toOpenAPI(allEndpoints, "Sample HTTP4s API", "1.0.0")

    // YAML形式で出力
    val yamlContent = docs.toYaml

    // ファイルに書き込み
    val outputPath = Paths.get("docs/openapi.yaml")

    // ディレクトリを作成（存在しない場合）
    Files.createDirectories(outputPath.getParent)

    // ファイルに書き込み
    Files.write(
      outputPath,
      yamlContent.getBytes(StandardCharsets.UTF_8),
      StandardOpenOption.CREATE,
      StandardOpenOption.TRUNCATE_EXISTING,
    )

    println(s"✅ OpenAPI仕様書を生成しました: ${outputPath.toAbsolutePath}")
    println(s"📄 Swagger UI で確認: https://editor.swagger.io/")
