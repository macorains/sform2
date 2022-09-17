package net.macolabo.sform2.domain.utils

import org.scalatest.flatspec.AnyFlatSpec

import java.math.BigInteger

class CryptoSpec extends AnyFlatSpec
{
  it should "text crypto" in {
    val src = "hogehoge"
    val secretKeyString = "PafewwaeheahWidNCoj9#kse@Ofdnqsf"
    val algorithmCipher = "AES/CBC/PKCS5Padding"
    val algorithmKey = "AES"
    val charset = "utf-8"

    val crypto = Crypto(secretKeyString, algorithmCipher, algorithmKey, charset)

    val ivsString = crypto.generateIV
    println(s"ivsString : $ivsString")
    val encryptoResult = crypto.encrypt(src, ivsString)
    println(s"encrypt result : $encryptoResult")
    val decryptoResult = crypto.decrypt(encryptoResult, ivsString)
    println(s"decrypto result : $decryptoResult")
  }


  it should "iv generate" in {
    val secretKeyString = "PafewwaeheahWidNCoj9#kse@Ofdnqsf"
    val algorithmCipher = "AES/CBC/PKCS5Padding"
    val algorithmKey = "AES"
    val charset = "utf-8"
    val crypto = Crypto(secretKeyString, algorithmCipher, algorithmKey, charset)
    val ivs = crypto.generateIV
    println(s"generated ivString : $ivs")
    val ivsBytes = ivs.getBytes()
    println(s"byte length : ${ivsBytes.size}")
  }
}
