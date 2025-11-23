package hiroki1117.samplehttp4s.adapter.http.route

import cats.effect.Async
import cats.syntax.all._

import org.http4s._
import sttp.tapir.server.http4s.Http4sServerInterpreter

import hiroki1117.samplehttp4s.adapter.http.dto._
import hiroki1117.samplehttp4s.adapter.http.endpoint.UsersEndpoint
import hiroki1117.samplehttp4s.domain.model.User

object UsersRoutes:

  def routes[F[_]: Async]: HttpRoutes[F] =
    // ダミーのドメインモデルデータ
    val dummyUsers: List[User] = List(
      User(1, "Alice"),
      User(2, "Bob"),
    )

    // Tapir エンドポイントの実装
    val listUsersRoute = UsersEndpoint.listUsers.serverLogic[F] { _ =>
      // domain -> DTO 変換
      val userDtos = dummyUsers.map(UserDto.fromDomain)
      userDtos.asRight[Unit].pure[F]
    }

    val getUserRoute = UsersEndpoint.getUser.serverLogic[F] { id =>
      dummyUsers.find(_.id == id) match
        case Some(user) =>
          // domain -> DTO 変換
          UserDto.fromDomain(user).asRight[ErrorResponse].pure[F]
        case None =>
          ErrorResponse(s"User $id not found").asLeft[UserDto].pure[F]
    }

    val createUserRoute = UsersEndpoint.createUser.serverLogic[F] { req =>
      // DTO -> domain 変換してビジネスロジック実行
      val created = User(999, req.name)
      // domain -> DTO 変換してレスポンス
      UserDto.fromDomain(created).asRight[ErrorResponse].pure[F]
    }

    val updateUserRoute = UsersEndpoint.updateUser.serverLogic[F] {
      case (id, req) =>
        // DTO -> domain 変換してビジネスロジック実行
        val updated = User(id, req.name)
        // domain -> DTO 変換してレスポンス
        UserDto.fromDomain(updated).asRight[ErrorResponse].pure[F]
    }

    val deleteUserRoute = UsersEndpoint.deleteUser.serverLogic[F] { id =>
      DeleteUserResponse(s"User $id deleted (dummy)").asRight[Unit].pure[F]
    }

    // Http4s ルートに変換
    Http4sServerInterpreter[F]().toRoutes(
      List(
        listUsersRoute,
        getUserRoute,
        createUserRoute,
        updateUserRoute,
        deleteUserRoute,
      )
    )
