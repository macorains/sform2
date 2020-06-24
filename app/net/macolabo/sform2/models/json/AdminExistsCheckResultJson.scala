package net.macolabo.sform2.models.json

import play.api.libs.json.{Json, Reads, Writes}

trait AdminExistsCheckResultJson {

  /**
   * Adminグループユーザーの存在チェック結果
   * @param result Adminグループユーザーが存在するか
   */
  case class AdminExistsCheckResult(result: Boolean)

  object AdminExistsCheckResult {
    implicit def jsonAdminExistsCheckResultWrites: Writes[AdminExistsCheckResult] = Json.writes[AdminExistsCheckResult]
    implicit def jsonAdminExistsCheckResultReads: Reads[AdminExistsCheckResult] = Json.reads[AdminExistsCheckResult]
  }
}
