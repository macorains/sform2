package net.macolabo.sform2.domain.models.entity.transfer

import scalikejdbc._

import java.time.ZonedDateTime

/**
 * Transfer
 * @param id Transfer ID
 * @param type_code Transfer Type Code
 * @param name Transferの名前
 * @param status ステータス
 * @param created_user 作成ユーザー
 * @param modified_user 更新ユーザー
 * @param created 作成日
 * @param modified 更新日
 */
case class Transfer(
  id: BigInt,
  type_code: String,
  name: String,
  status: Int,
  created_user: String,
  modified_user: String,
  created: ZonedDateTime,
  modified: ZonedDateTime
)

object Transfer extends SQLSyntaxSupport[Transfer] {
  override val tableName = "M_TRANSFER"
  def apply(rs: WrappedResultSet):Transfer = {
    Transfer(
      rs.bigInt("id"),
      rs.string("type_code"),
      rs.string("name"),
      rs.int("status"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }
}
