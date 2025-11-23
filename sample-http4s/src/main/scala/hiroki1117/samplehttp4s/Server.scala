package hiroki1117.samplehttp4s

import cats.effect.Async
import fs2.io.net.Network
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import com.comcast.ip4s.*
import org.http4s.ember.server.EmberServerBuilder
import scala.concurrent.duration.*


object Server:
  def run[F[_]: Async: Network]: F[Nothing] =
    val dsl = new Http4sDsl[F]{}
    import dsl.*
    val helloRoute = HttpRoutes.of[F]:
      case GET -> Root / "hello" =>
        Ok("hello")

    val httpApp = helloRoute.orNotFound

    EmberServerBuilder.default[F]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8081")
      .withHttpApp(httpApp)
      .withShutdownTimeout(1.second)
      .build
      .useForever
    