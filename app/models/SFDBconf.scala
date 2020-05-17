package models

import com.typesafe.config.ConfigFactory
import play.api.Play
import scalikejdbc.{AutoSession, ConnectionPool, ConnectionPoolSettings}

trait SFDBConf {
  val config = ConfigFactory.load()
  val SFDBDriver = config.getString("db.default.driver")
  val SFDBUrl = config.getString("db.default.url")
  val SFDBUser = config.getString("db.default.username")
  val SFDBPassword = config.getString("db.default.password")

  val settings = ConnectionPoolSettings(
    initialSize = 5,
    maxSize = 10,
    connectionTimeoutMillis = 3000L,
    validationQuery = "select 1 from dual"
  )
  Class.forName(SFDBDriver)
  ConnectionPool.singleton(SFDBUrl, SFDBUser, SFDBPassword, settings)
  implicit val session = AutoSession
}
