package net.macolabo.sform.api.security

import com.google.inject.Inject
import net.macolabo.sform.api.models.daos.ApiTokenDAO
import net.macolabo.sform.api.services.UserProfileService
import org.apache.shiro.authc.credential.DefaultPasswordService
import org.pac4j.core.context.WebContext
import org.pac4j.core.context.session.SessionStore
import org.pac4j.core.credentials.Credentials
import org.pac4j.core.credentials.authenticator.Authenticator
import org.pac4j.core.credentials.password.ShiroPasswordEncoder
import org.pac4j.core.exception.CredentialsException

class SqlAuthencator @Inject() (
  apiTokenDAO: ApiTokenDAO
)(implicit ec: DatabaseExecutionContext)  extends Authenticator {

  @Override
  def validate(cred: Credentials, context: WebContext, sessionStore: SessionStore): Unit = {
    if (cred == null) throw new CredentialsException("No credential")

    // 標準のprofile項目以外にDBから読みたいものあれば、カンマ区切りでフィールド名入れる
    val profile_fields = "user_group"
    val userProfileService = new UserProfileService(apiTokenDAO)(profile_fields, new ShiroPasswordEncoder(new DefaultPasswordService))

    // DBテーブル名はsetUsersTableで変更可能
    userProfileService.validate(cred, context, sessionStore)
  }
}
