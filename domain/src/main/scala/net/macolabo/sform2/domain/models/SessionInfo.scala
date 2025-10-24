package net.macolabo.sform2.domain.models

import play.api.mvc.Session

case class SessionInfo (
  user_id: String,
  user_group: String,
  user_role: String
)

object SessionInfo {
  def apply(session: Session): SessionInfo = {
    require(session.get("user_id").nonEmpty, "Could not get user_id.")
    require(session.get("user_group").nonEmpty, "Could not get user_group.")
    require(session.get("user_role").nonEmpty, "Could not get user_role")
    SessionInfo(session.get("user_id").get, session.get("user_group").get, session.get("user_role").get)
  }
}
