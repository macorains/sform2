package net.macolabo.sform2.domain.utils

import java.util.Base64
import com.google.inject.Inject

import javax.crypto.Cipher
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}
import play.api.Configuration
import scalikejdbc.Binders.bytes

import java.math.BigInteger
import javax.xml.bind.DatatypeConverter

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
    println(s"byteLength : ${new BigInteger(ivsString, 16).toByteArray.length}")
    val IvSpec = new IvParameterSpec(new BigInteger(ivsString, 16).toByteArray)
    //val IvSpec = new IvParameterSpec(ivsString.getBytes(  ))
    //val IvSpec = new IvParameterSpec(DatatypeConverter.parseHexBinary(ivsString))
    encryptCipher.init(Cipher.ENCRYPT_MODE, Key, IvSpec)
    new String(Base64.getEncoder.encode(encryptCipher.doFinal(text.getBytes(charset))), charset)
  }

  def decrypt(text: String, ivsString: String): String = {
    println(s"byteLength : ${new BigInteger(ivsString, 16).toByteArray.length}")
    val IvSpec = new IvParameterSpec(new BigInteger(ivsString, 16).toByteArray)
    // val IvSpec = new IvParameterSpec(ivsString.getBytes(charset))
    decryptCipher.init(Cipher.DECRYPT_MODE, Key, IvSpec)
    new String(decryptCipher.doFinal(Base64.getDecoder.decode(text.getBytes(charset))), charset)
  }

  def generateIV: String = {
    encryptCipher.init(Cipher.ENCRYPT_MODE, Key)
    // new String(Base64.getEncoder.encode(encryptCipher.getIV), charset)
    dump(encryptCipher.getIV)
  }

  def dump(bytes: Array[Byte]): String = {
    println(s"dump bytes length : ${bytes.length}")
    //DatatypeConverter.printHexBinary(bytes)
    new BigInteger(bytes).toString(16)
  }
  /*
 public static String dump(byte[] bytes, int radix) {
   return new java.math.BigInteger(1, bytes).toString(radix);
}
  */
}
