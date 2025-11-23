package hiroki1117.samplehttp4s.api

import sttp.tapir._
import sttp.tapir.json.circe.*
import sttp.model.StatusCode
import io.circe.generic.auto.*
import hiroki1117.samplehttp4s.User

/** Users API のエンドポイント定義 */
object UsersApi:

  // ベースエンドポイント
  private val baseEndpoint = endpoint
    .in("users")
    .tag("Users")

  // エラーレスポンス
  case class ErrorResponse(message: String) derives Schema

  // リクエストボディ
  case class CreateUserRequest(name: String) derives Schema
  case class UpdateUserRequest(name: String) derives Schema

  // レスポンス
  case class DeleteResponse(message: String) derives Schema
  
  // User の Schema を明示的に定義
  given Schema[User] = Schema.derived[User]

  /** GET /users - ユーザー一覧取得 */
  val listUsers: PublicEndpoint[Unit, Unit, List[User], Any] =
    baseEndpoint.get
      .out(jsonBody[List[User]])
      .description("ユーザー一覧を取得")

  /** GET /users/{id} - ユーザー1件取得 */
  val getUser: PublicEndpoint[Long, ErrorResponse, User, Any] =
    baseEndpoint.get
      .in(path[Long]("id"))
      .out(jsonBody[User])
      .errorOut(
        oneOf[ErrorResponse](
          oneOfVariant(statusCode(StatusCode.NotFound).and(jsonBody[ErrorResponse]))
        )
      )
      .description("指定IDのユーザーを取得")

  /** POST /users - ユーザー作成 */
  val createUser: PublicEndpoint[CreateUserRequest, ErrorResponse, User, Any] =
    baseEndpoint.post
      .in(jsonBody[CreateUserRequest])
      .out(statusCode(StatusCode.Created))
      .out(jsonBody[User])
      .errorOut(
        oneOf[ErrorResponse](
          oneOfVariant(statusCode(StatusCode.BadRequest).and(jsonBody[ErrorResponse]))
        )
      )
      .description("新しいユーザーを作成")

  /** PUT /users/{id} - ユーザー更新 */
  val updateUser: PublicEndpoint[(Long, UpdateUserRequest), ErrorResponse, User, Any] =
    baseEndpoint.put
      .in(path[Long]("id"))
      .in(jsonBody[UpdateUserRequest])
      .out(jsonBody[User])
      .errorOut(
        oneOf[ErrorResponse](
          oneOfVariant(statusCode(StatusCode.BadRequest).and(jsonBody[ErrorResponse]))
        )
      )
      .description("指定IDのユーザーを更新")

  /** DELETE /users/{id} - ユーザー削除 */
  val deleteUser: PublicEndpoint[Long, Unit, DeleteResponse, Any] =
    baseEndpoint.delete
      .in(path[Long]("id"))
      .out(jsonBody[DeleteResponse])
      .description("指定IDのユーザーを削除")

  /** 全エンドポイントのリスト */
  val all: List[AnyEndpoint] = List(
    listUsers,
    getUser,
    createUser,
    updateUser,
    deleteUser
  )
