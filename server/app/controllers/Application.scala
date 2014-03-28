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

object Application extends Controller {

  def index () = Action { Ok(views.html.index()) }

  def createUser(name: String) = Action{
    User.create(name) match {
      case Some(id) => Ok( Json.obj("id" -> id) )
      case _        => BadRequest("Failed to insert")
    }
  }

  def users = Action {
    Ok( Json.toJson(User.users) )
  }

  def user(name: String) = Action {
    Ok( Json.toJson(User.user(name)) )
  }

  def newQuestion() = Action {
    Ok(views.html.newquestion())
  }

  def postNewQuestion() = Action {
    Ok("posted question")
  }
}
