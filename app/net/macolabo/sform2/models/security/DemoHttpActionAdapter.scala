package net.macolabo.sform2.models.security

import org.pac4j.core.context.{HttpConstants, WebContext}
import org.pac4j.core.exception.http.HttpAction
import org.pac4j.play.PlayWebContext
import org.pac4j.play.http.PlayHttpActionAdapter
import play.mvc.{Result, Results}

class DemoHttpActionAdapter extends PlayHttpActionAdapter {

  override def adapt(action: HttpAction, context: WebContext): Result = {
    val playWebContext = context.asInstanceOf[PlayWebContext]
    if (action != null && action.getCode == HttpConstants.UNAUTHORIZED) {
      playWebContext.supplementResponse(Results.unauthorized("hoge!").as(HttpConstants.HTML_CONTENT_TYPE))
    } else if (action != null && action.getCode == HttpConstants.FORBIDDEN) {
      playWebContext.supplementResponse(Results.forbidden("fuga").as(HttpConstants.HTML_CONTENT_TYPE))
    } else {
      super.adapt(action, context)
    }
  }
}
