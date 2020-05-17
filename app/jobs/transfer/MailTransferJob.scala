package jobs.transfer

import javax.inject.Inject
import models.daos.{ Transfer, TransferDetailLogDAO, TransferTaskDAO }
import models.entity.{ TransferTask, Postdata }
import play.api.libs.functional.syntax.unlift
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.Logger
import play.api.libs.mailer.{ Email, MailerClient }

class MailTransferJob @Inject() (
  mailerClient: MailerClient,
  transferDetailLogDAO: TransferDetailLogDAO,
  transferTaskDAO: TransferTaskDAO
) {

  case class MailTransferAddressList(id: Option[Int], name: Option[String], address: Option[String])
  implicit val jsonMailTransferAddressListWrites: Writes[MailTransferAddressList] = (
    (JsPath \ "id").writeNullable[Int] ~
    (JsPath \ "name").writeNullable[String] ~
    (JsPath \ "address").writeNullable[String]
  )(unlift(MailTransferAddressList.unapply))
  implicit val jsonMailTransferAddressListReads: Reads[MailTransferAddressList] = (
    (JsPath \ "id").readNullable[Int] ~
    (JsPath \ "name").readNullable[String] ~
    (JsPath \ "address").readNullable[String]
  )(MailTransferAddressList.apply _)

  case class MailTransferConfig(id: Option[Int], addressList: Option[List[MailTransferAddressList]])
  implicit val jsonMailTransferConfigWrites: Writes[MailTransferConfig] = (
    (JsPath \ "id").writeNullable[Int] ~
    (JsPath \ "addressList").writeNullable[List[MailTransferAddressList]]
  )(unlift(MailTransferConfig.unapply))
  implicit val jsonMailTransferConfigReads: Reads[MailTransferConfig] = (
    (JsPath \ "id").readNullable[Int] ~
    (JsPath \ "addressList").readNullable[List[MailTransferAddressList]]
  )(MailTransferConfig.apply _)

  case class MailTransferTaskConfig(formId: String, mailSubject: String, mailFrom: String, mailTo: String, mailBody: String)
  implicit val jsonMailTransferTaskConfigWrites: Writes[MailTransferTaskConfig] = (
    (JsPath \ "formId").write[String] ~
    (JsPath \ "mailSubject").write[String] ~
    (JsPath \ "mailFrom").write[String] ~
    (JsPath \ "mailTo").write[String] ~
    (JsPath \ "mailBody").write[String]
  )(unlift(MailTransferTaskConfig.unapply))
  implicit val jsonMailTransferTaskConfigReads: Reads[MailTransferTaskConfig] = (
    (JsPath \ "formId").read[String] ~
    (JsPath \ "mailSubject").read[String] ~
    (JsPath \ "mailFrom").read[String] ~
    (JsPath \ "mailTo").read[String] ~
    (JsPath \ "mailBody").read[String]
  )(MailTransferTaskConfig.apply _)

  def execute(transferTask: TransferTask, transfer: Transfer, postdataList: List[Postdata]): Unit = {
    // 開始ログ
    Logger.logger.info(s"    Start MailTransferJob [ID=${transferTask.id}, NAME=${transferTask.name}]")

    getTransferConfig(transfer) match {
      case transferConfig: JsSuccess[MailTransferConfig] =>
        getTransferTaskConfig(transferTask) match {
          case transferTaskConfig: JsSuccess[MailTransferTaskConfig] =>
            postdataList.size match {
              case listSize if listSize > 0 =>
                executeTransferTask(transferConfig.get, transferTaskConfig.get, postdataList, transfer.type_id)
              case _ =>
                // データが無い
                Logger.logger.info("    No data for transfer.")
            }
          case e2: JsError =>
            // transferTaskConfigが取れない
            Logger.logger.error("    Could not get TransferTaskConfig.")
        }
      case e1: JsError =>
        // transferConfigが取れない
        Logger.logger.error("    Could not get TransferConfig.")
    }

    // 終了ログ
    Logger.logger.info(s"    End MailTransferJob [ID=${transferTask.id}, NAME=${transferTask.name}]")
  }

  /**
   * タスク実行
   * @param mailTransferConfig MailTransefer設定
   * @param mailTransferTaskConfig MailTrransferのタスク設定
   * @param postdataList 送信対象データのリスト
   * @param transfer_type 転送タイプ
   */
  private def executeTransferTask(
    mailTransferConfig: MailTransferConfig,
    mailTransferTaskConfig: MailTransferTaskConfig, postdataList: List[Postdata], transfer_type: Int): Unit = {
    val result = postdataList.map(postdata => {
      sendMail(mailTransferConfig, mailTransferTaskConfig, postdata, transfer_type)
    })
  }

  /**
   * メール送信
   * @param mailTransferConfig MailTransfer設定
   * @param mailTransferTaskConfig MailTransferのタスク設定
   * @param postdata 送信対象データ
   * @param transfer_type 転送タイプ
   * @return 結果
   */
  private def sendMail(
    mailTransferConfig: MailTransferConfig,
    mailTransferTaskConfig: MailTransferTaskConfig, postdata: Postdata, transfer_type: Int) = {
    val mailSubject = replaceTag(mailTransferTaskConfig.mailSubject, postdata)
    val mailFrom = replaceTag(mailTransferTaskConfig.mailFrom, postdata)
    val mailTo = replaceTag(mailTransferTaskConfig.mailTo, postdata)
    val mailBody = replaceTag(mailTransferTaskConfig.mailBody, postdata)

    val messageId = mailerClient.send(Email(
      subject = mailSubject,
      from = mailFrom,
      to = Seq(mailTo),
      bodyText = Some(mailBody)
    ))
    messageId match {
      case id: String if id.length > 0 =>
        transferDetailLogDAO.save(
          postdata.postdata_id, transfer_type, 1,
          postdata.postdata.toString, "{}", 1, "{}", "", "")
        true
      case _ => false
    }
  }

  private def getTransferConfig(transfer: Transfer): JsResult[MailTransferConfig] = {
    transfer.config.validate[MailTransferConfig]
  }

  private def getTransferTaskConfig(transferTask: TransferTask): JsResult[MailTransferTaskConfig] = {
    val config = Json.toJson(transferTask.config)
    config.validate[MailTransferTaskConfig]
  }

  private def replaceTag(template: String, postdata: Postdata): String = {
    val r = "\\{%[a-zA-Z0-9]+%\\}".r

    r.findFirstIn(template) match {
      case Some(tag) =>
        val colName = tag.replace("{%", "").replace("%}", "")
        (postdata.postdata \ colName).asOpt[String] match {
          case Some(s) => replaceTag(template.replace(r.findFirstIn(template).getOrElse(""), s), postdata)
          case None => replaceTag(template.replace(r.findFirstIn(template).getOrElse(""), ""), postdata)
        }
      case None => template
    }
  }
}
