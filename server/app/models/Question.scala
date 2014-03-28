package models

import org.joda.time.DateTime
import play.api.libs.json._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class Question(
  id:       Long,
  text:     String,
  created:  DateTime,
  delay:    Option[Long],
  closed:   Option[DateTime],
  user:     User
)

case class TempQuestion(
  text:     String,
  delay:    Option[Long],
  closed:   Option[DateTime],
  idUser:   Long
)

object TempQuestion{
  implicit val format = Json.format[TempQuestion]
}

object Question{
    import anorm._
    import anorm.SqlParser._
    import utils.AnormExtension._

    import play.api.Play.current
    import play.api.db._

    implicit val format = Json.format[Question]

    val questionParser = {
        get[Long]("QUESTION.id") ~
        get[String]("QUESTION.text") ~
        get[DateTime]("QUESTION.created") ~
        get[Option[Long]]("QUESTION.delay") ~
        get[Option[DateTime]]("QUESTION.closed") ~
        get[Long]("USER.id") ~
        get[String]("USER.name") map {
            case id ~ text ~ created ~ delay ~ closed ~ userId ~ userName => Question(id, text, created, delay, closed, User(userId, userName) )
        }
    }

    def create(question: TempQuestion): Future[Option[Long]] = Future{
        DB.withConnection { implicit c =>
          SQL(
              """
                  INSERT INTO QUESTION (id_user, text, created, delay, closed)
                    VALUES ( {idUser}, {text}, {created}, {delay}, {closed} )
              """
          ).onParams(
            question.idUser,
            question.text,
            DateTime.now,
            question.delay,
            question.closed
          ).executeInsert(scalar[Long].singleOpt)
      }
    }

    def question(id: Long): Future[Option[Question]] = Future {
      DB.withConnection { implicit c =>
        SQL(
            """
                SELECT QUESTION.*, USER.* FROM QUESTION
                  INNER JOIN USER
                    ON USER.id = QUESTION.id_user
                  WHERE QUESTION.id_user = {id}
            """
        ).onParams(id).as(questionParser*).headOption
      }
    }

    def questions: Future[List[Question]] = Future {
      DB.withConnection { implicit c =>
        SQL(
            """
                SELECT QUESTION.*, USER.* FROM QUESTION
                  INNER JOIN USER
                    ON USER.id = QUESTION.id_user
            """
        ).as(questionParser*)
      }
    }
}