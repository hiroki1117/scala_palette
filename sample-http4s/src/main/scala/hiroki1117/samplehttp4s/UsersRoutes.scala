package hiroki1117.samplehttp4s

import cats.effect.Async
import cats.syntax.all.*
import io.circe.generic.auto.*
import org.http4s.*
import org.http4s.circe.*
import sttp.tapir.server.http4s.Http4sServerInterpreter
import hiroki1117.samplehttp4s.api.UsersApi

final case class User(id: Long, name: String)

object User:
  // Circe の自動導出を使用（Tapir との互換性のため）
  given [F[_]]: EntityEncoder[F, User]       = jsonEncoderOf[F, User]
  given [F[_]]: EntityEncoder[F, List[User]] = jsonEncoderOf[F, List[User]]

object UsersRoutes:

  def routes[F[_]: Async]: HttpRoutes[F] =
    val dummyUsers: List[User] = List(
      User(1, "Alice"),
      User(2, "Bob")
    )

    // Tapir エンドポイントの実装
    val listUsersRoute = UsersApi.listUsers.serverLogic[F] { _ =>
      dummyUsers.asRight[Unit].pure[F]
    }

    val getUserRoute = UsersApi.getUser.serverLogic[F] { id =>
      dummyUsers.find(_.id == id) match
        case Some(user) => user.asRight[UsersApi.ErrorResponse].pure[F]
        case None       => UsersApi.ErrorResponse(s"User $id not found").asLeft[User].pure[F]
    }

    val createUserRoute = UsersApi.createUser.serverLogic[F] { req =>
      val created = User(999, req.name)
      created.asRight[UsersApi.ErrorResponse].pure[F]
    }

    val updateUserRoute = UsersApi.updateUser.serverLogic[F] { case (id, req) =>
      val updated = User(id, req.name)
      updated.asRight[UsersApi.ErrorResponse].pure[F]
    }

    val deleteUserRoute = UsersApi.deleteUser.serverLogic[F] { id =>
      UsersApi.DeleteResponse(s"User $id deleted (dummy)").asRight[Unit].pure[F]
    }

    // Http4s ルートに変換
    Http4sServerInterpreter[F]().toRoutes(
      List(
        listUsersRoute,
        getUserRoute,
        createUserRoute,
        updateUserRoute,
        deleteUserRoute
      )
    )
