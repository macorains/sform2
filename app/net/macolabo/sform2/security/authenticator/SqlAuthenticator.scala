package net.macolabo.sform2.security.authenticator

import org.pac4j.core.context.WebContext
import org.pac4j.core.context.session.SessionStore
import org.pac4j.core.credentials.{Credentials, UsernamePasswordCredentials}
import org.pac4j.core.credentials.authenticator.Authenticator
import org.pac4j.core.exception.CredentialsException
import org.pac4j.core.util.CommonHelper
import play.api.db._
import scalikejdbc.DB

class SqlAuthenticator extends Authenticator {

  @Override
  def validate(cred: Credentials, context: WebContext, sessionStore: SessionStore) = {
    if (cred == null) throw new CredentialsException("No credential")
    val credentials = cred.asInstanceOf[UsernamePasswordCredentials]
    val username = credentials.getUsername
    val password = credentials.getPassword
    if (CommonHelper.isBlank(username)) throw new CredentialsException("Username cannot be blank")
    if (CommonHelper.isBlank(password)) throw new CredentialsException("Password cannot be blank")

    // DBProfileServiceを使う


    }
}