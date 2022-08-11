package net.macolabo.sform2.domain.models.entity.transfer

import scalikejdbc._
import java.time.ZonedDateTime

/**
 * Transfer設定
 *
 * @param id Transfer設定ID
 * @param type_code Transfer Type Code
 * @param config_index Transfer設定の番号
 * @param name 名前
 * @param status ステータス
 * @param user_group ユーザーグループ
 * @param created_user 作成ユーザー
 * @param modified_user 更新ユーザー
 * @param created 作成日
 * @param modified 更新日
 */
case class TransferConfig(
                           id: BigInt,
                           type_code: String,
                           config_index: Int,
                           name: String,
                           status: Int,
                           user_group: String,
                           created_user: String,
                           modified_user: String,
                           created: ZonedDateTime,
                           modified: ZonedDateTime
                         )

object TransferConfig extends SQLSyntaxSupport[TransferConfig] {
  override val tableName = "d_transfer_config"

  def apply(rs: WrappedResultSet): TransferConfig = {
    TransferConfig(
      rs.bigInt("id"),
      rs.string("type_code"),
      rs.int("config_index"),
      rs.string("name"),
      rs.int("status"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }
}
