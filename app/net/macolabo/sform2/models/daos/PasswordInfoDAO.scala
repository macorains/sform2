package net.macolabo.sform2.models.daos

import scala.concurrent.Future
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import scalikejdbc._

class PasswordInfoDAO extends DelegableAuthInfoDAO[PasswordInfo] {

  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] =
    Future.successful(
      DB localTx { implicit s =>
        sql"""SELECT HASHER,PASSWORD,SALT FROM M_AUTHINFO WHERE PROVIDER_ID=${loginInfo.providerID}
             AND PROVIDER_KEY=${loginInfo.providerKey}"""
          .map(rs => PasswordInfo(rs.string("HASHER"), rs.string("PASSWORD"), Option(rs.string("SALT")))).single.apply()
      }
    )

  override def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    Future.successful {
      DB localTx { implicit s =>
        sql"""INSERT INTO M_AUTHINFO(PROVIDER_ID,PROVIDER_KEY,HASHER,PASSWORD,SALT)
             VALUES('',${loginInfo.providerID},${loginInfo.providerKey},${authInfo.hasher},${authInfo.password},${authInfo.salt})"""
          .updateAndReturnGeneratedKey.apply()
        authInfo
      }
    }

  override def remove(loginInfo: LoginInfo): Future[Unit] =
    Future.successful {
      DB localTx { implicit s =>
        sql"DELETE FROM M_AUTHINFO WHERE PROVIDER_ID=${loginInfo.providerID} AND PROVIDER_KEY=${loginInfo.providerKey}"
          .update.apply()
      }
    }

  override def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    find(loginInfo) match {
      case _: PasswordInfo => update(loginInfo, authInfo)
      case _ => add(loginInfo, authInfo)
    }

  override def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    Future.successful {
      DB localTx { implicit s =>
        sql"""INSERT INTO M_AUTHINFO(PROVIDER_ID,PROVIDER_KEY,HASHER,PASSWORD,SALT)
             VALUES(${loginInfo.providerID},${loginInfo.providerKey},${authInfo.hasher},${authInfo.password},${authInfo.salt})"""
          .updateAndReturnGeneratedKey.apply()
        authInfo
      }
    }
}