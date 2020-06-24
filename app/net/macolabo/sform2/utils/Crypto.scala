package net.macolabo.sform2.utils

import java.util.Base64

import com.google.inject.Inject
import javax.crypto.Cipher
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}
import play.api.Configuration

case class Crypto @Inject()(config: Configuration){

  private val Algorithm = config.get[String]("sform.crypt.algorithm.cipher")
  private val Key = new SecretKeySpec(Base64.getDecoder.decode(config.get[String]("sform.crypt.key")), config.get[String]("sform.crypt.algorithm.key"))
  private val IvSpec = new IvParameterSpec(new Array[Byte](16))
  private val Charset = config.get[String]("sform.crypt.charset")

  def encrypt(text: String): String = {
    val cipher = Cipher.getInstance(Algorithm)
    cipher.init(Cipher.ENCRYPT_MODE, Key, IvSpec)

    new String(Base64.getEncoder.encode(cipher.doFinal(text.getBytes(Charset))), Charset)
  }

  def decrypt(text: String): String = {
    val cipher = Cipher.getInstance(Algorithm)
    cipher.init(Cipher.DECRYPT_MODE, Key, IvSpec)
    new String(cipher.doFinal(Base64.getDecoder.decode(text.getBytes(Charset))), Charset)
  }
}
