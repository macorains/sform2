package net.macolabo.sform2.helper

import com.typesafe.config.{Config, ConfigFactory}
import play.api.db.{Database, Databases}
import scalikejdbc.{AutoSession, ConnectionPool}

trait SformTestHelper {
  val config: Config = ConfigFactory.load()
  Class.forName(config.getString("db.default.driver"))
  ConnectionPool.singleton(config.getString("db.default.url"), config.getString("db.default.username"), config.getString("db.default.password"))
  implicit val session: AutoSession.type = AutoSession

  def withSformDatabase[T](block: Database => T) = {
    Databases.withDatabase(
      driver = config.getString("db.default.driver"),
      url = config.getString("db.default.url"),
      name = "sformdb",
      config = Map(
        "user" -> config.getString("db.default.username"),
        "password" -> config.getString("db.default.password")
      )
    )(block)
  }
}