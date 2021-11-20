package net.macolabo.sform2.model.daos

import com.google.inject.Inject
import net.macolabo.sform2.helper.SformTestHelper
import net.macolabo.sform2.models.daos.UserDAOImpl
import net.macolabo.sform2.models.entity.user.User
import org.scalatest.flatspec.FixtureAnyFlatSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import scalikejdbc._
import scalikejdbc.scalatest.AutoRollback

import java.util.UUID

class UserDAOImplSpec extends FixtureAnyFlatSpec  with GuiceOneServerPerSuite with SformTestHelper with AutoRollback {
  behavior of "User"

  val username = "hoge"

  // def find(fields: String, key: String, value: String): List[Map[String, Any]]
  it should "find by keyvalue" in { implicit session =>
    val userDAO = new UserDAOImpl()
    // 実行
    val result = userDAO.find("USER_ID,USERNAME,PASSWORD,USER_GROUP,ROLE","USERNAME",username)

    // 結果見る
    val user = result.head
    println(user)
    assert(user("USER_ID").asInstanceOf[String].size > 0)
    assert(user("USERNAME").equals("hoge"))
    assert(user("PASSWORD").equals("fuga"))
    assert(user("USER_GROUP").equals("gg"))
    assert(user("ROLE").equals("admin"))
  }

  // save
  it should "insert user" in { implicit session =>
    val userId = UUID.randomUUID().toString
    val data :Seq[(String, AnyRef)]= Seq(
      ("user_id",userId),
      ("username","hoge2"),
      ("password","fuga2"),
      ("user_group","test2"),
      ("activated",false.asInstanceOf[AnyRef]),
      ("deletable",true.asInstanceOf[AnyRef])
    )
    val userDAO = new UserDAOImpl()
    userDAO.insert(data)

    val u = User.syntax("u")
    val result = withSQL (
      select(
        u.user_id,
        u.username,
        u.password,
        u.user_group,
        u.activated,
        u.deletable
      )
        .from(User as u)
        .where
        .eq(u.user_id, userId)
    ).map(rs => rs.toMap()).list().apply()

    val datamap = data.toMap
    val newuser = result.head
    println(newuser)
    assert(result.nonEmpty)
    assert(newuser("user_id").equals(datamap("user_id")))
    assert(newuser("username").equals(datamap("username")))
    assert(newuser("password").equals(datamap("password")))
    assert(newuser("user_group").equals(datamap("user_group")))
    assert((newuser("activated") == 1).equals(datamap("activated").asInstanceOf[Boolean]))
    assert((newuser("deletable") == 1).equals(datamap("deletable").asInstanceOf[Boolean]))
  }

  // delete
  // update

  /*
  Fixture
  */
  override def fixture(implicit session: DBSession): Unit = {
    withSQL {
      val c = User.column
      insertInto(User).namedValues(
        c.user_id -> UUID.randomUUID().toString,
        c.username -> "hoge",
        c.password -> "fuga",
        c.user_group -> "gg",
        c.role -> "admin",
        c.first_name -> "ほげ",
        c.last_name -> "ふが",
        c.full_name -> "ほげふが",
        c.email -> "hogefuga@macolabo.net",
        c.avatar_url -> "http://macolabo.net/hogeavatar",
        c.activated -> true,
        c.deletable -> true
      )
    }.update().apply()
  }
}
