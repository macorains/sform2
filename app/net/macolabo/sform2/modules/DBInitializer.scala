package net.macolabo.sform2.modules

import com.google.inject.AbstractModule
import play.api.Logger
import scalikejdbc.config._

/**
 * Created by Fumiyasu on 2016/09/15.
 */
class DBInitializer {
  DBs.setupAll()
  Logger.logger.debug(s"db setup...")
}

class DBInitializerModule extends AbstractModule {
  override def configure() = {
    bind(classOf[DBInitializer]).asEagerSingleton()
  }
}