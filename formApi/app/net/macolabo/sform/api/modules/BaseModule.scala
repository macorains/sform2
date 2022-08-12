package net.macolabo.sform.api.modules

import com.google.inject.AbstractModule
import net.macolabo.sform.api.models.daos.{AuthTokenDAO, AuthTokenDAOImpl, FormColDAO, FormColDAOImpl, FormColSelectDAO, FormColSelectDAOImpl, FormColValidationDAO, FormColValidationDAOImpl, FormDAO, FormDAOImpl, FormTransferTaskConditionDAO, FormTransferTaskConditionDAOImpl, FormTransferTaskDAO, FormTransferTaskDAOImpl, FormTransferTaskMailDAO, FormTransferTaskMailDAOImpl, FormTransferTaskSalesforceDAO, FormTransferTaskSalesforceDAOImpl, FormTransferTaskSalesforceFieldDAO, FormTransferTaskSalesforceFieldDAOImpl, PostdataDAO, PostdataDAOImpl}
import net.macolabo.sform.api.services.{AuthTokenService, AuthTokenServiceImpl}
import net.codingwell.scalaguice.ScalaModule
import net.macolabo.sform.api.models.entity.formtransfertask.FormTransferTaskMail


/**
 * The base Guice module.
 */
class BaseModule extends AbstractModule with ScalaModule {

  /**
   * Configures the module.
   */
  override def configure(): Unit = {
    bind[AuthTokenDAO].to[AuthTokenDAOImpl]
    bind[AuthTokenService].to[AuthTokenServiceImpl]
    bind[PostdataDAO].to[PostdataDAOImpl]
    bind[FormDAO].to[FormDAOImpl]
    bind[FormColDAO].to[FormColDAOImpl]
    bind[FormColSelectDAO].to[FormColSelectDAOImpl]
    bind[FormColValidationDAO].to[FormColValidationDAOImpl]
    bind[FormTransferTaskDAO].to[FormTransferTaskDAOImpl]
    bind[FormTransferTaskConditionDAO].to[FormTransferTaskConditionDAOImpl]
    bind[FormTransferTaskMailDAO].to[FormTransferTaskMailDAOImpl]
    bind[FormTransferTaskSalesforceDAO].to[FormTransferTaskSalesforceDAOImpl]
    bind[FormTransferTaskSalesforceFieldDAO].to[FormTransferTaskSalesforceFieldDAOImpl]
  }
}
