package net.macolabo.sform2.model.daos

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import net.macolabo.sform2.helper.SformTestHelper
import net.macolabo.sform2.models.entity.user.AuthInfo
import org.scalatest.flatspec.FixtureAnyFlatSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import scalikejdbc._
import scalikejdbc.scalatest.AutoRollback

import scala.concurrent.ExecutionContext.Implicits.global

class SqlAuthinfoDAOSpec extends FixtureAnyFlatSpec  with GuiceOneServerPerSuite with SformTestHelper with AutoRollback {

  behavior of "AuthInfo"

  it should "Find AuthInfo and return PasswordInfo" in { implicit session =>
    val authInfoDAO = new SqlAuthInfoDAO()
    val loginInfo = LoginInfo("hoge","fuga")
    val passowrdInfo = authInfoDAO._find(loginInfo)

    passowrdInfo.map(pi => {
      val info = pi.get
      assert(info.hasher.nonEmpty)
      assert(info.password.nonEmpty)
      assert(info.salt.nonEmpty)
    })
  }

  it should "Create AuthInfo" in { implicit session =>
    val authInfoDAO = new SqlAuthInfoDAO()
    val loginInfo = LoginInfo("hoge2","fuga2")
    val passwordInfo = PasswordInfo("hash2", "password", Some("salt2"))
    authInfoDAO._save(loginInfo, passwordInfo)

    val newPasswordInfo = authInfoDAO._find(loginInfo)
    newPasswordInfo.map(pi => {
      assert(pi.nonEmpty)
      val info = pi.get
      assert(info.hasher.equals("hash2"))
      assert(info.password.equals("password"))
      assert(info.salt.get.equals("salt2"))
    })
  }

  it should "Update AuthInfo" in { implicit session =>
    val authInfoDAO = new SqlAuthInfoDAO()
    val loginInfo = LoginInfo("hoge","fuga")
    val passwordInfo = PasswordInfo("zzz", "password", Some("suger"))
    authInfoDAO._save(loginInfo, passwordInfo)

    val newPasswordInfo = authInfoDAO._find(loginInfo)
    newPasswordInfo.map(pi => {
      assert(pi.nonEmpty)
      val info = pi.get
      assert(info.hasher.equals("zzz"))
      assert(info.password.equals("password"))
      assert(info.salt.get.equals("suger"))
    })
  }

  /*
 Fixture
 */
  override def fixture(implicit session: DBSession): Unit = {
    withSQL {
      val c = AuthInfo.column
      insertInto(AuthInfo).namedValues(
        c.authinfo_id -> 99,
        c.provider_id -> "hoge",
        c.provider_key -> "fuga",
        c.hasher -> "xxx",
        c.password -> "yyy",
        c.salt -> "suger"
      )
    }.update().apply()
  }
}
