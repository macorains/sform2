package net.macolabo.sform2.models.daos

import java.util.UUID
import net.macolabo.sform2.models.entity.user.AuthToken
import net.macolabo.sform2.models.SFDBConf

import scala.collection.mutable
import scala.concurrent.Future
import scalikejdbc._

import java.time.LocalDateTime
/**
 * Give access to the [[AuthToken]] object.
 */
class AuthTokenDAOImpl extends AuthTokenDAO with SFDBConf {

  /**
   * Finds a token by its ID.
   *
   * @param id The unique token ID.
   * @return The found token or None if no token for the given ID could be found.
   */
  def find(id: UUID)(implicit session: DBSession): Future[Option[AuthToken]] = Future.successful {
    val f = AuthToken.syntax("f")
    withSQL(
      select(
        f.id,
        f.user_id,
        f.expiry
      )
        .from(AuthToken as f)
        .where
        .eq(f.id, id.toString)
    ).map(rs => AuthToken(rs)).single().apply()
  }

  /**
   * Finds expired tokens.
   *
   * @param dateTime The current date time.
   */
  def findExpired(dateTime: LocalDateTime)(implicit session: DBSession): Future[Seq[AuthToken]] = Future.successful {
    val f = AuthToken.syntax("f")
    withSQL(
      select(
        f.id,
        f.user_id,
        f.expiry
      )
        .from(AuthToken as f)
        .where
        .le(f.expiry, dateTime)
    ).map(rs => AuthToken(rs)).list().apply()
  }

  /**
   * Saves a token.
   *
   * @param token The token to save.
   * @return The saved token.
   */
  def save(token: AuthToken)(implicit session: DBSession): Future[AuthToken] = Future.successful {
    withSQL {
      val c = AuthToken.column
      insertInto(AuthToken)
        .namedValues(
          c.id -> token.id.toString,
          c.user_id -> token.userID.toString,
          c.expiry -> token.expiry
        )
    }.update().apply()
    token
  }

  /**
   * Removes the toke for the given ID.
   *
   * @param id The ID for which the token should be removed.
   * @return A future to wait for the process to be completed.
   */
  def remove(id: UUID)(implicit session: DBSession): Future[Int] = Future.successful {
    withSQL {
      val c = AuthToken.column
      QueryDSL
        .delete
        .from(AuthToken)
        .where
        .eq(c.id, id.toString)
    }.update().apply()
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
