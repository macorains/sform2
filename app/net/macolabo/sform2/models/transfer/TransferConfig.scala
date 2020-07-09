package net.macolabo.sform2.models.transfer

import java.time.ZonedDateTime

case class TransferConfig(
                           id: Int,
                           type_id: String,
                           config_index: Int,
                           name: String,
                           status: Int,
                           user_group: String,
                           created_user: String,
                           modified_user: String,
                           created: ZonedDateTime,
                           modified: ZonedDateTime
                         )
