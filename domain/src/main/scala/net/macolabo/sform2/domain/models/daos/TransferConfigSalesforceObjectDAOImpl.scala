package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigSalesforceObject
import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigSalesforceObject.autoSession
import scalikejdbc._

class TransferConfigSalesforceObjectDAOImpl extends TransferConfigSalesforceObjectDAO {

  /**
   * TransferConfigSalesforceObjectのリスト取得
   * @param userGroup ユーザーグループ
   * @param transferConfigSalesforceId TransferConfigSalesforce ID
   * @param session DB Session
   * @return TransferConfigSalesforceObjectのリスト
   */
  def getList(userGroup: String, transferConfigSalesforceId: Option[BigInt])(implicit session: DBSession = autoSession): List[TransferConfigSalesforceObject] = {
    transferConfigSalesforceId.map(id => {
      val f = TransferConfigSalesforceObject.syntax("f")
      withSQL(
        select(
          f.id,
          f.transfer_config_salesforce_id,
          f.name,
          f.label,
          f.active,
          f.user_group,
          f.created_user,
          f.modified_user,
          f.created,
          f.modified
        )
          .from(TransferConfigSalesforceObject as f)
          .where
          .eq(f.transfer_config_salesforce_id, id)
          .and
          .eq(f.user_group, userGroup)
      ).map(rs => TransferConfigSalesforceObject(rs)).list().apply()
    }).getOrElse(List.empty)
  }

  /**
   * TransferConfigSalesforceObject作成
   * @param transferConfigSalesforceObject　TransferConfigSalesforceObject
   * @param session DB Session
   * @return 作成したレコードのID
   */
  def create(transferConfigSalesforceObject: TransferConfigSalesforceObject)(implicit session: DBSession = autoSession): BigInt = {
    withSQL {
      val c = TransferConfigSalesforceObject.column
      insert.into(TransferConfigSalesforceObject).namedValues(
        c.transfer_config_salesforce_id -> transferConfigSalesforceObject.transfer_config_salesforce_id,
        c.name -> transferConfigSalesforceObject.name,
        c.label -> transferConfigSalesforceObject.label,
        c.active -> transferConfigSalesforceObject.active,
        c.user_group -> transferConfigSalesforceObject.user_group,
        c.created_user -> transferConfigSalesforceObject.created_user,
        c.modified_user -> transferConfigSalesforceObject.modified_user,
        c.created -> transferConfigSalesforceObject.created,
        c.modified -> transferConfigSalesforceObject.modified
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  /**
   * TransferConfigSalesforceObject保存
   * @param transferConfigSalesforceObject　TransferConfigSalesforceObject
   * @param session DB Session
   * @return result
   */
  def save(transferConfigSalesforceObject: TransferConfigSalesforceObject)(implicit session: DBSession = autoSession): BigInt = {
    withSQL {
      val c = TransferConfigSalesforceObject.column
      update(TransferConfigSalesforceObject).set(
        c.transfer_config_salesforce_id -> transferConfigSalesforceObject.transfer_config_salesforce_id,
        c.name -> transferConfigSalesforceObject.name,
        c.label -> transferConfigSalesforceObject.label,
        c.active -> transferConfigSalesforceObject.active,
        c.user_group -> transferConfigSalesforceObject.user_group,
        c.modified_user -> transferConfigSalesforceObject.modified_user,
        c.modified -> transferConfigSalesforceObject.modified
      ).where.eq(c.id, transferConfigSalesforceObject.id)
    }.update().apply()
  }

  /**
   * TransferConfigSalesforceObject削除
   * @param userGroup ユーザーグループ
   * @param transferConfigSalesforceObjectId TransferConfigSalesforceObject ID
   * @param session DB Session
   * @return Result
   */
  def delete(userGroup: String, transferConfigSalesforceObjectId: BigInt)(implicit session: DBSession = autoSession): Int = {
    withSQL {
      val c = TransferConfigSalesforceObject.column
      deleteFrom(TransferConfigSalesforceObject)
        .where
        .eq(c.id, transferConfigSalesforceObjectId)
        .and
        .eq(c.user_group, userGroup)
    }.update().apply()
  }
}
