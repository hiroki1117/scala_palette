package hiroki1117.samplehttp4s.adapter.http.endpoint

import hiroki1117.samplehttp4s.adapter.http.dto._
import io.circe.generic.auto.*
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.json.circe.*

/** Tasks エンドポイント定義 */
object TasksEndpoint:

  // ベースエンドポイント
  private val baseEndpoint = endpoint
    .in("users")
    .tag("Tasks")

  // Schema の明示的な定義
  given Schema[TaskDto] = Schema.derived[TaskDto]
  given Schema[ErrorResponse] = Schema.derived[ErrorResponse]
  given Schema[CreateTaskRequest] = Schema.derived[CreateTaskRequest]
  given Schema[UpdateTaskRequest] = Schema.derived[UpdateTaskRequest]
  given Schema[DeleteTaskResponse] = Schema.derived[DeleteTaskResponse]

  /** GET /users/{userId}/tasks - タスク一覧取得 */
  val listTasks: PublicEndpoint[Long, Unit, List[TaskDto], Any] =
    baseEndpoint.get
      .in(path[Long]("userId") / "tasks")
      .out(jsonBody[List[TaskDto]])
      .description("指定ユーザーのタスク一覧を取得")

  /** GET /users/{userId}/tasks/{id} - タスク1件取得 */
  val getTask: PublicEndpoint[(Long, Long), ErrorResponse, TaskDto, Any] =
    baseEndpoint.get
      .in(path[Long]("userId") / "tasks" / path[Long]("id"))
      .out(jsonBody[TaskDto])
      .errorOut(
        oneOf[ErrorResponse](
          oneOfVariant(statusCode(StatusCode.NotFound).and(jsonBody[ErrorResponse]))
        )
      )
      .description("指定IDのタスクを取得")

  /** POST /users/{userId}/tasks - タスク作成 */
  val createTask: PublicEndpoint[(Long, CreateTaskRequest), ErrorResponse, TaskDto, Any] =
    baseEndpoint.post
      .in(path[Long]("userId") / "tasks")
      .in(jsonBody[CreateTaskRequest])
      .out(statusCode(StatusCode.Created))
      .out(jsonBody[TaskDto])
      .errorOut(
        oneOf[ErrorResponse](
          oneOfVariant(statusCode(StatusCode.BadRequest).and(jsonBody[ErrorResponse]))
        )
      )
      .description("新しいタスクを作成")

  /** PUT /users/{userId}/tasks/{id} - タスク更新 */
  val updateTask: PublicEndpoint[(Long, Long, UpdateTaskRequest), ErrorResponse, TaskDto, Any] =
    baseEndpoint.put
      .in(path[Long]("userId") / "tasks" / path[Long]("id"))
      .in(jsonBody[UpdateTaskRequest])
      .out(jsonBody[TaskDto])
      .errorOut(
        oneOf[ErrorResponse](
          oneOfVariant(statusCode(StatusCode.BadRequest).and(jsonBody[ErrorResponse]))
        )
      )
      .description("指定IDのタスクを更新")

  /** DELETE /users/{userId}/tasks/{id} - タスク削除 */
  val deleteTask: PublicEndpoint[(Long, Long), Unit, DeleteTaskResponse, Any] =
    baseEndpoint.delete
      .in(path[Long]("userId") / "tasks" / path[Long]("id"))
      .out(jsonBody[DeleteTaskResponse])
      .description("指定IDのタスクを削除")

  /** 全エンドポイントのリスト */
  val all: List[AnyEndpoint] = List(
    listTasks,
    getTask,
    createTask,
    updateTask,
    deleteTask,
  )
