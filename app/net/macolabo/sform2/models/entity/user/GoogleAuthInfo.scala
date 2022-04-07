package net.macolabo.sform2.models.entity.user

case class GoogleAuthInfo (
client_id: String,
project_id: String,
auth_uri: String,
token_uri: String,
auth_provider_x509_cert_url: String,
client_secret: String,
redirect_uris: List[String],
javascript_origins: List[String]
                          )
