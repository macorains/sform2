package net.macolabo.sform2.domain.model.helper

import com.typesafe.config.{Config, ConfigFactory}
import play.api.Application
import play.api.db.{Database, Databases}
import play.api.inject.guice.GuiceApplicationBuilder
import scalikejdbc.{AutoSession, ConnectionPool}

import java.io.File

trait SformTestHelper {
  val config: Config = ConfigFactory.parseFile(new File("conf/test.conf")).resolve()
  Class.forName(config.getString("db.test.driver"))
  ConnectionPool.singleton(config.getString("db.test.url"), config.getString("db.test.username"), config.getString("db.test.password"))
  implicit val session: AutoSession.type = AutoSession

  def withSformDatabase[T](block: Database => T): T = {
    Databases.withDatabase(
      driver = config.getString("db.test.driver"),
      url = config.getString("db.test.url"),
      name = "test",
      config = Map(
        "user" -> config.getString("db.test.username"),
        "password" -> config.getString("db.test.password")
      )
    )(block)
  }
}
