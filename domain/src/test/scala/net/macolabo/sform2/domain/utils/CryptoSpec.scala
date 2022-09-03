package net.macolabo.sform2.domain.utils

import org.scalatest.flatspec.AnyFlatSpec

class CryptoSpec extends AnyFlatSpec
{
  it should "text crypto" in {
    val src = "hogehoge"
    val ivsString = "jdaeigavnsdscois"
    val secretKeyString = "PafewwaeheahWidNCoj9#kse@Ofdnqsf"
    val algorithmCipher = "AES/CBC/PKCS5Padding"
    val algorithmKey = "AES"
    val charset = "utf-8"

    val crypto = Crypto(secretKeyString, algorithmCipher, algorithmKey, charset)

    val result = crypto.encrypt(src, ivsString)
    println(s"encrypt result : $result")
  }


  it should "iv generate" in {
    val secretKeyString = "PafewwaeheahWidNCoj9#kse@Ofdnqsf"
    val algorithmCipher = "AES/CBC/PKCS5Padding"
    val algorithmKey = "AES"
    val charset = "utf-8"
    val crypto = Crypto(secretKeyString, algorithmCipher, algorithmKey, charset)
    val ivs = crypto.generateIV
    println(s"generated ivString : $ivs")
  }
}
