package net.macolabo.sform2.domain.services.ApiToken

import net.macolabo.sform2.domain.models.daos.ApiTokenDAO
import net.macolabo.sform2.domain.models.entity.api_token.ApiToken
import net.macolabo.sform2.domain.models.helper.SformTestHelper
import net.macolabo.sform2.domain.services.ApiToken.insert.ApiTokenInsertRequest
import org.mockito.ArgumentMatchers._
import org.mockito.MockitoSugar
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.play.PlaySpec
import play.inject.guice.GuiceApplicationBuilder
import play.api.inject.bind
import scalikejdbc.DBSession

import java.util.Base64


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
      val decodedBytes = Base64.getUrlDecoder.decode(response.token)
      decodedBytes.length mustBe 16
    }
  }
}
