package net.macolabo.sform.api.models.entity.transferconfig

import java.time.ZonedDateTime

/**
 * Transfer設定
 *
 * @param id Transfer設定ID
 * @param type_code Transfer Type Code
 * @param config_index Transfer設定の番号
 * @param name 名前
 * @param status ステータス
 * @param user_group ユーザーグループ
 * @param created_user 作成ユーザー
 * @param modified_user 更新ユーザー
 * @param created 作成日
 * @param modified 更新日
 */
case class TransferConfig(
                           id: BigInt,
                           type_code: String,
                           config_index: Int,
                           name: String,
                           status: Int,
                           user_group: String,
                           created_user: String,
                           modified_user: String,
                           created: ZonedDateTime,
                           modified: ZonedDateTime
                         )
