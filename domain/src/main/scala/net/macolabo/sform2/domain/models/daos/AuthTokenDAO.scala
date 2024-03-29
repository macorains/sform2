package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.user.AuthToken
import scalikejdbc.DBSession

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.Future

/**
 * Give access to the AuthToken object.
 */
trait AuthTokenDAO {

  /**
   * Finds a token by its ID.
   *
   * @param id The unique token ID.
   * @return The found token or None if no token for the given ID could be found.
   */
  def find(id: UUID)(implicit session: DBSession): Future[Option[AuthToken]]

  /**
   * Finds expired tokens.
   *
   * @param dateTime The current date time.
   */
  def findExpired(dateTime: LocalDateTime)(implicit session: DBSession): Future[Seq[AuthToken]]

  /**
   * Saves a token.
   *
   * @param token The token to save.
   * @return The saved token.
   */
  def save(token: AuthToken)(implicit session: DBSession): Future[AuthToken]

  /**
   * Removes the token for the given ID.
   *
   * @param id The ID for which the token should be removed.
   * @return A future to wait for the process to be completed.
   */
  def remove(id: UUID)(implicit session: DBSession): Future[Int]
}
