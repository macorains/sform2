package net.macolabo.sform2.domain.models.entity.transfer

import scalikejdbc._

import java.time.ZonedDateTime

case class TransferConfigSalesforceObject(
  id: BigInt,
  transfer_config_salesforce_id: BigInt,
  name: String,
  label: String,
  active: Boolean,
  user_group: String,
  created_user: String,
  modified_user: String,
  created: ZonedDateTime,
  modified: ZonedDateTime
)

object TransferConfigSalesforceObject extends SQLSyntaxSupport[TransferConfigSalesforceObject] {
  override val tableName = "d_transfer_config_salesforce_object"
  def apply(rs:WrappedResultSet): TransferConfigSalesforceObject = {
    TransferConfigSalesforceObject(
      rs.bigInt("id"),
      rs.bigInt("transfer_config_salesforce_id"),
      rs.string("name"),
      rs.string("label"),
      rs.boolean("active"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }


}


