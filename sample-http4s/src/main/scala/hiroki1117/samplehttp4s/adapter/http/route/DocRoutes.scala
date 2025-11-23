package hiroki1117.samplehttp4s.adapter.http.route

import cats.effect.Async
import org.http4s.HttpRoutes
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.SwaggerUIOptions
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import hiroki1117.samplehttp4s.adapter.http.endpoint.{UsersEndpoint, TasksEndpoint}

/** API ドキュメント（OpenAPI + Swagger UI）を提供するルート */
object DocRoutes:

  def routes[F[_]: Async]: HttpRoutes[F] =
    // すべてのエンドポイントを集約
    val allEndpoints = UsersEndpoint.all ++ TasksEndpoint.all

    // Swagger UIのエンドポイントを生成
    val swaggerEndpoints = SwaggerInterpreter(
      swaggerUIOptions = SwaggerUIOptions.default.copy(
        pathPrefix = List("docs"),
        yamlName = "docs.yaml"
      )
    ).fromEndpoints[F](
      allEndpoints,
      "Sample HTTP4s API",
      "1.0.0"
    )

    // Http4s ルートに変換
    Http4sServerInterpreter[F]().toRoutes(swaggerEndpoints)
