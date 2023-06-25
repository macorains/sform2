package net.macolabo.sform2.domain.services.User

import net.macolabo.sform2.domain.models.daos.UserDAO
import net.macolabo.sform2.domain.models.entity.user.User
import org.apache.shiro.authc.credential.DefaultPasswordService
import org.pac4j.core.credentials.password.ShiroPasswordEncoder

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
    val userId = userSaveRequest.userId.map(UUID.fromString).getOrElse(UUID.randomUUID())
    val encoder = new ShiroPasswordEncoder(new DefaultPasswordService)
    val password = userSaveRequest.userId
      .flatMap(_ => userSaveRequest.password)
      .orElse(Some(encoder.encode(UUID.randomUUID().toString)))

    save(User(
      id = userId,
      username = userSaveRequest.email,
      password = password,
      user_group = Option(userSaveRequest.userGroup),
      role = Option(userSaveRequest.role),
      first_name = Option(userSaveRequest.firstName),
      last_name = Option(userSaveRequest.lastName),
      full_name = Option(userSaveRequest.fullName),
      email = Option(userSaveRequest.email),
      avatar_url = userSaveRequest.avatarUrl,
      activated = userSaveRequest.userId.isDefined,
      deletable = true
    ))
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
