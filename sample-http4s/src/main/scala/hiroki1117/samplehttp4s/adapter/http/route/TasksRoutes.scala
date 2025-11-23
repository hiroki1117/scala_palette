package hiroki1117.samplehttp4s.adapter.http.route

import cats.effect.Async
import cats.syntax.all.*
import org.http4s.*
import sttp.tapir.server.http4s.Http4sServerInterpreter
import hiroki1117.samplehttp4s.adapter.http.endpoint.TasksEndpoint
import hiroki1117.samplehttp4s.adapter.http.dto._
import hiroki1117.samplehttp4s.domain.model.Task

object TasksRoutes:

  def routes[F[_]: Async]: HttpRoutes[F] =
    // ダミーのタスク一覧（userId ごとにフィルタしている体）
    def dummyTasks(userId: Long): List[Task] = List(
      Task(1, userId, s"task1 for user $userId", done = false),
      Task(2, userId, s"task2 for user $userId", done = true)
    )

    // Tapir エンドポイントの実装
    val listTasksRoute = TasksEndpoint.listTasks.serverLogic[F] { userId =>
      // domain -> DTO 変換
      val taskDtos = dummyTasks(userId).map(TaskDto.fromDomain)
      taskDtos.asRight[Unit].pure[F]
    }

    val getTaskRoute = TasksEndpoint.getTask.serverLogic[F] { case (userId, id) =>
      dummyTasks(userId).find(_.id == id) match
        case Some(task) => 
          // domain -> DTO 変換
          TaskDto.fromDomain(task).asRight[ErrorResponse].pure[F]
        case None => 
          ErrorResponse(s"Task $id not found for user $userId").asLeft[TaskDto].pure[F]
    }

    val createTaskRoute = TasksEndpoint.createTask.serverLogic[F] { case (userId, req) =>
      // DTO -> domain 変換してビジネスロジック実行
      val created = Task(999, userId, req.title, done = false)
      // domain -> DTO 変換してレスポンス
      TaskDto.fromDomain(created).asRight[ErrorResponse].pure[F]
    }

    val updateTaskRoute = TasksEndpoint.updateTask.serverLogic[F] { case (userId, id, req) =>
      // DTO -> domain 変換してビジネスロジック実行
      val updated = Task(id, userId, req.title, req.done)
      // domain -> DTO 変換してレスポンス
      TaskDto.fromDomain(updated).asRight[ErrorResponse].pure[F]
    }

    val deleteTaskRoute = TasksEndpoint.deleteTask.serverLogic[F] { case (userId, id) =>
      DeleteTaskResponse(s"Task $id for user $userId deleted (dummy)").asRight[Unit].pure[F]
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
