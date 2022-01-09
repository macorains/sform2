package net.macolabo.sform2.models.daos

import java.util.UUID
import net.macolabo.sform2.models.entity.user.User
import net.macolabo.sform2.models.SFDBConf
import play.api.libs.json.{JsValue, Json, Reads, Writes}
import scalikejdbc._

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
class UserDAOImpl extends UserDAO with SFDBConf {

  /**
   * Finds a user by its user ID.
   *
   * @param userID The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  def find(userID: UUID): Future[Option[User]] = {
    // TODO QueryDSLに変更する
    Future.successful(
      DB localTx { implicit l =>
        sql"SELECT ID,USERNAME,PASSWORD,USER_GROUP,ROLE,FIRST_NAME,LAST_NAME,FULL_NAME,EMAIL,AVATAR_URL,ACTIVATED,DELETABLE FROM M_USERINFO WHERE ID=${userID.toString}"
          .map(rs =>
            User(
              UUID.fromString(rs.string("ID")),
              rs.string("USERNAME"),
              rs.string("PASSWORD"),
              Option(rs.string("USER_GROUP")),
              Option(rs.string("ROLE")),
              Option(rs.string("FIRST_NAME")),
              Option(rs.string("LAST_NAME")),
              Option(rs.string("FULL_NAME")),
              Option(rs.string("EMAIL")),
              Option(rs.string("AVATAR_URL")),
              rs.boolean("ACTIVATED"),
              rs.boolean("DELETABLE")
            )
          )
          .single().apply()
      }
    )
  }

  /**
   * Finds a user by its user ID.
   *
   * @param userID The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  def find(username: String): Future[Option[User]] =
  // TODO QueryDSLに変更する
  // TODO usergroupも加えて検索するように
    Future.successful(
      DB localTx { implicit l =>
        sql"SELECT ID,USERNAME,PASSWORD,USER_GROUP,ROLE,FIRST_NAME,LAST_NAME,FULL_NAME,EMAIL,AVATAR_URL,ACTIVATED,DELETABLE FROM M_USERINFO WHERE USERNAME=$username"
          .map(rs =>
            User(
              UUID.fromString(rs.string("ID")),
              rs.string("USERNAME"),
              rs.string("PASSWORD"),
              Option(rs.string("USER_GROUP")),
              Option(rs.string("ROLE")),
              Option(rs.string("FIRST_NAME")),
              Option(rs.string("LAST_NAME")),
              Option(rs.string("FULL_NAME")),
              Option(rs.string("EMAIL")),
              Option(rs.string("AVATAR_URL")),
              rs.boolean("ACTIVATED"),
              rs.boolean("DELETABLE")
            )
          )
          .single().apply()
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
    val result = StringSQLRunner(s"""SELECT ${fields} FROM M_USERINFO as c WHERE ${key} = '${value}'""").run()
    result
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
    val nv = attributes.map(attr => c.column(attr._1)->attr._2).toMap
    withSQL {
      QueryDSL.update(User).set(nv)
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
    Await.result(find(user.user_id), Duration.Inf) match {
      case Some(u: User) => update(user)
      case _ => add(user)
    }
  }

  /**
   * Delete a user.
   * @param userID The ID of the user to delete.
   */
  def delete(userID: String, group: String): Unit = {
    DB localTx { implicit l =>
      sql"DELETE FROM M_USERINFO WHERE ID=$userID AND USER_GROUP=$group".update().apply()
    }
  }

  /**
   * ユーザー削除(pac4j)
   * @param userID
   */
  def delete(userID: String): Unit = {
    DB localTx { implicit l =>
      withSQL{
        val c = User.column
        QueryDSL.delete.from(User).where.eq(c.user_id, userID)
      }.update().apply()
    }
  }

  private def update(user: User): Future[User] = {
    DB localTx { implicit l =>
      sql"UPDATE M_USERINFO SET USERNAME=${user.username}, PASSWORD=${user.password}, FIRST_NAME=${user.first_name}, LAST_NAME=${user.last_name} ,EMAIL=${user.email}, AVATAR_URL=${user.avatar_url}, ACTIVATED=${user.activated}, DELETABLE=${user.deletable} WHERE ID=${user.user_id.toString}"
        .update().apply()
      Future.successful(user)
    }
  }

  private def add(user: User): Future[User] = {
    DB localTx { implicit l =>
      sql"INSERT INTO M_USERINFO(ID,USERNAME,PASSWORD,USER_GROUP,ROLE,FIRST_NAME,LAST_NAME,EMAIL,AVATAR_URL,ACTIVATED,DELETABLE) VALUES(${user.user_id.toString},${user.username},${user.password},${user.user_group},${user.role},${user.first_name},${user.last_name},${user.email},'',0,1)"
        .update().apply()
      Future.successful(user)
    }
  }


  def getList(userGroup: String): JsValue = {
    DB localTx { implicit l =>
      val userList = sql"SELECT USER_ID,USERNAME,PASSWORD,USER_GROUP,ROLE,FIRST_NAME,LAST_NAME,FULL_NAME,EMAIL,AVATAR_URL,ACTIVATED,DELETABLE FROM M_USERINFO WHERE USER_GROUP=$userGroup"
        .map(rs =>
          User(
            UUID.fromString(rs.string("USER_ID")),
            rs.string("USERNAME"),
            rs.string("PASSWORD"),
            Option(rs.string("USER_GROUP")),
            Option(rs.string("ROLE")),
            Option(rs.string("FIRST_NAME")),
            Option(rs.string("LAST_NAME")),
            Option(rs.string("FULL_NAME")),
            Option(rs.string("EMAIL")),
            Option(rs.string("AVATAR_URL")),
            rs.boolean("ACTIVATED"),
            rs.boolean("DELETABLE")
          )
        )
        .list().apply()
      val userListJson = userList.map(
        u => {
          UserJson(
            u.user_id.toString,
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
      sql"SELECT count(*) FROM M_USERINFO WHERE USER_GROUP='admin'".map(_.int(1)).first().apply().getOrElse(0)
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
