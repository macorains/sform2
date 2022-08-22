package net.macolabo.sform2.domain.utils

import java.util.Base64

import com.google.inject.Inject
import javax.crypto.Cipher
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}
import play.api.Configuration

case class Crypto (
  ivs: Array[Byte],
  secretKey: Array[Byte],
  algorithmCipher: String,
  algorithmKey: String,
  charset: String
){
  private val Key = new SecretKeySpec(secretKey, algorithmKey)
  private val IvSpec = new IvParameterSpec(ivs)
  private val encryptCipher = Cipher.getInstance(algorithmCipher)
  private val decryptCipher = Cipher.getInstance(algorithmCipher)
  encryptCipher.init(Cipher.ENCRYPT_MODE, Key, IvSpec)
  decryptCipher.init(Cipher.DECRYPT_MODE, Key, IvSpec)

  def encrypt(text: String): String = {
    new String(Base64.getEncoder.encode(encryptCipher.doFinal(text.getBytes(charset))), charset)
  }

  def decrypt(text: String): String = {
    new String(decryptCipher.doFinal(Base64.getDecoder.decode(text.getBytes(charset))), charset)
  }
}
