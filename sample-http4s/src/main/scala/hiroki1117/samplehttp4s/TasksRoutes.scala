package hiroki1117.samplehttp4s

import cats.effect.Async
import cats.syntax.all.*
import io.circe.generic.auto.*
import org.http4s.*
import org.http4s.circe.*
import sttp.tapir.server.http4s.Http4sServerInterpreter
import hiroki1117.samplehttp4s.api.TasksApi

final case class Task(id: Long, userId: Long, title: String, done: Boolean)

object Task:
  // io.circe.generic.auto.* により自動的に Encoder/Decoder が導出される
  given [F[_]]: EntityEncoder[F, Task]        = jsonEncoderOf[F, Task]
  given [F[_]]: EntityEncoder[F, List[Task]]  = jsonEncoderOf[F, List[Task]]

object TasksRoutes:

  def routes[F[_]: Async]: HttpRoutes[F] =
    // ダミーのタスク一覧（userId ごとにフィルタしている体）
    def dummyTasks(userId: Long): List[Task] = List(
      Task(1, userId, s"task1 for user $userId", done = false),
      Task(2, userId, s"task2 for user $userId", done = true)
    )

    // Tapir エンドポイントの実装
    val listTasksRoute = TasksApi.listTasks.serverLogic[F] { userId =>
      dummyTasks(userId).asRight[Unit].pure[F]
    }

    val getTaskRoute = TasksApi.getTask.serverLogic[F] { case (userId, id) =>
      dummyTasks(userId).find(_.id == id) match
        case Some(task) => task.asRight[TasksApi.ErrorResponse].pure[F]
        case None       => TasksApi.ErrorResponse(s"Task $id not found for user $userId").asLeft[Task].pure[F]
    }

    val createTaskRoute = TasksApi.createTask.serverLogic[F] { case (userId, req) =>
      val created = Task(999, userId, req.title, done = false)
      created.asRight[TasksApi.ErrorResponse].pure[F]
    }

    val updateTaskRoute = TasksApi.updateTask.serverLogic[F] { case (userId, id, req) =>
      val updated = Task(id, userId, req.title, req.done)
      updated.asRight[TasksApi.ErrorResponse].pure[F]
    }

    val deleteTaskRoute = TasksApi.deleteTask.serverLogic[F] { case (userId, id) =>
      TasksApi.DeleteResponse(s"Task $id for user $userId deleted (dummy)").asRight[Unit].pure[F]
    }

    // Http4s ルートに変換
    Http4sServerInterpreter[F]().toRoutes(
      List(
        listTasksRoute,
        getTaskRoute,
        createTaskRoute,
        updateTaskRoute,
        deleteTaskRoute
      )
    )
