package net.macolabo.sform2.models.daos

import java.util.UUID

import net.macolabo.sform2.models
import net.macolabo.sform2.models.{AuthToken, SFDBConf}
import org.joda.time.DateTime
import scalikejdbc._

import scala.collection.mutable
import scala.concurrent.Future
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
  //def find(id: UUID) = Future.successful(tokens.get(id))
  def find(id: UUID): Future[Option[AuthToken]] = Future.successful(
    sql"SELECT ID,USER_ID,EXPIRY FROM D_AUTHTOKEN WHERE ID=${id.toString}"
      .map(rs => models.AuthToken(UUID.fromString(rs.string("ID")), UUID.fromString(rs.string("USER_ID")), rs.jodaDateTime("EXPIRY"))).single.apply()
  )

  /**
   * Finds expired tokens.
   *
   * @param dateTime The current date time.
   */
  def findExpired(dateTime: DateTime): Future[Seq[AuthToken]] = Future.successful {
    /*
    tokens.filter {
      case (_, token) =>
        token.expiry.isBefore(dateTime)
    }.values.toSeq
    */
    println(dateTime)
    sql"SELECT ID,USER_ID,EXPIRY FROM D_AUTHTOKEN WHERE EXPIRY<=$dateTime"
      .map(rs => models.AuthToken(UUID.fromString(rs.string("ID")), UUID.fromString(rs.string("USER_ID")), rs.jodaDateTime("EXPIRY"))).list.apply()

  }

  /**
   * Saves a token.
   *
   * @param token The token to save.
   * @return The saved token.
   */
  def save(token: AuthToken): Future[AuthToken] = {
    //tokens += (token.id -> token)
    //println(token.toString)
    sql"INSERT INTO D_AUTHTOKEN(ID,USER_ID,EXPIRY) VALUES(${token.id.toString},${token.userID.toString},${token.expiry})"
      .update.apply()
    Future.successful(token)
  }

  /**
   * Removes the toke for the given ID.
   *
   * @param id The ID for which the token should be removed.
   * @return A future to wait for the process to be completed.
   */
  def remove(id: UUID): Future[Unit] = {
    //tokens -= id
    sql"DELETE FROM D_AUTHTOKEN WHERE ID=$id".update.apply()
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
