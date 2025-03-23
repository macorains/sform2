package net.macolabo.sform.api.security

import com.google.inject.Inject
import net.macolabo.sform2.domain.models.daos.ApiTokenDAO
import net.macolabo.sform2.domain.services.User.ApiUserProfileService
import org.apache.shiro.authc.credential.DefaultPasswordService
import org.pac4j.core.context.{CallContext, WebContext}
import org.pac4j.core.context.session.SessionStore
import org.pac4j.core.credentials.Credentials
import org.pac4j.core.credentials.authenticator.Authenticator
import org.pac4j.core.credentials.password.ShiroPasswordEncoder
import org.pac4j.core.exception.CredentialsException

import java.util.Optional

class SqlAuthencator @Inject() (
  apiTokenDAO: ApiTokenDAO
)(implicit ec: DatabaseExecutionContext)  extends Authenticator {

  @Override
  def validate(context: CallContext, cred: Credentials): Optional[Credentials] = {
    if (cred == null) throw new CredentialsException("No credential")

    // 標準のprofile項目以外にDBから読みたいものあれば、カンマ区切りでフィールド名入れる
    val profile_fields = "user_group"
    val userProfileService = new ApiUserProfileService(apiTokenDAO)(profile_fields, new ShiroPasswordEncoder(new DefaultPasswordService))

    // DBテーブル名はsetUsersTableで変更可能
    userProfileService.validate(context, cred)
  }
}
