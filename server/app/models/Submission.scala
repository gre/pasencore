package models

import org.joda.time.DateTime
import play.api.libs.json._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class Submission(
  id:             Long,
  idQuestion:     Long,
  release:        DateTime,
  fileName:       String,
  comment:        Option[String],
  user:           User
)


object Submission {
    import anorm._
    import anorm.SqlParser._
    import utils.AnormExtension._

    import play.api.Play.current
    import play.api.db._

    implicit val format = Json.format[Submission]

    val submissionParser = {
        get[Long]("SUBMISSION.id") ~
        get[Long]("SUBMISSION.id_question") ~
        get[DateTime]("SUBMISSION.release") ~
        get[String]("SUBMISSION.file_name") ~
        get[Option[String]]("SUBMISSION.comment") ~
        get[Long]("USER.id") ~
        get[String]("USER.name") map {
            case id ~ idQuestion ~ date ~ fileName ~ comment ~ userId ~ userName => Submission(id, idQuestion, date, fileName, comment, User(userId, userName) )
        }
    }


    def create(idQuestion: Long, idUser: Long, fileName: String, release: DateTime): Future[Option[Long]] = Future{
        DB.withConnection { implicit c =>
          SQL(
              """
                  INSERT INTO Submission (id_question, id_user, file_name, release)
                    VALUES ( {idQuestion}, {idUser}, {fileName}, {release} )
              """
          ).onParams(idQuestion, idUser, fileName, release).executeInsert(scalar[Long].singleOpt)
      }
    }

    def all = Future {
        DB.withConnection { implicit c =>
          SQL(
              """
                  SELECT SUBMISSION.*, USER.* FROM SUBMISSION
                    INNER JOIN USER
                      ON SUBMISSION.id_user = USER.id
              """
          ).as(submissionParser*)
      }
    }

    def released(idQuestion: Long) = Future {
        DB.withConnection { implicit c =>
          SQL(
              """
                  SELECT SUBMISSION.*, USER.* FROM SUBMISSION
                    INNER JOIN USER
                      ON SUBMISSION.id_user = USER.id
                    WHERE SUBMISSION.id_question = {id_question}
                      AND SUBMISSION.release < {dateNow}
              """
          ).onParams(idQuestion, DateTime.now).as(submissionParser*)
      }
    }
}