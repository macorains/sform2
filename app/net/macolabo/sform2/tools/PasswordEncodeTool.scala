package net.macolabo.sform2.tools

import org.apache.shiro.authc.credential.DefaultPasswordService

object PasswordEncodeTool {
  def main(args: Array[String]): Unit ={
    val input = scala.io.StdIn.readLine()
    val service = new DefaultPasswordService
    val output = service.encryptPassword(input)
    println(output)
  }
}
