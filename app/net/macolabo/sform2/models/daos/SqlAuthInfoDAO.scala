package net.macolabo.sform2.models.daos

import scala.concurrent.Future
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import net.macolabo.sform2.models.entity.user.AuthInfo
import scalikejdbc._

import scala.reflect.ClassTag

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * 認証情報用DAO
 * ※このDAOはSilhouetteのクラスをオーバーライドする必要がある関係で、トランザクション境界をDAO内にしている
 * @param ct classTag
 */
class SqlAuthInfoDAO(implicit ct: ClassTag[PasswordInfo]) extends DelegableAuthInfoDAO[PasswordInfo] {
  val classTag: ClassTag[PasswordInfo] = ct

  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = {
      DB.localTx(implicit session => {
        _find(loginInfo)
      })
    }

  override def update(loginInfo: LoginInfo, passwordInfo: PasswordInfo): Future[PasswordInfo] = {
    DB.localTx(implicit session => {
      _update(loginInfo, passwordInfo)
    })
  }

  override def remove(loginInfo: LoginInfo): Future[Unit] = {
    DB.localTx(implicit session => {
      _remove(loginInfo)
    })
  }

  override def save(loginInfo: LoginInfo, passwordInfo: PasswordInfo): Future[PasswordInfo] = {
    DB.localTx(implicit session => {
      _save(loginInfo, passwordInfo)
    })
  }

  override def add(loginInfo: LoginInfo, passwordInfo: PasswordInfo): Future[PasswordInfo] = {
    DB.localTx(implicit session => {
      _add(loginInfo, passwordInfo)
    })
  }

  def _find(loginInfo: LoginInfo)(implicit session: DBSession): Future[Option[PasswordInfo]] = {
    Future.successful {
      val f = AuthInfo.syntax("f")
      withSQL(
        select(
          f.authinfo_id,
          f.provider_id,
          f.provider_key,
          f.hasher,
          f.password,
          f.salt
        )
          .from(AuthInfo as f)
          .where
          .eq(f.provider_id, loginInfo.providerID)
          .and
          .eq(f.provider_key, loginInfo.providerKey)
      ).map(rs => AuthInfo(rs)).single().apply().map(toPasswordInfo)
    }
  }

  def _remove(loginInfo: LoginInfo)(implicit session:DBSession): Future[Unit] =
    Future.successful {
      withSQL {
        val c = AuthInfo.column
        QueryDSL.delete
          .from(AuthInfo)
          .where
          .eq(c.provider_id, loginInfo.providerID)
          .and
          .eq(c.provider_key, loginInfo.providerKey)
      }.update().apply()
    }

  def _update(loginInfo: LoginInfo, passwordInfo: PasswordInfo)(implicit session: DBSession): Future[PasswordInfo] = {
    Future.successful {
      withSQL {
        val c = AuthInfo.column
        QueryDSL.update(AuthInfo).set(
          c.hasher -> passwordInfo.hasher,
          c.password -> passwordInfo.password,
          c.salt -> passwordInfo.salt
        ).where
          .eq(c.provider_id, loginInfo.providerID)
          .and
          .eq(c.provider_key, loginInfo.providerKey)
      }.update().apply()
      passwordInfo
    }
  }

  def _save(loginInfo: LoginInfo, passwordInfo: PasswordInfo)(implicit session: DBSession): Future[PasswordInfo] =
    _find(loginInfo).flatMap {
      case Some(s: PasswordInfo) => _update(loginInfo, passwordInfo)
      case _ => _add(loginInfo, passwordInfo)
    }

  def _add(loginInfo: LoginInfo, passwordInfo: PasswordInfo)(implicit session: DBSession): Future[PasswordInfo] =
    Future.successful {
      withSQL {
        val c = AuthInfo.column
        insertInto(AuthInfo).namedValues(
          c.provider_id -> loginInfo.providerID,
          c.provider_key -> loginInfo.providerKey,
          c.hasher -> passwordInfo.hasher,
          c.password -> passwordInfo.password,
          c.salt -> passwordInfo.salt
        )
      }.update().apply()
      passwordInfo
    }

  private def toPasswordInfo(authInfo: AuthInfo) = {
    PasswordInfo(authInfo.hasher.getOrElse(""), authInfo.password.getOrElse(""), authInfo.salt)
  }
}