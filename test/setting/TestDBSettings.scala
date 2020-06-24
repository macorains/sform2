package setting

import com.typesafe.config.ConfigFactory
import scalikejdbc.{ AutoSession, ConnectionPool }

trait TestDBSettings {

  def loadJDBCSettings() {
    // https://github.com/typesafehub/config
    val config = ConfigFactory.load()
    val SFDBDriver = config.getString("db.default.driver")
    val SFDBUrl = config.getString("db.default.url")
    val SFDBUser = config.getString("db.default.username")
    val SFDBPassword = config.getString("db.default.password")

    Class.forName(SFDBDriver)
    ConnectionPool.singleton(SFDBUrl, SFDBUser, SFDBPassword)
    implicit val session = AutoSession
  }

  loadJDBCSettings()
}
