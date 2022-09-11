package net.macolabo.sform2.domain.models.entity.transfer

import scalikejdbc._

import java.time.ZonedDateTime

case class TransferConfigSalesforce(
  id: BigInt,
  transfer_config_id: BigInt,
  sf_domain: String,
  api_version: String,
  sf_user_name: String,
  sf_password: String,
  sf_client_id: String,
  sf_client_secret: String,
  iv_user_name: String,
  iv_password: String,
  iv_client_id: String,
  iv_client_secret: String,
  user_group: String,
  created_user: String,
  modified_user: String,
  created: ZonedDateTime,
  modified: ZonedDateTime
){
  import TransferConfigSalesforce._
  def insert: BigInt = create(this)
  def update: BigInt = save(this)
}


object TransferConfigSalesforce extends SQLSyntaxSupport[TransferConfigSalesforce] {
  override val tableName = "d_transfer_config_salesforce"
  def apply(rs:WrappedResultSet): TransferConfigSalesforce = {
    TransferConfigSalesforce(
      rs.bigInt("id"),
      rs.bigInt("transfer_config_id"),
      rs.string("sf_domain"),
      rs.string("api_version"),
      rs.string("sf_user_name"),
      rs.string("sf_password"),
      rs.string("sf_client_id"),
      rs.string("sf_client_secret"),
      rs.string("iv_user_name"),
      rs.string("iv_password"),
      rs.string("iv_client_id"),
      rs.string("iv_client_secret"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }

  // TODO DAO側に統合する
  /**
   * TransferConfigSalesforce取得
   * @param userGroup ユーザーグループ
   * @param transferConfigId TransferConfig ID
   * @param session DB Session
   * @return TransferConfigSalesforce
   */
  def get(userGroup: String, transferConfigId: BigInt)(implicit session: DBSession = autoSession): Option[TransferConfigSalesforce] = {
    val f = TransferConfigSalesforce.syntax("f")
    withSQL(
      select(
        f.id,
        f.transfer_config_id,
        f.sf_domain,
        f.api_version,
        f.sf_user_name,
        f.sf_password,
        f.sf_client_id,
        f.sf_client_secret,
        f.iv_user_name,
        f.iv_password,
        f.iv_client_id,
        f.iv_client_secret,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(TransferConfigSalesforce as f)
        .where
        .eq(f.transfer_config_id, transferConfigId)
        .and
        .eq(f.user_group, userGroup)
    ).map(rs=>TransferConfigSalesforce(rs)).single().apply()
  }

  // TODO DAO側に統合する
  /**
   * TransferConfigSalesforce作成
   * @param transferConfigSalesforce TransferConfigSalesforce
   * @param session DB Session
   * @return 作成したレコードのID
   */
  def create(transferConfigSalesforce: TransferConfigSalesforce)(implicit session: DBSession = autoSession): BigInt = {
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

  // TODO DAO側に統合する
  /**
   * TransferConfigSalesforce更新
   * @param transferConfigSalesforce TransferConfigSalesforce
   * @param session DB Session
   * @return Result
   */
  def save(transferConfigSalesforce: TransferConfigSalesforce)(implicit session: DBSession = autoSession): BigInt = {
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
}
