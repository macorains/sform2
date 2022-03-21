package net.macolabo.sform2.services.GoogleAuth

trait GoogleAuthService
{
  def getToken(code: String) :Unit
}
