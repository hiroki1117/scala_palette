package hiroki1117.samplehttp4s

import cats.effect.Concurrent
import cats.syntax.all.*
import io.circe.{Encoder, Json}
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl

final case class User(id: Long, name: String)

object User:
  given Encoder[User] = Encoder.instance { u =>
    Json.obj(
      "id"   -> Json.fromLong(u.id),
      "name" -> Json.fromString(u.name)
    )
  }

  given [F[_]]: EntityEncoder[F, User]       = jsonEncoderOf[F, User]
  given [F[_]]: EntityEncoder[F, List[User]] = jsonEncoderOf[F, List[User]]

object UsersRoutes:

  def routes[F[_]: Concurrent]: HttpRoutes[F] =
    val dsl = new Http4sDsl[F]{}
    import dsl.*

    val dummyUsers: List[User] = List(
      User(1, "Alice"),
      User(2, "Bob")
    )

    HttpRoutes.of[F]:
      // 一覧取得: GET /users
      case GET -> Root / "users" =>
        Ok(dummyUsers)

      // 1件取得: GET /users/{id}
      case GET -> Root / "users" / LongVar(id) =>
        dummyUsers.find(_.id == id) match
          case Some(user) => Ok(user)
          case None       => NotFound(Json.obj("message" -> Json.fromString(s"User $id not found")))

      // 作成: POST /users
      // ※ ダミーなので、常に id=999 のユーザを作った風に返す
      case req @ POST -> Root / "users" =>
        req.as[Json].attempt.flatMap {
          case Right(json) =>
            val name = json.hcursor.get[String]("name").getOrElse("no-name")
            val created = User(999, name)
            Created(created)
          case Left(_) =>
            BadRequest(Json.obj("message" -> Json.fromString("invalid json")))
        }

      // 更新: PUT /users/{id}
      case req @ PUT -> Root / "users" / LongVar(id) =>
        req.as[Json].attempt.flatMap {
          case Right(json) =>
            val name = json.hcursor.get[String]("name").getOrElse("no-name")
            val updated = User(id, name)
            Ok(updated)
          case Left(_) =>
            BadRequest(Json.obj("message" -> Json.fromString("invalid json")))
        }

      // 削除: DELETE /users/{id}
      case DELETE -> Root / "users" / LongVar(id) =>
        Ok(Json.obj("message" -> Json.fromString(s"User $id deleted (dummy)")))
