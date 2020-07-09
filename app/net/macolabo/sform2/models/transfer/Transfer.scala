package net.macolabo.sform2.models.transfer

import java.time.ZonedDateTime

case class Transfer(
                     id: Int,
                     type_id: String,
                     name: String,
                     status: Int,
                     created_user: String,
                     modified_user: String,
                     created: ZonedDateTime,
                     modified: ZonedDateTime
                   )
