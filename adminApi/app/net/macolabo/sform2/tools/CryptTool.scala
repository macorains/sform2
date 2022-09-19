package net.macolabo.sform2.tools

import net.macolabo.sform2.domain.utils.Crypto
import play.api.Play

/**
 * Cryptoの動作を確認するツール
 *
 */
object CryptTool {
  def main(args: Array[String]): Unit ={
    val input = scala.io.StdIn.readLine()
    println(input)
  }
}
