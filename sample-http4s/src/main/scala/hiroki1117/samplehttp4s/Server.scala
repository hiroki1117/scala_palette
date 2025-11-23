package hiroki1117.samplehttp4s

import scala.concurrent.duration.*

import cats.effect.Async
import cats.syntax.all.*

import com.comcast.ip4s.*
import fs2.io.net.Network
import hiroki1117.samplehttp4s.adapter.http.route.AppRoutes
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.Logger

object Server:
  def run[F[_]: Async: Network]: F[Nothing] =
    val dsl = new Http4sDsl[F] {}
    import dsl.*
    val helloRoute = HttpRoutes.of[F]:
      case GET -> Root / "hello" =>
        Ok("hello")

    val appRoutes = AppRoutes.routes[F]
    val httpApp = (helloRoute <+> appRoutes).orNotFound
    val finalHttpApp = Logger.httpApp(true, true)(httpApp)

    EmberServerBuilder
      .default[F]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8081")
      .withHttpApp(finalHttpApp)
      .withShutdownTimeout(1.second)
      .build
      .useForever
