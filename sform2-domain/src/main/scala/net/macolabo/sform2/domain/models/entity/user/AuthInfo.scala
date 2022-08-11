package net.macolabo.sform2.domain.models.entity.user

import scalikejdbc._

case class AuthInfo (
  authinfo_id: BigInt,
  provider_id: String,
  provider_key: String,
  hasher: Option[String],
  password: Option[String],
  salt: Option[String]
                    )

object AuthInfo extends SQLSyntaxSupport[AuthInfo] {
  override val tableName =  "m_authinfo"
  def apply(rs: WrappedResultSet): AuthInfo = {
    AuthInfo(
      rs.bigInt("authinfo_id"),
      rs.string("provider_id"),
      rs.string("provider_key"),
      rs.stringOpt("hasher"),
      rs.stringOpt("password"),
      rs.stringOpt("salt")
    )
  }
}
