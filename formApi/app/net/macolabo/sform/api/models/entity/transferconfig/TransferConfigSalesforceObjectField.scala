package net.macolabo.sform.api.models.entity.transferconfig

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
