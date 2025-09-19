package net.macolabo.sform2.domain.models

import play.api.mvc.Session

case class SessionInfo (
  user_id: String,
  user_group: String
)

object SessionInfo {
  def apply(session: Session): SessionInfo = {
    require(session.get("user_id").nonEmpty, "Could not get user_id.")
    require(session.get("user_group").nonEmpty, "Could not get user_group.")
    SessionInfo(session.get("user_id").get, session.get("user_group").get)
  }
}
