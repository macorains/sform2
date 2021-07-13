package net.macolabo.sform2.models

import com.typesafe.config.{Config, ConfigFactory}
import scalikejdbc.{AutoSession, ConnectionPool, ConnectionPoolSettings}

trait SFDBConf {
  val config: Config = ConfigFactory.load()
  val SFDBDriver: String = config.getString("db.default.driver")
  val SFDBUrl: String = config.getString("db.default.url")
  val SFDBUser: String = config.getString("db.default.username")
  val SFDBPassword: String = config.getString("db.default.password")

  val settings: ConnectionPoolSettings = ConnectionPoolSettings(
    initialSize = 5,
    maxSize = 10,
    connectionTimeoutMillis = 3000L,
    validationQuery = "select 1 from dual"
  )
  Class.forName(SFDBDriver)
  ConnectionPool.singleton(SFDBUrl, SFDBUser, SFDBPassword, settings)
  // implicit val session = AutoSession
}
