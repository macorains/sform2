package net.macolabo.sform2.domain.services.User

import play.api.libs.json.{Format, Json}

/**
 * Adminグループユーザーの存在チェック結果
 * @param result Adminグループユーザーが存在するか
 */
case class AdminExistsCheckResult(result: Boolean)

object AdminExistsCheckResult {
  implicit val format: Format[AdminExistsCheckResult] = Json.format[AdminExistsCheckResult]
}
