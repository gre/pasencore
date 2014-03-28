package controllers

import play.api._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.iteratee._
import scala.concurrent.Future
import scala.util._

import play.api.Play.current

import models.Question

object Application extends Controller {

  def index () = Action.async { 
    Question.questions.map { list =>
        Ok(views.html.index(list)) 
    }
  }

  def newQuestion() = Action {
    Ok(views.html.newquestion())
  }

  def postNewQuestion() = Action {
    Ok("posted question")
  }
}
