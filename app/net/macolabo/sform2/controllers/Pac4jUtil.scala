package net.macolabo.sform2.controllers

import org.pac4j.core.profile.{ProfileManager, UserProfile}
import org.pac4j.play.PlayWebContext
import org.pac4j.play.scala.SecurityComponents
import play.api.mvc.RequestHeader

import java.util
import scala.jdk.CollectionConverters._

trait Pac4jUtil {

  def getProfiles(components: SecurityComponents)(implicit request: RequestHeader): util.List[UserProfile] = {
    val webContext = new PlayWebContext(request)
    val profileManager = new ProfileManager(webContext, components.sessionStore)
    profileManager.getProfiles()
  }

  def getAttributeValue(profiles: java.util.List[UserProfile], key: String): String = {
    profiles.asScala.headOption.map(profile => profile.getAttribute(key).asInstanceOf[String]).getOrElse("")
  }
}
