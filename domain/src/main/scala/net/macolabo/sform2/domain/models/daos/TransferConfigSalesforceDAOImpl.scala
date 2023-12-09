package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigSalesforce
import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigSalesforce.autoSession
import scalikejdbc._

class TransferConfigSalesforceDAOImpl extends TransferConfigSalesforceDAO {

  /**
   * TransferConfigSalesforce取得
   *
   * @param transferConfigId Id
   * @param session DB Session
   * @return 作成したレコードのID
   */
  def get(transferConfigId: BigInt)(implicit session: DBSession = autoSession): Option[TransferConfigSalesforce] = {
    val t = TransferConfigSalesforce.syntax("t")
    withSQL (
      select(
        t.id,
        t.transfer_config_id,
        t.sf_domain,
        t.api_version,
        t.sf_user_name,
        t.sf_password,
        t.sf_client_id,
        t.sf_client_secret,
        t.iv_user_name,
        t.iv_password,
        t.iv_client_id,
        t.iv_client_secret,
        t.user_group,
        t.created_user,
        t.modified_user,
        t.created,
        t.modified
      )
        .from(TransferConfigSalesforce as t)
        .where
        .eq(t.transfer_config_id, transferConfigId)
    ).map(rs => TransferConfigSalesforce(rs)).single().apply()
  }

  /**
   * TransferConfigSalesforce作成
   * @param transferConfigSalesforce TransferConfigSalesforce
   * @param session DB Session
   * @return 作成したレコードのID
   */
  def create(transferConfigSalesforce: TransferConfigSalesforce)(implicit session: DBSession = autoSession): BigInt = {
    // TODO sfの認証情報のivを取得する処理と暗号化を追加
    withSQL{
      val c = TransferConfigSalesforce.column
      insert.into(TransferConfigSalesforce).namedValues(
        c.transfer_config_id -> transferConfigSalesforce.transfer_config_id,
        c.sf_domain -> transferConfigSalesforce.sf_domain,
        c.api_version -> transferConfigSalesforce.api_version,
        c.sf_user_name -> transferConfigSalesforce.sf_user_name,
        c.sf_password -> transferConfigSalesforce.sf_password,
        c.sf_client_id -> transferConfigSalesforce.sf_client_id,
        c.sf_client_secret -> transferConfigSalesforce.sf_client_secret,
        c.iv_user_name -> transferConfigSalesforce.iv_user_name,
        c.iv_password -> transferConfigSalesforce.iv_password,
        c.iv_client_id -> transferConfigSalesforce.iv_client_id,
        c.iv_client_secret -> transferConfigSalesforce.iv_client_secret,
        c.user_group -> transferConfigSalesforce.user_group,
        c.created_user -> transferConfigSalesforce.created_user,
        c.modified_user -> transferConfigSalesforce.modified_user,
        c.created -> transferConfigSalesforce.created,
        c.modified -> transferConfigSalesforce.modified
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  /**
   * TransferConfigSalesforce更新
   * @param transferConfigSalesforce TransferConfigSalesforce
   * @param session DB Session
   * @return Result
   */
  def save(transferConfigSalesforce: TransferConfigSalesforce)(implicit session: DBSession = autoSession): BigInt = {
    // TODO sfの認証情報のivを取得する処理と暗号化を追加
    withSQL{
      val c = TransferConfigSalesforce.column
      update(TransferConfigSalesforce).set(
        c.transfer_config_id -> transferConfigSalesforce.transfer_config_id,
        c.sf_domain -> transferConfigSalesforce.sf_domain,
        c.api_version -> transferConfigSalesforce.api_version,
        c.sf_user_name -> transferConfigSalesforce.sf_user_name,
        c.sf_password -> transferConfigSalesforce.sf_password,
        c.sf_client_id -> transferConfigSalesforce.sf_client_id,
        c.sf_client_secret -> transferConfigSalesforce.sf_client_secret,
        c.iv_user_name -> transferConfigSalesforce.iv_user_name,
        c.iv_password -> transferConfigSalesforce.iv_password,
        c.iv_client_id -> transferConfigSalesforce.iv_client_id,
        c.iv_client_secret -> transferConfigSalesforce.iv_client_secret,
        c.user_group -> transferConfigSalesforce.user_group,
        c.created_user -> transferConfigSalesforce.created_user,
        c.modified_user -> transferConfigSalesforce.modified_user,
        c.created -> transferConfigSalesforce.created,
        c.modified -> transferConfigSalesforce.modified
      ).where.eq(c.id, transferConfigSalesforce.id)
    }.update().apply()
  }

  def delete(userGroup: String, transferConfigId: BigInt)(implicit session: DBSession = autoSession): Int = {
    withSQL {
      val c = TransferConfigSalesforce.column
      deleteFrom(TransferConfigSalesforce)
        .where
        .eq(c.id, transferConfigId)
    }.update().apply()
  }
}
