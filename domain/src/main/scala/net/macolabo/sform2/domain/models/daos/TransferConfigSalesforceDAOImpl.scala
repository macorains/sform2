package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigSalesforce
import scalikejdbc._

class TransferConfigSalesforceDAOImpl extends TransferConfigSalesforceDAO {
  def get(transferConfigId: BigInt)(implicit session: DBSession): Option[TransferConfigSalesforce] = {
    val t = TransferConfigSalesforce.syntax("t")
    withSQL (
      select(
        t.id,
        t.transfer_config_id,
        t.api_url,
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
}
