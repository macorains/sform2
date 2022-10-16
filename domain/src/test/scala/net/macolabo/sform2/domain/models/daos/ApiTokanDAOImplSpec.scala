package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.helper.SformTestHelper
import net.macolabo.sform2.domain.models.daos.ApiTokenDAOImpl
import net.macolabo.sform2.domain.models.entity.api_token.ApiToken
import org.scalatest.flatspec.FixtureAnyFlatSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import scalikejdbc._
import scalikejdbc.scalatest.AutoRollback

import java.time.LocalDateTime
import java.util.UUID

class ApiTokanDAOImplSpec extends FixtureAnyFlatSpec
  with GuiceOneServerPerSuite
  with SformTestHelper
  with AutoRollback
{
  behavior of "ApiToken"

  it should "save api token" in { implicit session =>
    val dateTime = LocalDateTime.of(2022, 4, 1, 12, 0, 0)
    val apiToken = ApiToken(
      UUID.randomUUID(),
      "hoge",
      "fuga",
      dateTime,
      dateTime,
      "x",
      "y"
    )
    val apiTokenDAO = new ApiTokenDAOImpl()
    apiTokenDAO.save(apiToken)

    val t = ApiToken.syntax("t")
    val result = withSQL (
      select(
        t.id,
        t.username,
        t.password,
        t.expiry,
        t.created,
        t.created_user,
        t.user_group
      )
        .from(ApiToken as t)
        .where
        .eq(t.id, apiToken.id.toString)
    ).map(rs => ApiToken(rs)).list().apply()
    val newToken = result.head
    assert(result.nonEmpty)
    assert(apiToken.id.equals(newToken.id))
    assert(apiToken.username.equals(newToken.username))
    assert(apiToken.password.equals(newToken.password))
    assert(apiToken.expiry.equals(newToken.expiry))
    assert(apiToken.created.equals(newToken.created))
  }
}
