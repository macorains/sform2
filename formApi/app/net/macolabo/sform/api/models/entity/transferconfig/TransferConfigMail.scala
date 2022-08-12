package net.macolabo.sform.api.models.entity.transferconfig

import java.time.ZonedDateTime

case class TransferConfigMail(
                               id: BigInt,
                               transfer_config_id: BigInt,
                               use_cc: Boolean,
                               use_bcc: Boolean,
                               use_replyto: Boolean,
                               user_group: String,
                               created_user: String,
                               modified_user: String,
                               created: ZonedDateTime,
                               modified: ZonedDateTime
                             )
