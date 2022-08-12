package net.macolabo.sform.api.services

import java.util.UUID
import javax.inject.Inject
import net.macolabo.sform.api.models.entity.user.AuthToken
import net.macolabo.sform.api.models.daos.AuthTokenDAO
import scalikejdbc.DB

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

/**
 * Handles actions to auth tokens.
 *
 * @param authTokenDAO The auth token DAO implementation.
 * @param ex           The execution context.
 */
class AuthTokenServiceImpl @Inject() (
  authTokenDAO: AuthTokenDAO,
)(
  implicit
  ex: ExecutionContext
) extends AuthTokenService {

  /**
   * Creates a new auth token and saves it in the backing store.
   *
   * @param userID The user ID for which the token should be created.
   * @param expiry The duration a token expires.
   * @return The saved auth token.
   */
  def create(userID: UUID, expiry: FiniteDuration = 5 minutes): Future[AuthToken] = {
    ???
    // TODO Silhouetteを外す
//    val token = AuthToken(UUID.randomUUID(), userID, clock.now.withZone(DateTimeZone.UTC).plusSeconds(expiry.toSeconds.toInt))
//    authTokenDAO.save(token)
  }

  /**
   * Validates a token ID.
   *
   * @param id The token ID to validate.
   * @return The token if it's valid, None otherwise.
   */
  def validate(id: UUID): Future[Option[AuthToken]] = {
    DB.localTx(implicit session => {
      authTokenDAO.find(id)
    })
  }

  /**
   * Cleans expired tokens.
   *
   * @return The list of deleted tokens.
   */
  def clean: Future[Seq[AuthToken]] = {
    ???
    // TODO Silhouetteを外す
    /*
    authTokenDAO.findExpired(clock.now.withZone(DateTimeZone.UTC)).flatMap { tokens =>
      Future.sequence(tokens.map { token =>
        authTokenDAO.remove(token.id).map(_ => token)
      })
    }
    */
  }
}
