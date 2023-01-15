package net.macolabo.sform2.domain.services.GoogleAuth

trait GoogleAuthService
{
  def getToken(code: String, baseUrl: String) :Option[String]
}
