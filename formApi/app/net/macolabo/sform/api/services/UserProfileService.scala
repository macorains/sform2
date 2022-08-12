package net.macolabo.sform.api.services

import com.google.inject.Inject
import net.macolabo.sform.api.models.daos.ApiTokenDAO
import org.pac4j.core.context.WebContext
import org.pac4j.core.context.session.SessionStore
import org.pac4j.core.credentials.password.PasswordEncoder
import org.pac4j.core.credentials.{Credentials, UsernamePasswordCredentials}
import org.pac4j.core.exception.{AccountNotFoundException, BadCredentialsException, MultipleAccountsFoundException, TechnicalException}
import org.pac4j.core.profile.definition.CommonProfileDefinition
import org.pac4j.core.profile.service.AbstractProfileService
import org.pac4j.core.util.CommonHelper._
import org.pac4j.core.util.Pac4jConstants._
import org.pac4j.core.util.serializer.JsonSerializer
import org.pac4j.sql.profile.DbProfile
import scalikejdbc.DB

import java.util
import java.util.UUID
import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters._

class UserProfileService @Inject()(
    apiTokenDAO: ApiTokenDAO
)(
  attributes: String,
  passwordEncoder: PasswordEncoder
)(implicit ex: ExecutionContext)
  extends AbstractProfileService[DbProfile]  {

  setAttributes(attributes)
  setPasswordEncoder(passwordEncoder)

  override protected def internalInit(): Unit = {
    assertNotNull("passwordEncoder", getPasswordEncoder)
    defaultProfileDefinition(new CommonProfileDefinition(_ => new DbProfile()))
    setSerializer(new JsonSerializer(classOf[DbProfile]))
    super.internalInit()
  }

  override protected def read(names: java.util.List[String], key: String, value: String): util.List[util.Map[String, AnyRef]] = {
    DB.localTx(implicit session => {
      val attributesList = names.asScala.toList.mkString(",")
      apiTokenDAO
        .find(attributesList, key, value)
        .map(user =>
          user.toList.map(u => (u._1, u._2.asInstanceOf[AnyRef])).toMap.asJava)
        .asJava
    })
  }

  override def validate(cred: Credentials, context: WebContext, sessionStore: SessionStore): Unit = {
    init()
    assertNotNull("credentials", cred)
    val credentials = cred.asInstanceOf[UsernamePasswordCredentials]
    val username = credentials.getUsername
    val password = credentials.getPassword
    assertNotBlank(USERNAME, username)
    assertNotBlank(PASSWORD, password)

    val attributesToRead = defineAttributesToRead()
    attributesToRead.add(getPasswordAttribute)

    try {
      val listAttributes = read(attributesToRead, getUsernameAttribute, username)
      if(listAttributes == null || listAttributes.isEmpty) {
        println("No account found for: " + username)
        throw new AccountNotFoundException("No account found for: " + username)
      } else if (listAttributes.size() > 1) {
        println("Too many accounts found for: " + username)
        throw new MultipleAccountsFoundException("Too many accounts found for: " + username)
      } else {
        val retrievedPassword = listAttributes.get(0).get(getPasswordAttribute).asInstanceOf[String]
        if (!passwordEncoder.matches(password, retrievedPassword)) {
          println("Bad credentials for: " + username)
          throw new BadCredentialsException("Bad credentials for: " + username)
        } else {
          val profile = convertAttributesToProfile(listAttributes, null)
          credentials.setUserProfile(profile)
          // 401エラーの時のhogeはDemoHttpActionAdapterに定義されている
        }
      }
    } catch {
      case e: TechnicalException =>
        logger.debug("Authentication error", e)
        throw e
    }
  }

  // API側では使わないので実装しない
  override def insert(attributes: util.Map[String, AnyRef]): Unit = ???

  // API側では使わないので実装しない
  override def update(attributes: util.Map[String, AnyRef]): Unit = ???

  // API側では使わないので実装しない
  override def deleteById(id: String): Unit = ???
}
