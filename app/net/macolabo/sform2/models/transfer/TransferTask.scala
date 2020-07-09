package net.macolabo.sform2.models.transfer

import java.time.ZonedDateTime

import play.api.libs.json.JsValue

case class TransferTask(
                       id: Int,
                       transfer_config_id: Int,
                       conditions: JsValue,
                       user_group: String,
                       created_user: String,
                       modified_user: String,
                       created: ZonedDateTime,
                       modified: ZonedDateTime
                       )
