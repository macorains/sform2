package net.macolabo.sform2.domain.models.entity.transfer

import scalikejdbc._

import java.time.ZonedDateTime

case class TransferConfigSalesforceObjectField(
  id: BigInt,
  transfer_config_salesforce_object_id: BigInt,
  name: String,
  label: String,
  field_type: String,
  active: Boolean,
  user_group: String,
  created_user: String,
  modified_user: String,
  created: ZonedDateTime,
  modified: ZonedDateTime
)

object TransferConfigSalesforceObjectField extends SQLSyntaxSupport[TransferConfigSalesforceObjectField] {
  override val tableName = "d_transfer_config_salesforce_object_field"

  def apply(rs: WrappedResultSet): TransferConfigSalesforceObjectField = {
    TransferConfigSalesforceObjectField(
      rs.bigInt("id"),
      rs.bigInt("transfer_config_salesforce_object_id"),
      rs.string("name"),
      rs.string("label"),
      rs.string("field_type"),
      rs.boolean("active"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }


}
