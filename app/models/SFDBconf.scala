package models

import com.typesafe.config.ConfigFactory
import play.api.Play
import scalikejdbc.{ AutoSession, ConnectionPool }

trait SFDBConf {
  val config = ConfigFactory.load()
  val SFDBDriver = config.getString("db.default.driver")
  val SFDBUrl = config.getString("db.default.url")
  val SFDBUser = config.getString("db.default.username")
  val SFDBPassword = config.getString("db.default.password")

  Class.forName(SFDBDriver)
  ConnectionPool.singleton(SFDBUrl, SFDBUser, SFDBPassword)
  implicit val session = AutoSession
}
