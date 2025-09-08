package net.macolabo.sform2.domain.services.ApiToken

import net.macolabo.sform2.domain.models.daos.{ApiTokenDAO, ApiTokenDAOImpl}
import net.macolabo.sform2.domain.models.entity.api_token.ApiToken
import net.macolabo.sform2.domain.models.helper.SformTestHelper
import net.macolabo.sform2.domain.services.ApiToken.insert.ApiTokenInsertRequest
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.testkit.TestKit
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.mockito.MockitoSugar
import org.mockito.MockitoSugar.{doReturn, spy, when}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatestplus.play.PlaySpec
import play.inject.guice.GuiceApplicationBuilder
import play.api.inject.bind
import scalikejdbc.DBSession

import java.time.LocalDateTime

class ApiTokenServiceImplSpec extends PlaySpec
  with SformTestHelper
  with MockitoSugar
  with Matchers
{
  "ApiTokenService" must {
    "insert" in {
      val apiTokenDao = mock[ApiTokenDAO]
      doReturn(1).when(apiTokenDao).save(any[ApiToken])(any[DBSession])

      val app = new GuiceApplicationBuilder()
        .overrides(bind[ApiTokenDAO].toInstance(apiTokenDao))
        .build()

      val service = app.injector.instanceOf(classOf[ApiTokenServiceImpl])

      val response = service.insert(ApiTokenInsertRequest(90L), "hoge", "fuga")
      response.token.length mustBe 32
    }
  }

}
