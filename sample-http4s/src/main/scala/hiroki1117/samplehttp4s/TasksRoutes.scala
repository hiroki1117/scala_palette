package hiroki1117.samplehttp4s

import cats.effect.Concurrent
import cats.syntax.all.*
import io.circe.{Encoder, Json}
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl

final case class Task(id: Long, userId: Long, title: String, done: Boolean)

object Task:
  given Encoder[Task] = Encoder.instance { t =>
    Json.obj(
      "id"      -> Json.fromLong(t.id),
      "userId"  -> Json.fromLong(t.userId),
      "title"   -> Json.fromString(t.title),
      "done"    -> Json.fromBoolean(t.done)
    )
  }

  given [F[_]]: EntityEncoder[F, Task]        = jsonEncoderOf[F, Task]
  given [F[_]]: EntityEncoder[F, List[Task]]  = jsonEncoderOf[F, List[Task]]

object TasksRoutes:

  def routes[F[_]: Concurrent]: HttpRoutes[F] =
    val dsl = new Http4sDsl[F]{}
    import dsl.*

    // ダミーのタスク一覧（userId ごとにフィルタしている体）
    def dummyTasks(userId: Long): List[Task] = List(
      Task(1, userId, s"task1 for user $userId", done = false),
      Task(2, userId, s"task2 for user $userId", done = true)
    )

    HttpRoutes.of[F]:
      // 一覧取得: GET /users/{userId}/tasks
      case GET -> Root / "users" / LongVar(userId) / "tasks" =>
        Ok(dummyTasks(userId))

      // 1件取得: GET /users/{userId}/tasks/{id}
      case GET -> Root / "users" / LongVar(userId) / "tasks" / LongVar(id) =>
        dummyTasks(userId).find(_.id == id) match
          case Some(task) => Ok(task)
          case None       => NotFound(Json.obj("message" -> Json.fromString(s"Task $id not found for user $userId")))

      // 作成: POST /users/{userId}/tasks
      case req @ POST -> Root / "users" / LongVar(userId) / "tasks" =>
        req.as[Json].attempt.flatMap {
          case Right(json) =>
            val title = json.hcursor.get[String]("title").getOrElse("no-title")
            val created = Task(999, userId, title, done = false)
            Created(created)
          case Left(_) =>
            BadRequest(Json.obj("message" -> Json.fromString("invalid json")))
        }

      // 更新: PUT /users/{userId}/tasks/{id}
      case req @ PUT -> Root / "users" / LongVar(userId) / "tasks" / LongVar(id) =>
        req.as[Json].attempt.flatMap {
          case Right(json) =>
            val title = json.hcursor.get[String]("title").getOrElse("no-title")
            val done  = json.hcursor.get[Boolean]("done").getOrElse(false)
            val updated = Task(id, userId, title, done)
            Ok(updated)
          case Left(_) =>
            BadRequest(Json.obj("message" -> Json.fromString("invalid json")))
        }

      // 削除: DELETE /users/{userId}/tasks/{id}
      case DELETE -> Root / "users" / LongVar(userId) / "tasks" / LongVar(id) =>
        Ok(Json.obj("message" -> Json.fromString(s"Task $id for user $userId deleted (dummy)")))
