package net.macolabo.sform2.utils

import java.nio.ByteBuffer
import java.security.SecureRandom
import org.apache.commons.codec.binary.Base64

trait TokenUtil {

  def generateToken: String = {
    val buf = ByteBuffer.allocate(16)
    val randomBytes = new Array[Byte](16)
    val secureRandom = new SecureRandom
    secureRandom.nextBytes(randomBytes)
    buf.put(randomBytes)
    Base64.encodeBase64URLSafeString(buf.array())
  }
}
