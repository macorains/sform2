package net.macolabo.sform2.models.security

import org.pac4j.core.context.WebContext
import org.pac4j.core.context.session.SessionStore
import org.pac4j.core.credentials.Credentials
import org.pac4j.core.credentials.authenticator.Authenticator
import org.pac4j.core.exception.CredentialsException
import org.pac4j.sql.profile.service.DbProfileService
import play.api.db._

class SqlAuthencator()(implicit ec: DatabaseExecutionContext)  extends Authenticator {

  val db = Databases.apply("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/pac4jtest?allowPublicKeyRetrieval=true&useSSL=false&characterEncoding=UTF8", "default",Map("user"->"pac4j","password"->"Pac4j43210"))

  @Override
  def validate(cred: Credentials, context: WebContext, sessionStore: SessionStore)  = {

    if (cred == null) throw new CredentialsException("No credential")

    // 標準のprofile項目以外にDBから読みたいものあれば、カンマ区切りでフィールド名入れる
    val profile_fields = ""
    val dbProfileService = new DbProfileService(db.dataSource, profile_fields, new DummyPasswordEncoder)

    // DBテーブル名はsetUsersTableで変更可能

    dbProfileService.validate(cred, context, sessionStore)
  }
}
