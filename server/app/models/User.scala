package models

import play.api.libs.json._

case class User(id: Long, name: String)

object User{
    import anorm._
    import anorm.SqlParser._
    import utils.AnormExtension._

    import play.api.Play.current
    import play.api.db._

    implicit val format = Json.format[User]

    val userParser = {
        get[Long]("USER.id") ~
        get[String]("USER.name") map {
            case id ~ name => User(id, name)
        }
    }

    def create(name: String): Option[Long] = DB.withConnection { implicit c =>
        SQL(
            """
                INSERT INTO USER (name) VALUES ({name})
            """
        ).onParams(name).executeInsert(scalar[Long].singleOpt)
    }

    def users: Option[User] = DB.withConnection { implicit c =>
        SQL(
            """
                SELECT * FROM USER
            """
        ).as(userParser*).headOption
    }

    def user(name: String): Option[User] = DB.withConnection { implicit c =>
        SQL(
            """
                SELECT * FROM USER WHERE name = {name}
            """
        ).onParams(name).as(userParser*).headOption
    }
}