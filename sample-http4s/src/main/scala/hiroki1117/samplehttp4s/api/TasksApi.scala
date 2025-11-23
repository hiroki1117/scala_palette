package hiroki1117.samplehttp4s.api

import sttp.tapir._
import sttp.tapir.json.circe.*
import sttp.model.StatusCode
import io.circe.generic.auto.*
import hiroki1117.samplehttp4s.Task

/** Tasks API のエンドポイント定義 */
object TasksApi:

  // ベースエンドポイント
  private val baseEndpoint = endpoint
    .in("users")
    .tag("Tasks")

  // エラーレスポンス
  case class ErrorResponse(message: String) derives Schema

  // リクエストボディ
  case class CreateTaskRequest(title: String) derives Schema
  case class UpdateTaskRequest(title: String, done: Boolean) derives Schema

  // レスポンス
  case class DeleteResponse(message: String) derives Schema
  
  // Task の Schema を明示的に定義
  given Schema[Task] = Schema.derived[Task]

  /** GET /users/{userId}/tasks - タスク一覧取得 */
  val listTasks: PublicEndpoint[Long, Unit, List[Task], Any] =
    baseEndpoint.get
      .in(path[Long]("userId") / "tasks")
      .out(jsonBody[List[Task]])
      .description("指定ユーザーのタスク一覧を取得")

  /** GET /users/{userId}/tasks/{id} - タスク1件取得 */
  val getTask: PublicEndpoint[(Long, Long), ErrorResponse, Task, Any] =
    baseEndpoint.get
      .in(path[Long]("userId") / "tasks" / path[Long]("id"))
      .out(jsonBody[Task])
      .errorOut(
        oneOf[ErrorResponse](
          oneOfVariant(statusCode(StatusCode.NotFound).and(jsonBody[ErrorResponse]))
        )
      )
      .description("指定IDのタスクを取得")

  /** POST /users/{userId}/tasks - タスク作成 */
  val createTask: PublicEndpoint[(Long, CreateTaskRequest), ErrorResponse, Task, Any] =
    baseEndpoint.post
      .in(path[Long]("userId") / "tasks")
      .in(jsonBody[CreateTaskRequest])
      .out(statusCode(StatusCode.Created))
      .out(jsonBody[Task])
      .errorOut(
        oneOf[ErrorResponse](
          oneOfVariant(statusCode(StatusCode.BadRequest).and(jsonBody[ErrorResponse]))
        )
      )
      .description("新しいタスクを作成")

  /** PUT /users/{userId}/tasks/{id} - タスク更新 */
  val updateTask: PublicEndpoint[(Long, Long, UpdateTaskRequest), ErrorResponse, Task, Any] =
    baseEndpoint.put
      .in(path[Long]("userId") / "tasks" / path[Long]("id"))
      .in(jsonBody[UpdateTaskRequest])
      .out(jsonBody[Task])
      .errorOut(
        oneOf[ErrorResponse](
          oneOfVariant(statusCode(StatusCode.BadRequest).and(jsonBody[ErrorResponse]))
        )
      )
      .description("指定IDのタスクを更新")

  /** DELETE /users/{userId}/tasks/{id} - タスク削除 */
  val deleteTask: PublicEndpoint[(Long, Long), Unit, DeleteResponse, Any] =
    baseEndpoint.delete
      .in(path[Long]("userId") / "tasks" / path[Long]("id"))
      .out(jsonBody[DeleteResponse])
      .description("指定IDのタスクを削除")

  /** 全エンドポイントのリスト */
  val all: List[AnyEndpoint] = List(
    listTasks,
    getTask,
    createTask,
    updateTask,
    deleteTask
  )
