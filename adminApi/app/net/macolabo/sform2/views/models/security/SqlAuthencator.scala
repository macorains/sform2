package net.macolabo.sform2.views.models.security

import com.google.inject.Inject
import net.macolabo.sform2.domain.models.daos.UserDAO
import net.macolabo.sform2.domain.services.User.UserProfileService
import org.apache.shiro.authc.credential.DefaultPasswordService
import org.pac4j.core.context.{CallContext, WebContext}
import org.pac4j.core.context.session.SessionStore
import org.pac4j.core.credentials.Credentials
import org.pac4j.core.credentials.authenticator.Authenticator
import org.pac4j.core.credentials.password.ShiroPasswordEncoder
import org.pac4j.core.exception.CredentialsException

import java.util.Optional

class SqlAuthencator @Inject() (
  userDAO: UserDAO
)(implicit ec: DatabaseExecutionContext)  extends Authenticator {

  //val db = Databases.apply("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/pac4jtest?allowPublicKeyRetrieval=true&useSSL=false&characterEncoding=UTF8", "default",Map("user"->"pac4j","password"->"Pac4j43210"))

  @Override
  def validate(context: CallContext, cred: Credentials): Optional[Credentials] = {
    if (cred == null) throw new CredentialsException("No credential")

    // 標準のprofile項目以外にDBから読みたいものあれば、カンマ区切りでフィールド名入れる
    val profile_fields = "user_group,email"
    val userProfileService = new UserProfileService(userDAO)(profile_fields, new ShiroPasswordEncoder(new DefaultPasswordService))

    // DBテーブル名はsetUsersTableで変更可能

    //dbProfileService.validate(cred, context, sessionStore)
    // userProfileService.validate(cred, context, sessionStore)

    userProfileService.validate(context, cred)

  }
}
