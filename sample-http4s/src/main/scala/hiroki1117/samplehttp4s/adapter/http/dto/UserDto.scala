package hiroki1117.samplehttp4s.adapter.http.dto

import io.circe.generic.auto.*
import org.http4s.*
import org.http4s.circe.*
import hiroki1117.samplehttp4s.domain.model.User

/** HTTPレスポンス用のUserDTO */
final case class UserDto(id: Long, name: String)

object UserDto:
  // Circe の自動導出を使用
  given [F[_]]: EntityEncoder[F, UserDto]       = jsonEncoderOf[F, UserDto]
  given [F[_]]: EntityEncoder[F, List[UserDto]] = jsonEncoderOf[F, List[UserDto]]

  /** domain.model.User から UserDto への変換 */
  def fromDomain(user: User): UserDto =
    UserDto(id = user.id, name = user.name)

  /** UserDto から domain.model.User への変換 */
  def toDomain(dto: UserDto): User =
    User(id = dto.id, name = dto.name)

/** ユーザー作成リクエスト */
final case class CreateUserRequest(name: String)

/** ユーザー更新リクエスト */
final case class UpdateUserRequest(name: String)

/** ユーザー削除レスポンス */
final case class DeleteUserResponse(message: String)

/** エラーレスポンス */
final case class ErrorResponse(message: String)
