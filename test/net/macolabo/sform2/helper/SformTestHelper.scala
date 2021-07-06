package net.macolabo.sform2.helper

import com.typesafe.config.{Config, ConfigFactory}
import play.api.db.{Database, Databases}
import scalikejdbc.{AutoSession, ConnectionPool}

trait SformTestHelper {
  val config: Config = ConfigFactory.load()
  Class.forName(config.getString("db.test.driver"))
  ConnectionPool.singleton(config.getString("db.test.url"), config.getString("db.test.username"), config.getString("db.test.password"))
  implicit val session: AutoSession.type = AutoSession

  def withSformDatabase[T](block: Database => T) = {
    Databases.withDatabase(
      driver = config.getString("db.test.driver"),
      url = config.getString("db.test.url"),
      name = "sformdb",
      config = Map(
        "user" -> config.getString("db.test.username"),
        "password" -> config.getString("db.test.password")
      )
    )(block)
  }
}