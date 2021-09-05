package net.macolabo.sform2.models.daos

import scala.concurrent.Future
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import net.macolabo.sform2.models.entity.user.AuthInfo
import scalikejdbc._

import scala.reflect.ClassTag

/**
 * パスワード情報用DAO
 * @param ct classTag
 */
class SqlAuthInfoDAO(implicit ct: ClassTag[PasswordInfo]) extends DelegableAuthInfoDAO[PasswordInfo] {
  val classTag: ClassTag[PasswordInfo] = ct
  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] =
    Future.successful(
      DB localTx { implicit s =>
        val f = AuthInfo.syntax("f")
        withSQL (
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
        ).map(rs => AuthInfo(rs)).single.apply.map(toPasswordInfo)
      }
    )

  override def update(loginInfo: LoginInfo, passwordInfo: PasswordInfo): Future[PasswordInfo] =
    Future.successful {
      DB localTx { implicit s =>
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

  override def remove(loginInfo: LoginInfo): Future[Unit] =
    Future.successful {
      DB localTx { implicit s =>
        withSQL {
          val c = AuthInfo.column
          QueryDSL.delete
            .from(AuthInfo)
            .where
            .eq(c.provider_id, loginInfo.providerID)
            .and
            .eq(c.provider_key, loginInfo.providerKey)
        }.update.apply
      }
    }

  override def save(loginInfo: LoginInfo, passwordInfo: PasswordInfo): Future[PasswordInfo] =
    find(loginInfo) match {
      case _: PasswordInfo => update(loginInfo, passwordInfo)
      case _ => add(loginInfo, passwordInfo)
    }

  override def add(loginInfo: LoginInfo, passwordInfo: PasswordInfo): Future[PasswordInfo] =
    Future.successful {
      DB localTx { implicit s =>
        withSQL {
          val c = AuthInfo.column
          insertInto(AuthInfo).namedValues(
            c.provider_id -> loginInfo.providerID,
            c.provider_key -> loginInfo.providerKey,
            c.hasher -> passwordInfo.hasher,
            c.password -> passwordInfo.password,
            c.salt -> passwordInfo.salt
          )
        }.update.apply
        passwordInfo
      }
    }

  private def toPasswordInfo(authInfo: AuthInfo) = {
    PasswordInfo(authInfo.hasher.getOrElse(""), authInfo.password.getOrElse(""), authInfo.salt)
  }
}