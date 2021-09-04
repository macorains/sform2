package net.macolabo.sform2.models.entity.form

import java.time.ZonedDateTime

import scalikejdbc._

/**
 * TransferTask
 * @param id TransferTask ID
 * @param transfer_config_id TransferConfig ID
 * @param form_id フォームID
 * @param task_index 順序
 * @param name タスク名
 * @param user_group ユーザーグループ
 * @param created_user 作成ユーザー
 * @param modified_user 更新ユーザー
 * @param created 作成日
 * @param modified 更新日
 */
case class FormTransferTask(
                             id: BigInt,
                             transfer_config_id: BigInt,
                             form_id: BigInt,
                             task_index: Int,
                             name: String,
                             user_group: String,
                             created_user: String,
                             modified_user: String,
                             created: ZonedDateTime,
                             modified: ZonedDateTime
                           ){
}

object FormTransferTask extends SQLSyntaxSupport[FormTransferTask] {
  override val tableName = "D_FORM_TRANSFER_TASK"

  def apply(rs: WrappedResultSet): FormTransferTask = {
    FormTransferTask(
      rs.bigInt("id"),
      rs.bigInt("transfer_config_id"),
      rs.bigInt("form_id"),
      rs.int("task_index"),
      rs.string("name"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }
}