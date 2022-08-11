package net.macolabo.sform2.domain.utils

import play.api

/**
 * Implement this to get a named logger in scope.
 */
trait Logger {

  /**
   * A named logger instance.
   */
  val logger: api.Logger = play.api.Logger(this.getClass)
}
