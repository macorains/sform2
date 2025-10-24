package net.macolabo.sform2.domain.services.User

import net.macolabo.sform2.domain.models.SessionInfo
import net.macolabo.sform2.domain.models.entity.user.User

import java.util.UUID
import scala.concurrent.Future

// TODO silhouette関係ない形に直す
/**
 * Handles actions to users.
 */
trait UserService {

  /**
   * Retrieves a user that matches the specified ID.
   *
   * @param id The ID to retrieve a user.
   * @return The retrieved user or None if no user could be retrieved for the given ID.
   */
  def retrieve(id: UUID): Future[Option[User]]
  def retrieve(username: String): Future[Option[User]]
  def retrieveByEmail(email: String): Future[Option[User]]
  def getList(sessionInfo: SessionInfo): UserListResponse

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User): Future[User]

  def save(userSaveRequest: UserSaveRequest, sessionInfo: SessionInfo): Future[User]
  /**
   * Saves the social profile for a user.
   *
   * If a user exists for this profile then update the user, otherwise create a new user with the given profile.
   *
   * @param profile The social profile to save.
   * @return The user for whom the profile was saved.
   */
  //def save(profile: CommonSocialProfile): Future[User]

  def delete(userId: String, sessionInfo: SessionInfo): Unit

  /**
   * Adminグループのユーザー存在チェック
   * @return Adminグループのユーザー数が0の場合はfalse、1以上でtrue
   */
  def checkAdminExists: Boolean

}
