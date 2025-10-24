package net.macolabo.sform2
import play.api.Logger
import play.api.mvc._
import play.api.http.HttpErrorHandler

import javax.inject.Singleton
import scala.concurrent._

@Singleton
class GlobalErrorHandler extends HttpErrorHandler {
  private val logger = Logger(this.getClass)
  def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    Future.successful(Results.Status(statusCode)(s"Client error: $message"))
  }

  def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    logger.error(s"Server error: ${exception.getMessage}", exception)
    Future.successful(Results.InternalServerError("Internal server error"))
  }
}
