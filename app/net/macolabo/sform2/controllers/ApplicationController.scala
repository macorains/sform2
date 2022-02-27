package net.macolabo.sform2.controllers

import javax.inject.Inject
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Request}
import org.pac4j.core.profile.UserProfile
import org.pac4j.play.scala.{Security, SecurityComponents}

import scala.concurrent.Future

/**
 * The basic application controller.
 *
 * @param controllerComponents   The Play controller components.
 * @param webJarsUtil            The webjar util.
 */
class ApplicationController @Inject() (
                                        val controllerComponents: SecurityComponents
)(
  implicit
  webJarsUtil: WebJarsUtil
) extends Security[UserProfile] with I18nSupport {

  /**
   * Handles the index action.
   *
   * @return The result to display.
   */
  def index: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    //Future.successful(Ok(views.html.home(request.identity)))
    //println(request.identity)
    // Future.successful(Ok(views.html.index(request.identity)))
    Ok("")
  }

  /**
   * Handles the Sign Out action.
   *
   * @return The result to display.
   */
  def signOut: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Redirect(routes.ApplicationController.index)
    //silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    //silhouette.env.authenticatorService.discard(request.authenticator, result)
  }
}
