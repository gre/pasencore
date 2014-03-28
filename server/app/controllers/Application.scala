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

import play.api.data._
import play.api.data.Forms._

import models._

object Application extends Controller {

  def user(implicit request: Request[_]): Option[User] = {
    val session = request.session
    for {
      id    <- session.get("id")
      name  <- session.get("name")
    } yield ( User(id.toLong, name) )
  }


  def index () = Action.async {
    Question.questions.map { list =>
        Ok(views.html.index(list))
    }
  }

  def loginPage () = Action { Ok(views.html.login()) }

  def newQuestion() = Action {
    Ok(views.html.newquestion())
  }

  def postNewQuestion() = Action {
    Ok("posted question")
  }

  def login () = Action.async { implicit request =>
    val name = Form("name" -> text ).bindFromRequest.get

    User.user(name).flatMap{
      case Some(u) => Future(Some(u))
      case _       => User.create(name).map( id =>
        id.map { i => User(i, name) }
      )
    }.map {
      case Some(u) => {
        val session = request.session + ("id" -> u.id.toString) + ("name" -> u.name)
        Redirect(routes.Application.index).withSession(session)
      }
      case _      =>  Redirect(routes.Application.loginPage)
    }
  }
}
