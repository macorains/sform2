package net.macolabo.sform2.domain.utils

import java.util.Base64

import com.google.inject.Inject
import javax.crypto.Cipher
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}
import play.api.Configuration

case class Crypto (
  secretKeyString: String,
  algorithmCipher: String,
  algorithmKey: String,
  charset: String
){
  private val Key = new SecretKeySpec(secretKeyString.getBytes(charset), algorithmKey)
  private val encryptCipher = Cipher.getInstance(algorithmCipher)
  private val decryptCipher = Cipher.getInstance(algorithmCipher)

  def encrypt(text: String, ivsString: String): String = {
    val IvSpec = new IvParameterSpec(ivsString.getBytes(charset))
    encryptCipher.init(Cipher.ENCRYPT_MODE, Key, IvSpec)
    new String(Base64.getEncoder.encode(encryptCipher.doFinal(text.getBytes(charset))), charset)
  }

  def decrypt(text: String, ivsString: String): String = {
    val IvSpec = new IvParameterSpec(ivsString.getBytes(charset))
    decryptCipher.init(Cipher.DECRYPT_MODE, Key, IvSpec)
    new String(decryptCipher.doFinal(Base64.getDecoder.decode(text.getBytes(charset))), charset)
  }

  def generateIV: String = {
    encryptCipher.init(Cipher.ENCRYPT_MODE, Key)
    new String(encryptCipher.getIV, charset)
  }
}
