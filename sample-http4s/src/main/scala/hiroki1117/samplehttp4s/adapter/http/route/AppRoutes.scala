package hiroki1117.samplehttp4s.adapter.http.route

import cats.effect.Async
import cats.syntax.all._

import org.http4s.HttpRoutes

/** アプリケーションのHttp4sルートをまとめるエントリーポイント */
object AppRoutes:

  def routes[F[_]: Async]: HttpRoutes[F] =
    // 各機能ごとのルートを合成
    val users = UsersRoutes.routes[F]
    val tasks = TasksRoutes.routes[F]
    val docs = DocRoutes.routes[F]

    users <+> tasks <+> docs
