package net.macolabo.sform2.utils

/**
 * Implement this to get a named logger in scope.
 */
trait Logger {

  /**
   * A named logger instance.
   */
  val logger = play.api.Logger(this.getClass)
}
