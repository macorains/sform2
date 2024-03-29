package net.macolabo.sform2.domain.utils

import java.util.Base64
import org.apache.commons.lang3.RandomStringUtils

import javax.crypto.Cipher
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}

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
    val IvSpec = new IvParameterSpec(ivsString.getBytes())
    encryptCipher.init(Cipher.ENCRYPT_MODE, Key, IvSpec)
    new String(Base64.getEncoder.encode(encryptCipher.doFinal(text.getBytes(charset))), charset)
  }

  def decrypt(text: String, ivsString: String): String = {
    val IvSpec = new IvParameterSpec(ivsString.getBytes())
    decryptCipher.init(Cipher.DECRYPT_MODE, Key, IvSpec)
    new String(decryptCipher.doFinal(Base64.getDecoder.decode(text.getBytes(charset))), charset)
  }

  def generateIV: String = {
    // encryptCipher.getIVを使うとbyte[] -> String -> byte[]の変換が
    // できないので、単純にランダム文字列を返す形に変更
    RandomStringUtils.randomAlphanumeric(16)
  }
}
