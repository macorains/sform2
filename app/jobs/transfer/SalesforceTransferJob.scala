package jobs.transfer

import com.sforce.soap.partner.{ PartnerConnection, SaveResult }
import com.sforce.soap.partner.sobject.SObject
import javax.inject.Inject
import models.connector.SalesforceConnector
import models.daos.{ Postdata, Transfer, TransferDetailLogDAO, TransferTaskDAO }
import models.entity.TransferTask
import play.api.libs.json._
import play.api.Logger

class SalesforceTransferJob @Inject() (
  salesforceConnector: SalesforceConnector,
  transferDetailLogDao: TransferDetailLogDAO,
  transferTaskDAO: TransferTaskDAO
) {
  case class SalesforceTransferConfig(user: String, password: String, securityToken: String)
  object SalesforceTransferConfig {
    implicit def jsonSalesforceTransferConfigWrites: Writes[SalesforceTransferConfig] = Json.writes[SalesforceTransferConfig]
    implicit def jsonSalesforceTransferConfigReads: Reads[SalesforceTransferConfig] = Json.reads[SalesforceTransferConfig]
  }
  case class SalesforceTransferTaskConfig(formId: String, sfObject: String, columnConvertDefinition: List[JsObject])
  object SalesforceTransferTaskConfig {
    implicit def jsonSalesforceTransferTaskConfigWrites: Writes[SalesforceTransferTaskConfig] = Json.writes[SalesforceTransferTaskConfig]
    implicit def jsonSalesforceTransferTaskConfigReads: Reads[SalesforceTransferTaskConfig] = Json.reads[SalesforceTransferTaskConfig]
  }

  case class SalesforceDataSet(postdata_id: Int, sobject: SObject, postdata: JsValue)

  def execute(transferTask: TransferTask, transfer: Transfer, postdataList: List[Postdata]): Unit = {
    // 開始ログ
    Logger.info(s"Start SalesforceTransferJob [ID=${transferTask.id}, NAME=${transferTask.name}]")

    getTransferConfig(transfer) match {
      case transferConfig: JsSuccess[SalesforceTransferConfig] => {
        getTransferTaskConfig(transferTask) match {
          case transferTaskConfig: JsSuccess[SalesforceTransferTaskConfig] => {
            postdataList.size match {
              case listSize if listSize > 0 => {
                executeTransferTask(transferConfig.get, transferTaskConfig.get, postdataList, transfer.type_id)
              }
              case _ => {
                // データが無い
                Logger.info("No data for transfer.")
              }
            }
          }
          case e2: JsError => {
            // transferTaskConfigが取れない
            Logger.error("Could not get TransferTaskConfig.")
          }
        }
      }
      case e1: JsError => {
        // transferConfigが取れない
        Logger.error("Could not get TransferConfig.")
      }
    }
    // 終了ログ
    Logger.info(s"End SalesforceTransferJob [ID=${transferTask.id}, NAME=${transferTask.name}]")
  }

  /**
   * タスク実行
   * @param salesforceTransferConfig SalesforceTransefer設定
   * @param salesforceTransferTaskConfig SalesforceTrransferのタスク設定
   * @param postdataList 送信対象データのリスト
   * @param transfer_type 転送タイプ
   */
  private def executeTransferTask(
    salesforceTransferConfig: SalesforceTransferConfig,
    salesforceTransferTaskConfig: SalesforceTransferTaskConfig, postdataList: List[Postdata], transfer_type: Int) = {
    // 送信データを作成
    val sfobjArray: Array[SalesforceDataSet] = convertPostdata(salesforceTransferTaskConfig, postdataList)
    // SFで送信
    sendToSalesforce(salesforceTransferConfig, sfobjArray, transfer_type)
  }

  /**
   * postdataから送信用データ作成
   * @param postdataList
   */
  private def convertPostdata(salesforceTransferTaskConfig: SalesforceTransferTaskConfig, postdataList: List[Postdata]) = {
    postdataList.map(postdata => {
      val sfobj: SObject = new SObject()
      sfobj.setType(salesforceTransferTaskConfig.sfObject)

      salesforceTransferTaskConfig.columnConvertDefinition.map(ccd => {
        getCdef(ccd)
      }).foreach(cd => {
        val colValue = (postdata.postdata \ s"$cd._2").validate[String] match {
          case c: JsSuccess[String] => c.get
          case _ => ""
        }
        sfobj.setField(cd._1.replace("\"", ""), colValue)
      })
      SalesforceDataSet(postdata.postdata_id, sfobj, postdata.postdata)
    }).toArray

  }

  /**
   * JsObjectから項目変換定義を作成
   * @param cdef JsObject
   * @return Salesforce項目とSForm項目のタプル
   */
  private def getCdef(cdef: JsObject): (String, String) = {
    (cdef \ s"sfCol").validate[String] match {
      case sfCol: JsResult[String] =>
        (cdef \ s"sformCol").validate[String] match {
          case sformCol: JsResult[String] =>
            (sfCol.get, sformCol.get)
          case _ => ("", "")
        }
      case _ => ("", "")
    }
  }

  /**
   * Salesforceへのデータ送信
   * @param salesforceTransferConfig Salesforce転送設定
   * @param sfobjArray 送信するデータ
   */
  private def sendToSalesforce(salesforceTransferConfig: SalesforceTransferConfig, sfobjArray: Array[SalesforceDataSet], transfer_type: Int): Boolean = {
    val connection = salesforceConnector.getConnection(
      salesforceTransferConfig.user,
      salesforceTransferConfig.password,
      salesforceTransferConfig.securityToken
    ).getOrElse(None)

    connection match {
      case pc: PartnerConnection => {
        var result = salesforceConnector.create(pc, sfobjArray.map(b => b.sobject))
        createResultLog(sfobjArray, result, transfer_type)
        true
      }
      case _ => {
        // SFに接続できない
        Logger.error("Could not connect to Salesforce.")
        false
      }
    }

  }

  private def createResultLog(sfobjArray: Array[SalesforceDataSet], result: Array[SaveResult], transfer_type: Int): Unit = {
    (0 to sfobjArray.length - 1).foreach(index => {
      val errors = result(index).getErrors.map(e => {
        e.getMessage
      }).mkString("{\"message\":\"", ",", "\"}")
      transferDetailLogDao.save(
        sfobjArray(index).postdata_id,
        transfer_type,
        if (result(index).getSuccess) 1 else 9,
        sfobjArray(index).postdata.toString,
        "{}",
        if (result(index).getSuccess) 1 else 9,
        errors,
        "",
        ""
      )
    })
  }

  private def getTransferConfig(transfer: Transfer): JsResult[SalesforceTransferConfig] = {
    transfer.config.validate[SalesforceTransferConfig]
  }

  private def getTransferTaskConfig(transferTask: TransferTask): JsResult[SalesforceTransferTaskConfig] = {
    transferTask.config.validate[SalesforceTransferTaskConfig]
  }

}
