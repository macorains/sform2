package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigSalesforceObjectField
import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigSalesforceObjectField.autoSession
import scalikejdbc._

class TransferConfigSalesforceObjectFieldDAOImpl extends TransferConfigSalesforceObjectFieldDAO {

  /**
   * TransferConfigSalesforceObjectFieldのリスト取得
   *
   * @param userGroup                        ユーザーグループ
   * @param transferConfigSalesforceObjectId TransferConfigSalesforceObject ID
   * @param session                          DB Session
   * @return TransferConfigSalesforceObjectFieldのリスト
   */
  def getList(userGroup: String, transferConfigSalesforceObjectId: Option[BigInt])(implicit session: DBSession = autoSession): List[TransferConfigSalesforceObjectField] = {
    transferConfigSalesforceObjectId.map(id => {
      val f = TransferConfigSalesforceObjectField.syntax("f")
      withSQL(
        select(
          f.id,
          f.transfer_config_salesforce_object_id,
          f.name,
          f.label,
          f.field_type,
          f.active,
          f.user_group,
          f.created_user,
          f.modified_user,
          f.created,
          f.modified
        )
          .from(TransferConfigSalesforceObjectField as f)
          .where
          .eq(f.transfer_config_salesforce_object_id, id)
          .and
          .eq(f.user_group, userGroup)
      ).map(rs => TransferConfigSalesforceObjectField(rs)).list().apply()
    }).getOrElse(List.empty)
  }

  /**
   * TransferConfigSalesforceObjectField作成
   *
   * @param transferConfigSalesforceObjectField TransferConfigSalesforceObjectField
   * @param session                             DB Session
   * @return 作成したレコードのID
   */
  def create(transferConfigSalesforceObjectField: TransferConfigSalesforceObjectField)(implicit session: DBSession = autoSession): BigInt = {
    withSQL {
      val c = TransferConfigSalesforceObjectField.column
      insert.into(TransferConfigSalesforceObjectField).namedValues(
        c.transfer_config_salesforce_object_id -> transferConfigSalesforceObjectField.transfer_config_salesforce_object_id,
        c.name -> transferConfigSalesforceObjectField.name,
        c.label -> transferConfigSalesforceObjectField.label,
        c.active -> transferConfigSalesforceObjectField.active,
        c.field_type -> transferConfigSalesforceObjectField.field_type,
        c.user_group -> transferConfigSalesforceObjectField.user_group,
        c.created_user -> transferConfigSalesforceObjectField.created_user,
        c.modified_user -> transferConfigSalesforceObjectField.modified_user,
        c.created -> transferConfigSalesforceObjectField.created,
        c.modified -> transferConfigSalesforceObjectField.modified
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  /**
   * TransferConfigSalesforceObjectField保存
   *
   * @param transferConfigSalesforceObjectField TransferConfigSalesforceObjectField
   * @param session                             DB Session
   * @return result
   */
  def save(transferConfigSalesforceObjectField: TransferConfigSalesforceObjectField)(implicit session: DBSession = autoSession): BigInt = {
    withSQL {
      val c = TransferConfigSalesforceObjectField.column
      update(TransferConfigSalesforceObjectField).set(
        c.transfer_config_salesforce_object_id -> transferConfigSalesforceObjectField.transfer_config_salesforce_object_id,
        c.name -> transferConfigSalesforceObjectField.name,
        c.label -> transferConfigSalesforceObjectField.label,
        c.active -> transferConfigSalesforceObjectField.active,
        c.field_type -> transferConfigSalesforceObjectField.field_type,
        c.user_group -> transferConfigSalesforceObjectField.user_group,
        c.modified_user -> transferConfigSalesforceObjectField.modified_user,
        c.modified -> transferConfigSalesforceObjectField.modified
      ).where.eq(c.id, transferConfigSalesforceObjectField.id)
    }.update().apply()
    transferConfigSalesforceObjectField.id
  }

  /**
   * TransferConfigSalesforceObjectField削除
   *
   * @param userGroup                             ユーザーグループ
   * @param transferConfigSalesforceObjectFieldId TransferConfigSalesforceObjectField ID
   * @param session                               DB Session
   * @return Result
   */
  def delete(userGroup: String, transferConfigSalesforceObjectFieldId: BigInt)(implicit session: DBSession = autoSession): Int = {
    withSQL {
      val c = TransferConfigSalesforceObjectField.column
      deleteFrom(TransferConfigSalesforceObjectField)
        .where
        .eq(c.id, transferConfigSalesforceObjectFieldId)
        .and
        .eq(c.user_group, userGroup)
    }.update().apply()
  }
}
