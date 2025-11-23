package hiroki1117.samplehttp4s.adapter.http.dto

import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._

import hiroki1117.samplehttp4s.domain.model.Task

/** HTTPレスポンス用のTaskDTO */
final case class TaskDto(id: Long, userId: Long, title: String, done: Boolean)

object TaskDto:
  // Circe の自動導出を使用
  given [F[_]]: EntityEncoder[F, TaskDto] = jsonEncoderOf[F, TaskDto]
  given [F[_]]: EntityEncoder[F, List[TaskDto]] = jsonEncoderOf[F, List[TaskDto]]

  /** domain.model.Task から TaskDto への変換 */
  def fromDomain(task: Task): TaskDto =
    TaskDto(id = task.id, userId = task.userId, title = task.title, done = task.done)

  /** TaskDto から domain.model.Task への変換 */
  def toDomain(dto: TaskDto): Task =
    Task(id = dto.id, userId = dto.userId, title = dto.title, done = dto.done)

/** タスク作成リクエスト */
final case class CreateTaskRequest(title: String)

/** タスク更新リクエスト */
final case class UpdateTaskRequest(title: String, done: Boolean)

/** タスク削除レスポンス */
final case class DeleteTaskResponse(message: String)
