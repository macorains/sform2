package net.macolabo.sform2.models.daos

import java.util.UUID
import net.macolabo.sform2.models.entity.user.User
import play.api.libs.json.{JsValue, Json, Reads, Writes}
import scalikejdbc._
import scalikejdbc.interpolation.SQLSyntax.count

import scala.collection.mutable
import scala.concurrent.{Future, _}
import scala.concurrent.duration.Duration

case class UserJson(
                     user_id: String,
                     username: String,
                     password: String,
                     user_group: String,
                     role: String,
                     first_name: String,
                     last_name: String,
                     full_name: String,
                     email: String,
                     avatar_url: String,
                     activated: Boolean,
                     deletable: Boolean
                   )
object UserJson {
  implicit def jsonUserWrites: Writes[UserJson] = Json.writes[UserJson]
  implicit def jsonUserReads: Reads[UserJson] = Json.reads[UserJson]
}

/**
 * Give access to the user object.
 */
class UserDAOImpl extends UserDAO {

  /**
   * Finds a user by its user ID.
   *
   * @param userID The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  def find(userID: UUID): Future[Option[User]] = {
    Future.successful(
      DB localTx { implicit l =>
        val u = User.syntax("u")
        withSQL(
          select(
            u.id,
            u.username,
            u.password,
            u.user_group,
            u.role,
            u.first_name,
            u.last_name,
            u.full_name,
            u.email,
            u.avatar_url,
            u.activated,
            u.deletable
          )
            .from(User as u)
            .where
            .eq(u.id, userID.toString)
        ).map(rs => User(rs)).single().apply()
      }
    )
  }

  /**
   * Finds a user by its username.
   *
   * @param username ユーザー名
   * @return The found user or None if no user for the given ID could be found.
   */
  def find(username: String): Future[Option[User]] =
  // TODO usergroupも加えて検索するように
    Future.successful(
      DB localTx { implicit l =>
        val u = User.syntax("u")
        withSQL(
          select(
            u.id,
            u.username,
            u.password,
            u.user_group,
            u.role,
            u.first_name,
            u.last_name,
            u.full_name,
            u.email,
            u.avatar_url,
            u.activated,
            u.deletable
          )
            .from(User as u)
            .where
            .eq(u.username, username)
        ).map(rs => User(rs)).single().apply()
      }
    )

  /**
   * ユーザー検索(pac4j)
   * @param fields DBフィールド
   * @param key 検索キー
   * @param value 検索値
   * @param session DBSession
   * @return
   */
  def find(fields: String, key: String, value: String)(implicit session: DBSession): List[Map[String, Any]] = {
    StringSQLRunner(s"""SELECT $fields FROM m_userinfo as c WHERE $key = '$value'""").run()
  }

  /**
   * ユーザー作成(pac4j)
   * @param attributes 属性リスト
   * @param session DBSession
   */
  def insert(attributes: Seq[(String,AnyRef)])(implicit session: DBSession): Unit = {
    val c = User.column
    val nv = attributes.map(attr => c.column(attr._1)->attr._2).toMap
    withSQL {
      insertInto(User).namedValues(nv)
    }.update().apply()
  }

  /**
   * ユーザー更新(pac4j)
   * @param attributes 属性リスト
   * @param session DBSession
   */
  def update(attributes: Seq[(String,AnyRef)])(implicit session: DBSession): Unit = {
    val c = User.column
    val userId = attributes.filter(attr => attr._1.eq("id")).map(attr => attr._2).head
    val nv = attributes.filter(attr => attr._1.ne("id")).map(attr => c.column(attr._1)->attr._2).toMap
    withSQL {
      QueryDSL
        .update(User)
        .set(nv)
        .where
        .eq(c.id, userId)
    }.update().apply()
  }

  implicit val pf:ParameterBinderFactory[AnyRef] = ParameterBinderFactory {
    value => (stmt, idx) => stmt.setObject(idx, value)
  }

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User): Future[User] = {
    Await.result(find(user.id), Duration.Inf) match {
      case Some(_: User) => update(user)
      case _ => add(user)
    }
  }

  /**
   * Delete a user.
   * @param userID The ID of the user to delete.
   */
  def delete(userID: String, group: String): Unit = {
    DB localTx { implicit l =>
      withSQL{
        val u = User.column
        QueryDSL
          .delete
          .from(User)
          .where
          .eq(u.id, userID)
          .and
          .eq(u.user_group, group)
      }.update().apply()
    }
  }

  /**
   * ユーザー削除(pac4j)
   * @param userID ユーザーID
   */
  def delete(userID: String): Unit = {
    DB localTx { implicit l =>
      withSQL{
        val u = User.column
        QueryDSL
          .delete
          .from(User)
          .where
          .eq(u.id, userID)
      }.update().apply()
    }
  }

  private def update(user: User): Future[User] = {
    DB localTx { implicit l =>
      withSQL {
        val u = User.column
        QueryDSL.update(User).set(
          u.username -> user.username,
          u.password -> user.password,
          u.first_name -> user.first_name,
          u.last_name -> user.last_name,
          u.email -> user.email,
          u.avatar_url -> user.avatar_url,
          u.activated -> user.activated,
          u.deletable -> user.deletable
        )
          .where
          .eq(u.id, user.id.toString)
      }.update().apply()
      Future.successful(user)
    }
  }

  private def add(user: User): Future[User] = {
    DB localTx { implicit l =>
      withSQL {
        val u = User.column
        insertInto(User).namedValues(
          u.id -> user.id.toString,
          u.username -> user.username,
          u.password -> user.password,
          u.user_group -> user.user_group,
          u.role -> user.role,
          u.first_name -> user.first_name,
          u.last_name -> user.last_name,
          u.email -> user.email,
          u.avatar_url -> user.avatar_url,
          u.activated -> 0,
          u.deletable -> 1
        )
      }.update().apply()
      Future.successful(user)
    }
  }

  def getList(userGroup: String): JsValue = {
    DB localTx { implicit l =>
      val u = User.syntax("u")
      val userList = withSQL(
        select(
          u.id,
          u.username,
          u.password,
          u.user_group,
          u.role,
          u.first_name,
          u.last_name,
          u.full_name,
          u.email,
          u.avatar_url,
          u.activated,
          u.deletable
        )
          .from(User as u)
          .where
          .eq(u.user_group, userGroup)
      ).map(rs => User(rs)).list().apply()

      val userListJson = userList.map(
        u => {
          UserJson(
            u.id.toString,
            u.username,
            u.password,
            u.user_group.getOrElse(""),
            u.role.getOrElse(""),
            u.first_name.getOrElse(""),
            u.last_name.getOrElse(""),
            u.full_name.getOrElse(""),
            u.email.getOrElse(""),
            u.avatar_url.getOrElse(""),
            u.activated,
            u.deletable
          )
        }
      )
      Json.toJson(userListJson)
    }
  }

  // Adminグループのユーザーアカウント数を取得
  def countAdminUsers() :Int = {
    DB localTx { implicit l =>
      withSQL {
        val u = User.syntax("u")
        select(count)
          .from(User as u)
          .where
          .eq(u.user_group, "admin")
      }.map(rs => rs.int(1)).single().apply().getOrElse(0)
    }
  }
}

/**
 * The companion object.
 */
object UserDAOImpl {

  /**
   * The list of users.
   */
  val users: mutable.HashMap[UUID, User] = mutable.HashMap()
}
