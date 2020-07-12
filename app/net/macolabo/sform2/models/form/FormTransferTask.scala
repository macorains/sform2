package net.macolabo.sform2.models.form

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
                       id: Int,
                       transfer_config_id: Int,
                       form_id: Int,
                       task_index: Int,
                       name: String,
                       user_group: String,
                       created_user: String,
                       modified_user: String,
                       created: ZonedDateTime,
                       modified: ZonedDateTime
                       )

object FormTransferTask extends SQLSyntaxSupport[FormTransferTask] {
  override val tableName = "D_FORM_TRANSFER_TASK"
  def apply(rs:WrappedResultSet): FormTransferTask = {
    FormTransferTask(
      rs.int("id"),
      rs.int("transfer_config_id"),
      rs.int("form_id"),
      rs.int("task_index"),
      rs.string("name"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }

  /**
   * TransferTask取得
   * @param transferTaskId TransferTask ID
   * @param session DB Session
   * @return TransferTask
   */
  def get(userGroup: String, transferTaskId: Int)(implicit session: DBSession = autoSession): Option[FormTransferTask] = {
    val f = FormTransferTask.syntax("f")
    withSQL(
      select(
        f.id,
        f.transfer_config_id,
        f.form_id,
        f.task_index,
        f.name,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(FormTransferTask as f)
        .where
        .eq(f.id, transferTaskId)
        .and
        .eq(f.user_group, userGroup)
    ).map(rs => FormTransferTask(rs)).single().apply()
  }

  /**
   * TransferTaskのリスト取得
   * @param formId フォームID
   * @param session DB Session
   * @return TransferTaskのリスト
   */
  def getList(userGroup: String, formId: Int)(implicit session: DBSession = autoSession): List[FormTransferTask] = {
    val f = FormTransferTask.syntax("f")
    withSQL(
      select(
        f.id,
        f.transfer_config_id,
        f.form_id,
        f.task_index,
        f.name,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(FormTransferTask as f)
        .where
        .eq(f.form_id, formId)
        .and
        .eq(f.user_group, userGroup)
    ).map(rs => FormTransferTask(rs)).list().apply()
  }

  /**
   * TransferTaskの作成
   * @param formTransferTask TransferTaskのデータ
   * @param session DB Session
   * @return FormTransferTaskのID
   */
  def create(formTransferTask: FormTransferTask)(implicit session: DBSession = autoSession): Int = {
    withSQL {
      val c = FormTransferTask.column
      insert
        .into(FormTransferTask)
        .namedValues(
          c.transfer_config_id -> formTransferTask.transfer_config_id,
          c.form_id -> formTransferTask.form_id,
          c.task_index -> formTransferTask.task_index,
          c.name -> formTransferTask.name,
          c.user_group -> formTransferTask.user_group,
          c.created_user -> formTransferTask.created_user,
          c.modified_user -> formTransferTask.modified_user,
          c.created -> formTransferTask.created,
          c.modified -> formTransferTask.modified
        )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  /**
   * TransferTaskの更新
   * @param formTransferTask TransferTaskのデータ
   * @param session DB Session
   * @return
   */
  def save(formTransferTask: FormTransferTask)(implicit session: DBSession = autoSession): Int = {
    withSQL {
      val c = FormTransferTask.column
      update(FormTransferTask)
        .set(
          c.transfer_config_id -> formTransferTask.transfer_config_id,
          c.form_id -> formTransferTask.form_id,
          c.task_index -> formTransferTask.task_index,
          c.user_group -> formTransferTask.user_group,
          c.name -> formTransferTask.name,
          c.created_user -> formTransferTask.created_user,
          c.modified_user -> formTransferTask.modified_user,
          c.created -> formTransferTask.created,
          c.modified -> formTransferTask.modified
        ).where.eq(c.id, formTransferTask.id)
    }.update().apply()
  }
}
