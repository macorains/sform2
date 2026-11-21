package net.macolabo.sform2.domain.services.Transfer

import com.google.inject.Inject
import net.macolabo.sform2.domain.models.daos.TransferConfigSmtpMailDAOImpl
import net.macolabo.sform2.domain.services.Transfer.SmtpMailTransfer.TransferTaskRequest
import net.macolabo.sform2.domain.utils.Crypto
import play.api.Logging
import play.api.libs.json.JsValue
import scalikejdbc.DB

import java.util.Properties
import javax.mail.internet.{InternetAddress, MimeMessage}
import javax.mail.{Message, Session, Transport}
import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

class SmtpMailTransfer @Inject()(
  transferConfigSmtpMailDAO: TransferConfigSmtpMailDAOImpl
) extends BaseTransfer with Logging {

  override def receive: Receive = {
    case TransferTaskRequest(taskList, postdata, cryptoConfig) =>
      val taskBean = taskList.head

      val logText = taskBean.t_mail.map(tm => {
        val smtpConfigOpt = DB.localTx(implicit session =>
          transferConfigSmtpMailDAO.get(taskBean.user_group, taskBean.transfer_config_id)
        )
        smtpConfigOpt match {
          case None => "SmtpMailTransfer: Config not found."
          case Some(config) =>
            val crypto = Crypto(cryptoConfig.secret_key_string, cryptoConfig.cipher_algorithm, cryptoConfig.secret_key_algorithm, cryptoConfig.charset)
            val password = crypto.decrypt(config.smtp_password, config.iv_smtp_password)
            getToAddress(postdata, tm) match {
              case None => "SmtpMailTransfer: No to address."
              case Some(toAddress) =>
                val body = replaceTag(tm.body, postdata)
                sendSmtpMail(
                  config.smtp_host,
                  config.smtp_port,
                  config.smtp_user,
                  password,
                  config.from_address,
                  toAddress,
                  tm.subject,
                  body
                ) match {
                  case Success(_)  => "SmtpMailTransfer: Success."
                  case Failure(e)  => s"SmtpMailTransfer Failed: ${e.getMessage}"
                }
            }
        }
      }).getOrElse("SmtpMailTransfer Skipped.")

      endTask(taskList, postdata, logText)
  }

  def sendSmtpMail(
    host: String,
    port: Int,
    user: String,
    password: String,
    from: String,
    to: String,
    subject: String,
    body: String
  ): Try[Unit] = SmtpMailTransfer.sendSmtpMail(host, port, user, password, from, to, subject, body)

  private def getToAddress(postdata: JsValue, tm: TransferTaskBeanSesMail): Option[String] = {
    tm.to_address match {
      case Some(addr) if addr.nonEmpty => Some(addr)
      case _ =>
        tm.to_address_field match {
          case Some(field) => (postdata \ field).validate[String].asOpt
          case None        => None
        }
    }
  }

  @tailrec
  private def replaceTag(template: String, postdata: JsValue): String = {
    val r = "\\{%[a-zA-Z0-9]+%\\}".r
    r.findFirstIn(template) match {
      case Some(tag) =>
        val colName = tag.replace("{%", "").replace("%}", "")
        (postdata \ colName).asOpt[String] match {
          case Some(s) => replaceTag(template.replace(tag, s), postdata)
          case None    => replaceTag(template.replace(tag, ""), postdata)
        }
      case None => template
    }
  }
}

object SmtpMailTransfer {
  case class TransferTaskRequest(taskList: List[TransferTaskBean], postdata: JsValue, cryptoConfig: net.macolabo.sform2.domain.models.entity.CryptoConfig)

  def sendSmtpMail(
    host: String,
    port: Int,
    user: String,
    password: String,
    from: String,
    to: String,
    subject: String,
    body: String
  ): Try[Unit] = Try {
    val props = new Properties()
    props.put("mail.smtp.host", host)
    props.put("mail.smtp.port", port.toString)
    props.put("mail.smtp.auth", "true")
    props.put("mail.smtp.starttls.enable", "true")

    val session = Session.getInstance(props, new javax.mail.Authenticator {
      override protected def getPasswordAuthentication: javax.mail.PasswordAuthentication =
        new javax.mail.PasswordAuthentication(user, password)
    })

    val message = new MimeMessage(session)
    message.setFrom(new InternetAddress(from))
    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to))
    message.setSubject(subject, "UTF-8")
    message.setText(body, "UTF-8")
    Transport.send(message)
  }
}
