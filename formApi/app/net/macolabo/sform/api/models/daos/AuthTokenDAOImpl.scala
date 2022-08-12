package net.macolabo.sform.api.models.daos

import java.util.UUID
import net.macolabo.sform.api.models.entity.user.AuthToken
import scalikejdbc._

import java.time.LocalDateTime
import scala.collection.mutable
import scala.concurrent.Future
/**
 * Give access to the [[AuthToken]] object.
 */
class AuthTokenDAOImpl extends AuthTokenDAO {

  /**
   * Finds a token by its ID.
   *
   * @param id The unique token ID.
   * @return The found token or None if no token for the given ID could be found.
   */
  def find(id: UUID)(implicit session: DBSession): Future[Option[AuthToken]] = Future.successful {
    val a = AuthToken.syntax("a")
    withSQL(
      select(
        a.id,
        a.user_id,
        a.expiry
      )
        .from(AuthToken as a)
        .where
        .eq(a.id, id.toString)
    ).map(rs =>
      AuthToken(
        UUID.fromString(rs.string("ID")),
        UUID.fromString(rs.string("USER_ID")),
        rs.get("EXPIRY")
      )
    ).single().apply()
  }

  /**
   * Finds expired tokens.
   *
   * @param dateTime The current date time.
   */
  def findExpired(dateTime: LocalDateTime)(implicit session: DBSession): Future[Seq[AuthToken]] = Future.successful {
    val a = AuthToken.syntax("a")
    withSQL(
      select(
        a.id,
        a.user_id,
        a.expiry
      )
        .from(AuthToken as a)
        .where
        .lt(a.expiry, dateTime)
    ).map(rs =>
      AuthToken(
        UUID.fromString(rs.string("ID")),
        UUID.fromString(rs.string("USER_ID")),
        rs.get("EXPIRY")
      )
    ).list().apply()
  }

  /**
   * Saves a token.
   *
   * @param token The token to save.
   * @return The saved token.
   */
  def save(token: AuthToken)(implicit session: DBSession): Future[AuthToken] = {
    withSQL {
      val a = AuthToken.column
      insertInto(AuthToken).namedValues(
        a.id -> token.id.toString,
        a.user_id -> token.user_id.toString,
        a.expiry -> token.expiry
      )
    }.update().apply()
    Future.successful(token)
  }

  /**
   * Removes the toke for the given ID.
   *
   * @param id The ID for which the token should be removed.
   * @return A future to wait for the process to be completed.
   */
  def remove(id: UUID)(implicit session: DBSession): Future[Unit] = {
    withSQL {
      val a = AuthToken.column
      QueryDSL
        .delete
        .from(AuthToken)
        .where
        .eq(a.id, id.toString)
    }.update().apply()
    Future.successful(())
  }
}

/**
 * The companion object.
 */
object AuthTokenDAOImpl {

  /**
   * The list of tokens.
   */
  val tokens: mutable.HashMap[UUID, AuthToken] = mutable.HashMap()
}
