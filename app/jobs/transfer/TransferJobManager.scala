package jobs.transfer

import javax.inject.Inject
import akka.actor.Actor
import models.daos._
import models.entity._
import models.json.TransferTaskJson
import play.api.Logger

class TransferJobManager @Inject() (
  formsDao: FormsDAO,
  transfersDao: TransfersDAO,
  transferTaskDAO: TransferTaskDAO,
  postdataDao: PostdataDAO,
  salesforceTransferJob: SalesforceTransferJob,
  mailTransferJob: MailTransferJob
) extends Actor {

  def receive: Receive = {
    // 普通に実行
    case "Exec" => {
      Logger.info("---------- TransferJobManager Start.")
      // Transferを取得
      val transferList = transfersDao.getTransfetList
      // ステータス有効のフォームを検索
      val formIdList = formsDao.getListForTransferJobManager

      formIdList.foreach(formId => {
        Logger.info(s"  FormId : ${formId}")
        // 処理対象のフォームデータを検索
        val postdataList = postdataDao.getPostdata(formId)
        // 転送タスクを検索
        val transfetTaskList = transferTaskDAO.getTransferTaskListByFormId(formId)

        transfetTaskList.foreach(transferTask => {

          // ジョブ実行ログを出力（ジョブ起動中ステータス）
          // 各転送タスクごとにジョブ起動
          dispatchTransferJob(transferTask, transferList, postdataList)
          // ジョブ実行ログを出力（ジョブ完了ステータスorジョブ異常終了ステータス）
        })
      })
      Logger.info("---------- TransferJobManager Finish.")
    }
    // 状態表示
    case "Status" => {
      // TODO 実行中ジョブがあれば表示
      println("Status")
    }
  }

  def dispatchTransferJob(transferTask: TransferTask, transferList: List[Transfer], postdataList: List[Postdata]) = {
    transferList.foreach(transfer => {
      transfer.type_id match {
        case t: Int if transfer.type_id == transferTask.transfer_type_id => {
          t match {
            // SalesforceTransfer
            case 1 => {
              salesforceTransferJob.execute(transferTask, transfer, postdataList)
            }
            // MailTransfer
            case 2 => {
              mailTransferJob.execute(transferTask, transfer, postdataList)
            }
            // 想定外の転送タイプID
            case _ => {
              Logger.error(s"Illegal transfer_type_id : ${t}")
            }
          }
        }
        case _ => {
          Logger.error(s"Could not dispatch transfer job : ${transferTask.id}")
        }
      }
    })
  }

}
