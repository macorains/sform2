package net.macolabo.sform2.modules

import com.google.inject.AbstractModule
import net.macolabo.sform2.domain.utils.Logger
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
