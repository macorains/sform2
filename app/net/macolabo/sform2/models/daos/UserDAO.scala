package net.macolabo.sform2.models.daos

import java.util.UUID
import net.macolabo.sform2.models.entity.user.User
import play.api.libs.json.JsValue
import scalikejdbc.DBSession

import scala.concurrent.Future

/**
 * Give access to the user object.
 */
trait UserDAO {

  /**
   * Finds a user by its user ID.
   *
   * @param userID The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  def find(userID: UUID): Future[Option[User]]

  /**
   * ユーザー検索(pac4j)
   * @param fields DBフィールド
   * @param key 検索キー
   * @param value 検索値
   * @param session DBSession
   * @return
   */
  def find(fields: String, key: String, value: String)(implicit session: DBSession): List[Map[String, Any]]

  /**
   * ユーザー作成(pac4j)
   * @param attributes 属性リスト
   * @param session DBSession
   */
  def insert(attributes: Seq[(String,AnyRef)])(implicit session: DBSession): Unit

  /**
   * ユーザー更新(pac4j)
   * @param attributes 属性リスト
   * @param session DBSession
   */
  def update(attributes: Seq[(String,AnyRef)])(implicit session: DBSession): Unit

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User): Future[User]

  /**
   * Delete a user.
   * @param userID The ID of the user to delete.
   */
  def delete(userID: String, group: String): Unit

  /**
   * ユーザー削除(pac4j)
   * @param userID
   */
  def delete(userID: String): Unit

  def getList(identity: User): JsValue
  def countAdminUsers() :Int
}
