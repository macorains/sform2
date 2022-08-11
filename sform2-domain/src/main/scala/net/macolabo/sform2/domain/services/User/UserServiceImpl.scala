package net.macolabo.sform2.domain.services.User

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * Handles actions to users.
 *
 * @param userDAO The user DAO implementation.
 * @param ex      The execution context.
 */
class UserServiceImpl @Inject() (userDAO: UserDAO)(implicit ex: ExecutionContext) extends UserService {

  /**
   * Retrieves a user that matches the specified ID.
   *
   * @param id The ID to retrieve a user.
   * @return The retrieved user or None if no user could be retrieved for the given ID.
   */
  def retrieve(id: UUID): Future[Option[User]] = userDAO.find(id)

  def retrieve(username: String): Future[Option[User]] = {
    userDAO.find(username)
  }

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User): Future[User] = userDAO.save(user)

  def save(userSaveRequest: UserSaveRequest, userGroup: String): Future[User] = {
    // TODO silhouette関係ない形に直す
    ???
    // とりあえず蓋 2021/11/14
//    val userId = userSaveRequest.userId.map(UUID.fromString).getOrElse(UUID.randomUUID())
//    save(user.User(
//      userID = userId,
//      loginInfo = LoginInfo(userId.toString, s"""${userSaveRequest.email}:${userSaveRequest.userGroup}"""),
//      group = Option(userGroup),
//      role = Option("operator"),
//      firstName = Option(userSaveRequest.firstName),
//      lastName = Option(userSaveRequest.lastName),
//      fullName = Option(userSaveRequest.fullName),
//      email = Option(userSaveRequest.email),
//      avatarURL = userSaveRequest.avatarUrl,
//      activated = userSaveRequest.userId.isDefined,
//      deletable = true
//    ))
  }

  def delete(userId: String, group: String): Unit = {
    userDAO.delete(userId, group)
  }

  /**
   * Adminグループのユーザー存在チェック
   * @return Adminグループのユーザー数が0の場合はfalse、1以上でtrue
   */
  def checkAdminExists: Boolean = {
    !userDAO.countAdminUsers().equals(0)
  }
}
