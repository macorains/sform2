package net.macolabo.sform2.filters

import javax.inject.Inject
import org.apache.pekko.stream.Materializer
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class SessionRenewalFilter @Inject()(
  implicit val mat: Materializer,
  ec: ExecutionContext
) extends Filter {

  def apply(nextFilter: RequestHeader => Future[Result])(
    request: RequestHeader
  ): Future[Result] = {
    nextFilter(request).map { result =>
      if (request.session.data.nonEmpty && result.newSession.isEmpty) {
        result.withSession(request.session)
      } else {
        result
      }
    }
  }
}
