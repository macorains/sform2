package net.macolabo.sform2.modules

import com.google.inject.AbstractModule
import net.macolabo.sform2.utils.Logger
import scalikejdbc.config._

class DBInitializer extends Logger {
  DBs.setupAll()
  logger.debug(s"db setup...")
}

class DBInitializerModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[DBInitializer]).asEagerSingleton()
  }
}