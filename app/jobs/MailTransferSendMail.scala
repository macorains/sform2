package jobs

import javax.inject.Inject

import akka.actor.Actor
import models.daos._
import play.api.i18n.Messages
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.Logger
import play.api.libs.mailer.{ Email, MailerClient }

class MailTransferSendMail @Inject() (
  transfersDao: TransfersDAO,
  transferTaskDao: TransferTaskDAO,
  transferLogDao: TransferLogDAO,
  transferDetailLogDao: TransferDetailLogDAO,
  postdataDao: PostdataDAO,
  mailerClient: MailerClient
) extends Actor {

  val transferType = 2

  case class MailTransferAddressList(id: Option[Int], name: Option[String], address: Option[String])
  implicit val jsonMailTransferAddressListWrites = (
    (JsPath \ "id").writeNullable[Int] ~
    (JsPath \ "name").writeNullable[String] ~
    (JsPath \ "address").writeNullable[String]
  )(unlift(MailTransferAddressList.unapply))
  implicit val jsonMailTransferAddressListReads = (
    (JsPath \ "id").readNullable[Int] ~
    (JsPath \ "name").readNullable[String] ~
    (JsPath \ "address").readNullable[String]
  )(MailTransferAddressList.apply _)

  case class MailTransferConfig(id: Option[Int], addressList: Option[List[MailTransferAddressList]])
  implicit val jsonMailTransferConfigWrites = (
    (JsPath \ "id").writeNullable[Int] ~
    (JsPath \ "addressList").writeNullable[List[MailTransferAddressList]]
  )(unlift(MailTransferConfig.unapply))
  implicit val jsonMailTransferConfigReads: Reads[MailTransferConfig] = (
    (JsPath \ "id").readNullable[Int] ~
    (JsPath \ "addressList").readNullable[List[MailTransferAddressList]]
  )(MailTransferConfig.apply _)

  case class MailTransferTaskConfig(formId: String, mailSubject: String, mailFrom: String, mailTo: String, mailBody: String)
  implicit val jsonMailTransferTaskConfigWrites = (
    (JsPath \ "formId").write[String] ~
    (JsPath \ "mailSubject").write[String] ~
    (JsPath \ "mailFrom").write[String] ~
    (JsPath \ "mailTo").write[String] ~
    (JsPath \ "mailBody").write[String]
  )(unlift(MailTransferTaskConfig.unapply))
  implicit val jsonMailTransferTaskConfigReads = (
    (JsPath \ "formId").read[String] ~
    (JsPath \ "mailSubject").read[String] ~
    (JsPath \ "mailFrom").read[String] ~
    (JsPath \ "mailTo").read[String] ~
    (JsPath \ "mailBody").read[String]
  )(MailTransferTaskConfig.apply _)

  def receive: Receive = {
    case msg: String => {
      transfersDao.getTransfer(transferType) match {
        case Some(t1: transfersDao.Transfer) => {
          t1.config.validate[MailTransferConfig] match {
            case c1: JsSuccess[MailTransferConfig] => {
              getTransferTask(c1.get)
              Logger.debug("")
            }
            case e: JsError => {
              Logger.debug("")
            }
          }
        }
        case None => {
          Logger.debug("")
        }
      }
    }
  }
  def getTransferTask(transferConfig: MailTransferConfig) = {
    val log_id = transferLogDao.create(transferType)
    transferTaskDao.getTransferTaskList(transferType).map(t => {
      t.config.validate[MailTransferTaskConfig] match {
        case c: JsSuccess[MailTransferTaskConfig] => {
          val posts = postdataDao.getPostdataByFormHashedId(c.get.formId, transferType)
          transferLogDao.start(log_id, t.id, posts.map(p => { p.postdata.toString() }).mkString("[", ",", "]"))
          posts.map(p => {
            sendMail(transferConfig, c.get, p)
          })
          // TransferLogDaoに作業終了記録
          transferLogDao.update(log_id, 1)
        }
        case e: JsError => None
      }
    })
  }

  def sendMail(transferConfig: MailTransferConfig, taskConfig: MailTransferTaskConfig, post: postdataDao.Postdata) = {

    // ToDo　メール本文のタグ置換処理
    val mailSubject = replaceTag(taskConfig.mailSubject, post)
    val mailFrom = replaceTag(taskConfig.mailFrom, post)
    val mailTo = replaceTag(taskConfig.mailTo, post)
    val mailBody = replaceTag(taskConfig.mailBody, post)

    Logger.info("MailTransferSendMail.sendMail:" + mailSubject + "," + mailFrom + "," + mailTo + "," + mailBody)

    mailerClient.send(Email(
      subject = mailSubject,
      from = mailFrom,
      to = Seq(mailTo),
      bodyText = Some(mailBody)
    ))

    transferDetailLogDao.save(
      post.postdata_id, transferType, 1,
      post.postdata.toString, "{}", 1, "{}", "", "")

  }

  def replaceTag(mailBody: String, post: postdataDao.Postdata): String = {
    val r = "\\{%[a-zA-Z0-9]+%\\}".r

    r.findFirstIn(mailBody) match {
      case Some(s) => {
        var colName = s.replace("{%", "").replace("%}", "")
        (post.postdata \ colName).asOpt[String] match {
          case Some(s) => replaceTag(mailBody.replace(r.findFirstIn(mailBody).getOrElse(""), s), post)
          case None => replaceTag(mailBody.replace(r.findFirstIn(mailBody).getOrElse(""), ""), post)
        }
      }
      case None => mailBody
    }
  }
}
