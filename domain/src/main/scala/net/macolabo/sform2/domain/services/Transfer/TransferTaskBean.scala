package net.macolabo.sform2.domain.services.Transfer

import java.math.BigInteger

case class TransferTaskBean(
  id: BigInt,
  transfer_config_id: BigInt,
  form_id: BigInt,
  task_index: Int,
  name: String,
  user_group: String,
  condition: List[TransferTaskBeanCondition],
  t_mail: Option[TransferTaskBeanMail],
  t_salesforce: Option[TransferTaskBeanSalesforce]
)

case class TransferTaskBeanCondition(
  id: BigInt,
  form_transfer_task_id: BigInt,
  form_id: BigInt,
  form_col_id: BigInt,
  operator: String,
  cond_value: String,
  user_group: String
)

case class TransferTaskBeanMail (
  id: BigInt,
  form_transfer_task_id: BigInt,
  from_address_id: BigInteger,
  to_address: Option[String],
  to_address_id: Option[BigInteger],
  to_address_field: Option[String],
  cc_address: Option[String],
  cc_address_id: Option[BigInteger],
  cc_address_field: Option[String],
  bcc_address_id: BigInteger,
  replyto_address_id: BigInteger,
  subject: String,
  body: String,
  user_group: String
)

case class TransferTaskBeanSalesforce (
  id: BigInt,
  form_transfer_task_id: BigInt,
  object_name: String,
  user_group: String,
  fields: List[TransferTaskBeanSalesforceField]
)

case class TransferTaskBeanSalesforceField(
  id: BigInt,
  form_transfer_task_salesforce_id: BigInt,
  form_column_id: String,
  field_name: String,
  user_group: String
)


