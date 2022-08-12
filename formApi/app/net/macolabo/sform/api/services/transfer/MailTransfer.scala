package net.macolabo.sform.api.services.transfer

import com.amazonaws.regions.Regions
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import com.amazonaws.services.simpleemail.model.{Body, Content, Destination, Message, SendEmailRequest, SendEmailResult}
import com.google.inject.Inject
import net.macolabo.sform.api.models.daos.TransferConfigMailAddressDAOImpl
import net.macolabo.sform.api.services.transfer.MailTransfer.TransferTaskRequest
import play.api.Logging
import play.api.libs.json.JsValue
import scalikejdbc.DB

import java.math.BigInteger
import scala.annotation.tailrec
import scala.collection.JavaConverters.seqAsJavaListConverter

class MailTransfer @Inject()(
  transferConfigMailAddressDAO: TransferConfigMailAddressDAOImpl
) extends BaseTransfer with Logging {

  override def receive: Receive = {
    case TransferTaskRequest(taskList, postdata) =>
      val taskBean = taskList.head

      val logText = taskBean.t_mail.map(tm => {
        val result = getMailAddress(tm.from_address_id).flatMap(mailFrom => {
          getToAddress(postdata,tm).map(toAddress => {
            val sesClient = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.AP_NORTHEAST_1).build()
            val mailTextBody = replaceTag(tm.body, postdata)
            // TODO とりあえずテキスト形式のみ実装。後でHTMLも追加。
            // val mailHtmlBody = Messages("sform.mail.body.html.verification", verificationCode)
            val sesRequest = new SendEmailRequest()
              .withDestination(new Destination().withToAddresses(toAddress))
              .withMessage(new Message()
                .withBody(new Body()
                  // TODO とりあえずテキスト形式のみ実装。後でHTMLも追加。
                  // .withHtml(new Content().withCharset("UTF-8").withData(mailHtmlBody))
                  .withText(new Content().withCharset("UTF-8").withData(mailTextBody)))
                .withSubject(new Content().withCharset("UTF-8").withData(tm.subject)))
              .withSource(mailFrom)
            getCcAddress(postdata, tm).map(ccAddr => sesRequest.withDestination(new Destination().withCcAddresses(ccAddr)))
            val replyToAddresses = getReplyToAddress(tm.replyto_address_id)
            if(!replyToAddresses.isEmpty) sesRequest.withReplyToAddresses(replyToAddresses)
            sesClient.sendEmail(sesRequest)
          })
        })
        createLog(result)
      }).getOrElse("MailTransfer Skipped.")
      endTask(taskList, postdata, logText)
  }

  def getToAddress(postdata: JsValue, tm: TransferTaskBeanMail): Option[String] = {
    tm.to_address match {
      case Some(toAddress) if toAddress.nonEmpty => Some(toAddress)
      case _ =>
        tm.to_address_id match {
          case Some(toAddressId) => getMailAddress(toAddressId)
          case None =>
            tm.to_address_field match {
              case Some(toAddressField) => (postdata \ s"$toAddressField").validate[String].asOpt
              case None => None
            }
        }
    }
  }

  def getCcAddress(postdata: JsValue, tm: TransferTaskBeanMail): Option[String] = {
    tm.cc_address match {
      case Some(ccAddress) if ccAddress.nonEmpty => Some(ccAddress)
      case _ =>
        tm.cc_address_id match {
          case Some(ccAddressId) => getMailAddress(ccAddressId)
          case None =>
            tm.cc_address_field match {
              case Some(ccAddressField) => (postdata \ s"$ccAddressField").validate[String].asOpt
              case None => None
            }
        }
    }
  }

  def getMailAddress(mailAddressId: BigInteger): Option[String] = {
    DB.localTx(implicit session => {
      transferConfigMailAddressDAO.get(mailAddressId).map(rs => rs.address)
    })
  }

  def getReplyToAddress(replyToAddressId: BigInteger): java.util.List[String] = {
    DB.localTx(implicit session => {
      transferConfigMailAddressDAO.get(replyToAddressId).map(rs => rs.address)
    }).map(address => List(address).asJava).getOrElse(java.util.List.of())
  }

  def createLog(result: Option[SendEmailResult]): String = {
    result.map(_.getMessageId).getOrElse("Failed.")
  }

  @tailrec
  private def replaceTag(template: String, postdata: JsValue): String = {
    val r = "\\{%[a-zA-Z0-9]+%\\}".r

    r.findFirstIn(template) match {
      case Some(tag) =>
        val colName = tag.replace("{%", "").replace("%}", "")
        (postdata \ colName).asOpt[String] match {
          case Some(s) => replaceTag(template.replace(r.findFirstIn(template).getOrElse(""), s), postdata)
          case None => replaceTag(template.replace(r.findFirstIn(template).getOrElse(""), ""), postdata)
        }
      case None => template
    }
  }
}

object MailTransfer {
  case class TransferTaskRequest(taskList: List[TransferTaskBean], postdata: JsValue)
}
