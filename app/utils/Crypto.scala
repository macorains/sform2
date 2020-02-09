package utils

import com.google.inject.Inject
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}
import play.api.Configuration

case class Crypto @Inject()(config: Configuration){
  private val Algorithm = config.get[String]("sform.crypt.algorithm.cipher")
  private val Key = new SecretKeySpec(Base64.getDecoder.decode(config.get[String]("sform.crypt.key")), config.get[String]("sform.crypt.algorithm.key"))
  private val IvSpec = new IvParameterSpec(new Array[Byte](16))

  def encrypt(text: String): String = {
    val cipher = Cipher.getInstance(Algorithm)
    cipher.init(Cipher.ENCRYPT_MODE, Key, IvSpec)

    new String(Base64.getEncoder.encode(cipher.doFinal(text.getBytes("utf-8"))), "utf-8")
  }

  def decrypt(text: String): String = {
    val cipher = Cipher.getInstance(Algorithm)
    cipher.init(Cipher.DECRYPT_MODE, Key, IvSpec)

    new String(cipher.doFinal(Base64.getDecoder.decode(text.getBytes("utf-8"))), "utf-8")
  }
}
