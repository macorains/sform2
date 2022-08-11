package net.macolabo.sform2.domain.services.User

import com.google.inject.Inject
import net.macolabo.sform2.domain.models.daos.UserDAO
import scalikejdbc.DB
import org.pac4j.core.context.WebContext
import org.pac4j.core.context.session.SessionStore
import org.pac4j.core.credentials.{Credentials, UsernamePasswordCredentials}
import org.pac4j.core.credentials.password.PasswordEncoder
import org.pac4j.core.exception.{AccountNotFoundException, BadCredentialsException, CredentialsException, MultipleAccountsFoundException, TechnicalException}
import org.pac4j.core.profile.definition.CommonProfileDefinition
import org.pac4j.core.profile.service.AbstractProfileService
import org.pac4j.sql.profile.DbProfile
import org.pac4j.core.util.CommonHelper._
import org.pac4j.core.util.serializer.JsonSerializer
import org.pac4j.core.util.Pac4jConstants._

import java.util
import java.util.UUID
import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters._

class UserProfileService @Inject()(
  userDAO: UserDAO
) (
  attributes: String,
  passwordEncoder: PasswordEncoder
)(implicit ex: ExecutionContext)
  extends AbstractProfileService[DbProfile]  {

  val usersTable = "users"
  val profilePrefix = "PR_"
  val verificationKeyPrefix = "VC_"
  val credentialPrefix = "CR_"
  setAttributes(attributes)
  setPasswordEncoder(passwordEncoder)


  override protected def internalInit = {
    assertNotNull("passwordEncoder", getPasswordEncoder)
    defaultProfileDefinition(new CommonProfileDefinition(_ => new DbProfile()))
    setSerializer(new JsonSerializer(classOf[DbProfile]))
    super.internalInit()
  }

  override protected def insert(attributes: util.Map[String, AnyRef]) = {
    DB.localTx(implicit session => {
      userDAO.insert(attributes.asScala.toSeq)
    })
  }

  override protected def update(attributes: util.Map[String, AnyRef]) = {
    DB.localTx(implicit session => {
      userDAO.update(attributes.asScala.toSeq)
    })
  }

  override protected def deleteById(id: String): Unit = {
    userDAO.delete(id)
  }

  protected def execute(query: String, args: Any *) = {

  }

  override protected def read(names: java.util.List[String], key: String, value: String): util.List[util.Map[String, AnyRef]] = {
    DB.localTx(implicit session => {
      val attributesList = names.asScala.toList.mkString(",")
      userDAO
        .find(attributesList, key, value)
        .map(user =>
          user.toList.map(u => (u._1, u._2.asInstanceOf[AnyRef])).toMap.asJava)
        .asJava
    })
  }

  override def validate(cred: Credentials, context: WebContext, sessionStore: SessionStore) = {
    println("init()")
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
      val listAttributes = read(attributesToRead, getUsernameAttribute(), username)
      if(listAttributes == null || listAttributes.isEmpty()) {
        println("No account found for: " + username)
        throw new AccountNotFoundException("No account found for: " + username)
      } else if (listAttributes.size() > 1) {
        println("Too many accounts found for: " + username)
        throw new MultipleAccountsFoundException("Too many accounts found for: " + username)
      } else {
        val retrievedPassword = listAttributes.get(0).get(getPasswordAttribute()).asInstanceOf[String]
        if (!passwordEncoder.matches(password, retrievedPassword)) {
          println("Bad credentials for: " + username)
          throw new BadCredentialsException("Bad credentials for: " + username)
        } else {
          val profile = convertAttributesToProfile(listAttributes, null)

          // 認証キーを生成する
          val uuid = UUID.randomUUID.toString
          profile.addAttribute("AuthKey", uuid)

          // 確認コードを作る
          val verificationCode = (Math.floor(Math.random * 899999).toInt + 100000).toString
          profile.addAttribute("VerificationCode", verificationCode)

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
/*
  override def validate(cred: Credentials, context: WebContext, sessionStore: SessionStore) = {
    init()
    // context.getRequestParameter("hoge")でランダム文字列と確認コード取れるかな？
    // ランダム文字列・確認コードの組み合わせチェック
    // ランダム文字列に紐づいたprofileをsessionStoreから取り出してセット

    // 後はコントローラ側でJWTトークン作ってhederにセットすればOK
    //  context.setResponseHeader("PPP", "HHH")

    //println("*** sessionStore.get ***")
    //println(sessionStore.get(context, "hoge"))

    val authKey = context.getRequestParameter("authkey").get
    val verificationCode = context.getRequestParameter("verification_code").get()

    println(s"authKey: $authKey, verificationCode: $verificationCode")

    sessionStore.get(context, verificationKeyPrefix + authKey).map(vkey => {
      if(vkey.equals(verificationCode)) {
        println("hoge 1")
        sessionStore.get(context, profilePrefix + authKey).map(profile => {
          println("hoge 2")
          //val credentials = cred.asInstanceOf[UsernamePasswordCredentials]
          //val credentials = sessionStore.get(context, credentialPrefix + authKey).get().asInstanceOf[UsernamePasswordCredentials]
          //println(credentials.toString)
          sessionStore.get(context, credentialPrefix + authKey).map(cred => {
            val credentials = cred.asInstanceOf[UsernamePasswordCredentials]
            credentials.setUserProfile(profile.asInstanceOf[DbProfile])
          })
        })
      }
    })

//    val profile = sessionStore.get(context, "hoge").get().asInstanceOf[DbProfile]
//    // profile
//    val credentials = cred.asInstanceOf[UsernamePasswordCredentials]
//    credentials.setUserProfile(profile)
  }

*/
  /*
      public void validate(final Credentials cred, final WebContext context, final SessionStore sessionStore) {
        init();

        assertNotNull("credentials", cred);
        final var credentials = (UsernamePasswordCredentials) cred;
        final var username = credentials.getUsername();
        final var password = credentials.getPassword();
        assertNotBlank(USERNAME, username);
        assertNotBlank(PASSWORD, password);

        final var attributesToRead = defineAttributesToRead();
        // + password to check
        attributesToRead.add(getPasswordAttribute());

        try {
            final var listAttributes = read(attributesToRead, getUsernameAttribute(), username);
            if (listAttributes == null || listAttributes.isEmpty()) {
                throw new AccountNotFoundException("No account found for: " + username);
            } else if (listAttributes.size() > 1) {
                throw new MultipleAccountsFoundException("Too many accounts found for: " + username);
            } else {
                final var retrievedPassword = (String) listAttributes.get(0).get(getPasswordAttribute());
                // check password
                if (!passwordEncoder.matches(password, retrievedPassword)) {
                    throw new BadCredentialsException("Bad credentials for: " + username);
                } else {
                    final var profile = convertAttributesToProfile(listAttributes, null);
                    credentials.setUserProfile(profile);
                }
            }

        } catch (final TechnicalException e) {
            logger.debug("Authentication error", e);
            throw e;
        }
    }

   */

  protected def query(query: String, key: String, value: String): util.List[util.Map[String, AnyRef]] = {
    ???
//    var h = null
//    try {
//      h = dbi.open
//      logger.debug("Query: {} for key/value: {} / {}", query, key, value)
//      h.createQuery(query).bind(key, value).list(2)
//    } finally if (h != null) h.close()
  }


}
