package controllers

import play.api._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.iteratee._
import scala.concurrent.Future
import scala.util._

import models._

import play.api.Play.current

object Api extends Controller {
  val filesPath = "../data/files"

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
    Question.create( request.body.as[TempQuestion] ).map {
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

def saveVideo(id: Long) = Action(parse.temporaryFile) { request =>
    val uuid = java.util.UUID.randomUUID().toString() + ".webm"
    request.body.moveTo(new java.io.File(s"$filesPath/$id/$uuid"))

    Ok(uuid)
  }

}