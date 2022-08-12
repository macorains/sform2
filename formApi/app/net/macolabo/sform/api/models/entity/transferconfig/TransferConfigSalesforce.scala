package net.macolabo.sform.api.models.entity.transferconfig

import java.time.ZonedDateTime

case class TransferConfigSalesforce(
                                     id: BigInt,
                                     transfer_config_id: BigInt,
                                     sf_user_name: String,
                                     sf_password: String,
                                     sf_security_token: String,
                                     user_group: String,
                                     created_user: String,
                                     modified_user: String,
                                     created: ZonedDateTime,
                                     modified: ZonedDateTime
                                   )
