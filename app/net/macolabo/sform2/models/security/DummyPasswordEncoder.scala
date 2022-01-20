package net.macolabo.sform2.models.security

import org.pac4j.core.credentials.password.PasswordEncoder

class DummyPasswordEncoder extends PasswordEncoder {

  override def encode(password: String): String = password

  override def matches(plainPassword: String, encodedPassword: String): Boolean = plainPassword.equals(encodedPassword)

}
