package net.macolabo.sform2.domain.utils

import net.macolabo.sform2.domain.models.helper.SformTestHelper
import org.scalatest.flatspec.{AnyFlatSpec, FixtureAnyFlatSpec}

class CryptoSpec extends AnyFlatSpec {

  it should "text crypto" in {
    val src = "hogehoge"
    val ivsString = "jdaeigavnsdscois"
    val secretKeyString = "PafewwaeheahWidNCoj9#kse@Ofdnqsf"
    val algorithmCipher = "AES/CBC/PKCS5Padding"
    val algorithmKey = "AES"
    val charset = "utf-8"

    val crypto = Crypto(ivsString, secretKeyString, algorithmCipher, algorithmKey, charset)

    val result = crypto.encrypt(src)
    println(result)
  }

}
