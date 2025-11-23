package hiroki1117.samplehttp4s.domain.model

/** タスクドメインモデル */
final case class Task(id: Long, userId: Long, title: String, done: Boolean)
