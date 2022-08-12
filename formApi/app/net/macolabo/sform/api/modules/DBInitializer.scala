package net.macolabo.sform.api.modules

import com.google.inject.AbstractModule
import play.api.Logger
import scalikejdbc.config._

class DBInitializer {
  DBs.setupAll()
  // TODO ログ出せるようにしたい
  // Logger.logger.debug(s"db setup...")
}

class DBInitializerModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[DBInitializer]).asEagerSingleton()
  }
}