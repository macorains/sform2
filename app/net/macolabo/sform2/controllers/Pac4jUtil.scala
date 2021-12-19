package net.macolabo.sform2.controllers

import org.pac4j.core.profile.ProfileManager
import org.pac4j.play.PlayWebContext
import org.pac4j.play.scala.SecurityComponents
import play.api.mvc.RequestHeader

trait Pac4jUtil {

  def getProfiles(components: SecurityComponents)(implicit request: RequestHeader)  = {
    val webContext = new PlayWebContext(request)
    val profileManager = new ProfileManager(webContext, components.sessionStore)
    profileManager.getProfiles()

    //val profiles = profileManager.getProfiles()
    //asScala(profiles).toList
  }

}
