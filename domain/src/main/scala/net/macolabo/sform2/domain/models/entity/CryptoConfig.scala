package net.macolabo.sform2.domain.models.entity

case class CryptoConfig(
  secret_key_algorithm: String,
  cipher_algorithm: String,
  secret_key_string: String,
  charset: String
)
