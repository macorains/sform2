package net.macolabo.sform.api.models.entity.transferconfig

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
