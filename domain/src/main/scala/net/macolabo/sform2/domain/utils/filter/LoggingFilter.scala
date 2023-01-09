package net.macolabo.sform2.domain.utils.filter

import akka.stream.Materializer
import com.google.inject.Inject
import play.api.Logger
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.{ExecutionContext, Future}

class LoggingFilter @Inject()(implicit val mat: Materializer, ec: ExecutionContext) extends Filter {

  val logger: Logger = Logger(this.getClass)

  def apply(nextFilter: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] = {

    val startTime = System.currentTimeMillis

    nextFilter(requestHeader).map { result =>
      val endTime     = System.currentTimeMillis
      val requestTime = endTime - startTime

      //if (requestHeader.uri == "/filter-log" && result.header.status != 200) {
        logger.info(
          s"method:${requestHeader.method}, url:${requestHeader.uri}, status:${result.header.status}, took:${requestTime}ms, headers:${requestHeader.headers.toSimpleMap}"
        )
      //}
      result
    }
  }
}
