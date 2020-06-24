package net.macolabo.sform2.utils

import javax.inject.Inject
import play.api.http.DefaultHttpFilters
import play.filters.cors.CORSFilter
/**
 * Provides filters.
 */

//class Filters @Inject() (csrfFilter: CSRFFilter, securityHeadersFilter: SecurityHeadersFilter) extends HttpFilters {
//  override def filters: Seq[EssentialFilter] = Seq(csrfFilter, securityHeadersFilter)
//}

class Filters @Inject() (corsFilter: CORSFilter)
  extends DefaultHttpFilters(corsFilter)