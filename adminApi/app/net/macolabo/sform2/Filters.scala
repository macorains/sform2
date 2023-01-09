package net.macolabo.sform2

import com.digitaltangible.playguard.GuardFilter

import javax.inject._
import play.api._
import play.api.http.HttpFilters
import net.macolabo.sform2.domain.utils.filter.LoggingFilter

/**
 * This class configures filters that run on every request. This
 * class is queried by Play to get a list of filters.
 *
 * Play will automatically use filters from any class called
 * `net.macolabo.sform2.Filters` that is placed the root package. You can load filters
 * from a different class by adding a `play.http.filters` setting to
 * the `application.conf` configuration file.
 *
 * @param env Basic environment settings for the current application.
 * @param loggingFilter A logging filter.
 * @param guardFilter   the guardFilter to test with this sample app
 */
@Singleton
class Filters @Inject() (
  env: Environment,
  loggingFilter: LoggingFilter,
  guardFilter: GuardFilter) extends HttpFilters {

  override val filters = {
    // Use the example filter if we're running development mode. If
    // we're running in production or test mode then don't use any
    // filters at all.
    if (env.mode == Mode.Dev) Seq(loggingFilter, guardFilter) else Seq(loggingFilter, guardFilter)
  }

}
