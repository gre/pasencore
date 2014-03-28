package controllers

import play.api._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.iteratee._
import scala.concurrent.Future
import scala.util._
import org.joda.time.DateTime

import models._

import play.api.Play.current

object Api extends Controller {
  val filesPath = "../data/files"

  def currentUser(implicit request: Request[_]): Option[User] = {
    val session = request.session
    for {
      id    <- session.get("id")
      name  <- session.get("name")
    } yield ( User(id.toLong, name) )
  }

  def createUser(name: String) = Action.async {
    User.create(name).map {
      case Some(id) => Ok( Json.obj("id" -> id) )
      case _        => BadRequest("Failed to insert")
    }
  }

  def users = Action.async {
    User.users.map { users =>
      Ok( Json.toJson(users) )
    }
  }

  def user(name: String) = Action.async {
    User.user(name).map { n =>
      Ok( Json.toJson(n) )
    }
  }

  def createQuestion = Action.async(parse.json) { request =>
    var user = currentUser(request).get
    Question.create(user.id, request.body.as[TempQuestion] ).map {
      case Some(id) => Ok( Json.obj("id" -> id) )
      case _        => BadRequest("Failed to insert")
    }
  }

  def questions = Action.async {
    Question.questions.map { questions =>
      Ok( Json.toJson(questions) )
    }
  }

  def question(id: Long) = Action.async {
    Question.question(id).map { questions =>
      Ok( Json.toJson(questions) )
    }
  }

  def saveVideo(idQuestion: Long) = Action.async(parse.temporaryFile) { request =>
    var user = currentUser(request).get
    val uuid = java.util.UUID.randomUUID().toString() + ".webm"
    request.body.moveTo(new java.io.File(s"$filesPath/$idQuestion/$uuid"))

    val created = for {
      question <- Question.question(idQuestion)
      idVideo  <- question.map { q: Question =>
        val release = q.closed.getOrElse {
          q.delay.map{ d => DateTime.now.plusMonths(d.toInt)  }.getOrElse(DateTime.now)
        }

        Submission.create(q.id, user.id, uuid, release)
      }.getOrElse( Future.failed( new RuntimeException("Invalid Question") ) )
    } yield idVideo

    created.map {
      case Some(id) => Ok( Json.obj("id" -> id) )
      case _        => BadRequest("Failed to insert")
    }
  }

  def submissions = Action.async {
    Submission.all.map { submissions =>
      Ok( Json.toJson(submissions) )
    }
  }

  def released(idQuestion: Long) = Action.async {
    Submission.released(idQuestion).map { submissions =>
      Ok( Json.toJson(submissions) )
    }
  }

}