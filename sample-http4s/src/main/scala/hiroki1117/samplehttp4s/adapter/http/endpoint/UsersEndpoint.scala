package hiroki1117.samplehttp4s.adapter.http.endpoint

import hiroki1117.samplehttp4s.adapter.http.dto._
import io.circe.generic.auto.*
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.json.circe.*

/** Users エンドポイント定義 */
object UsersEndpoint:

  // ベースエンドポイント
  private val baseEndpoint = endpoint
    .in("users")
    .tag("Users")

  // Schema の明示的な定義
  given Schema[UserDto] = Schema.derived[UserDto]
  given Schema[ErrorResponse] = Schema.derived[ErrorResponse]
  given Schema[CreateUserRequest] = Schema.derived[CreateUserRequest]
  given Schema[UpdateUserRequest] = Schema.derived[UpdateUserRequest]
  given Schema[DeleteUserResponse] = Schema.derived[DeleteUserResponse]

  /** GET /users - ユーザー一覧取得 */
  val listUsers: PublicEndpoint[Unit, Unit, List[UserDto], Any] =
    baseEndpoint.get
      .out(jsonBody[List[UserDto]])
      .description("ユーザー一覧を取得")

  /** GET /users/{id} - ユーザー1件取得 */
  val getUser: PublicEndpoint[Long, ErrorResponse, UserDto, Any] =
    baseEndpoint.get
      .in(path[Long]("id"))
      .out(jsonBody[UserDto])
      .errorOut(
        oneOf[ErrorResponse](
          oneOfVariant(statusCode(StatusCode.NotFound).and(jsonBody[ErrorResponse]))
        )
      )
      .description("指定IDのユーザーを取得")

  /** POST /users - ユーザー作成 */
  val createUser: PublicEndpoint[CreateUserRequest, ErrorResponse, UserDto, Any] =
    baseEndpoint.post
      .in(jsonBody[CreateUserRequest])
      .out(statusCode(StatusCode.Created))
      .out(jsonBody[UserDto])
      .errorOut(
        oneOf[ErrorResponse](
          oneOfVariant(statusCode(StatusCode.BadRequest).and(jsonBody[ErrorResponse]))
        )
      )
      .description("新しいユーザーを作成")

  /** PUT /users/{id} - ユーザー更新 */
  val updateUser: PublicEndpoint[(Long, UpdateUserRequest), ErrorResponse, UserDto, Any] =
    baseEndpoint.put
      .in(path[Long]("id"))
      .in(jsonBody[UpdateUserRequest])
      .out(jsonBody[UserDto])
      .errorOut(
        oneOf[ErrorResponse](
          oneOfVariant(statusCode(StatusCode.BadRequest).and(jsonBody[ErrorResponse]))
        )
      )
      .description("指定IDのユーザーを更新")

  /** DELETE /users/{id} - ユーザー削除 */
  val deleteUser: PublicEndpoint[Long, Unit, DeleteUserResponse, Any] =
    baseEndpoint.delete
      .in(path[Long]("id"))
      .out(jsonBody[DeleteUserResponse])
      .description("指定IDのユーザーを削除")

  /** 全エンドポイントのリスト */
  val all: List[AnyEndpoint] = List(
    listUsers,
    getUser,
    createUser,
    updateUser,
    deleteUser,
  )
