package net.macolabo.sform.api.controllers

import org.pac4j.core.profile.{ProfileManager, UserProfile}
import org.pac4j.play.PlayWebContext
import org.pac4j.play.context.PlayFrameworkParameters
import org.pac4j.play.scala.SecurityComponents
import play.api.mvc.RequestHeader

import java.util
import scala.jdk.CollectionConverters._

trait Pac4jUtil {

  def getProfiles(components: SecurityComponents)(implicit request: RequestHeader): util.List[UserProfile] = {
    val parameters = new PlayFrameworkParameters(request)
    val webContext = components.config.getWebContextFactory.newContext(parameters)
    val sessionStore = components.config.getSessionStoreFactory.newSessionStore(parameters)
    val profileManager = components.config.getProfileManagerFactory.apply(webContext, sessionStore)
    profileManager.getProfiles
  }

  def getAttributeValue(profiles: java.util.List[UserProfile], key: String): String = {
    profiles.asScala.headOption.map(profile => profile.getAttribute(key).asInstanceOf[String]).getOrElse("")
  }
}
