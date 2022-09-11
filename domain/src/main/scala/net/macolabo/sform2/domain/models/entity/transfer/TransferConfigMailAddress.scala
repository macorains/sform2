package net.macolabo.sform2.domain.models.entity.transfer

import scalikejdbc._

import java.time.ZonedDateTime

case class TransferConfigMailAddress(
  id: BigInt,
  transfer_config_mail_id: BigInt,
  address_index: Int,
  name: String,
  address: String,
  user_group: String,
  created_user: String,
  modified_user: String,
  created: ZonedDateTime,
  modified: ZonedDateTime
)

object TransferConfigMailAddress extends SQLSyntaxSupport[TransferConfigMailAddress] {
  override val tableName = "d_transfer_config_mail_address"
  def apply(rs: WrappedResultSet): TransferConfigMailAddress = {
    TransferConfigMailAddress(
      rs.bigInt("id"),
      rs.bigInt("transfer_config_mail_id"),
      rs.int("address_index"),
      rs.string("name"),
      rs.string("address"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }
}
