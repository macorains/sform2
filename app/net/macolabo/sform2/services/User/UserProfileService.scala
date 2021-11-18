package net.macolabo.sform2.services.User

import com.google.inject.Inject
import net.macolabo.sform2.models.daos.{FormDAO, UserDAO}
import org.pac4j.core.profile.service.AbstractProfileService
import org.pac4j.sql.profile.DbProfile
import org.skife.jdbi.v2.Handle
import scalikejdbc.DB

import java.util
import java.util.{List, Map}
import scala.collection.immutable.List
import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters._

class UserProfileService @Inject()(
  userDAO: UserDAO
) (implicit ex: ExecutionContext)
  extends AbstractProfileService[DbProfile]{

  val usersTable = "users"

  override protected def insert(attributes: util.Map[String, AnyRef]) = {
    val attributeList = attributes.asScala.toSeq
    val fields = attributeList.map(attr => attr._1).mkString(",")
    val values = Seq(attributeList.map(attr => attr._2))
    val questionMarks = Seq().padTo(fields.size, "?").mkString(",")
  }

  override protected def update(attributes: util.Map[String, AnyRef]) = {
    val attributeList = attributes.asScala.toSeq
    val fields = attributeList.map(attr => s"${attr._1} = :${attr._1}").mkString(",")
    val values = Seq(attributeList.map(attr => attr._2))
  }

  override protected def deleteById(id: String): Unit = {
//    val query = "delete from " + usersTable + " where " + getIdAttribute + " = :id"
//    execute(query, id)
    userDAO.delete(id)
  }

  protected def execute(query: String, args: Any *) = {

  }

  override protected def read(names: java.util.List[String], key: String, value: String): util.List[util.Map[String, AnyRef]] = {
    DB.localTx(implicit session => {
      val attributesList = names.asScala.toList.mkString(",")
      userDAO
        .find(attributesList, key, value)
        .map(user =>
          user.toList.map(u => (u._1, u._2.asInstanceOf[AnyRef])).toMap.asJava)
        .asJava
    })
  }

  protected def query(query: String, key: String, value: String): util.List[util.Map[String, AnyRef]] = {
    ???
//    var h = null
//    try {
//      h = dbi.open
//      logger.debug("Query: {} for key/value: {} / {}", query, key, value)
//      h.createQuery(query).bind(key, value).list(2)
//    } finally if (h != null) h.close()
  }


}
