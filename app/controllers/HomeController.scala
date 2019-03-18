package controllers

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import javax.inject._
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.mvc._
import utils.auth.DefaultEnv

import scala.concurrent.Future

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton //class HomeController @Inject() extends Controller {
class HomeController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv]
)(
  implicit
  webJarsUtil: WebJarsUtil
) extends AbstractController(components) with I18nSupport {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  /*
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  */
  def index: Action[AnyContent] = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    // Future.successful(Ok(views.html.home(request.identity)))
    Future.successful(Ok(""))
  }

}
