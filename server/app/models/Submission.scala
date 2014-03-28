package models

import org.joda.time.DateTime
import play.api.libs.json._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class Submission(
  id:             Long,
  idQuestion:     Long,
  user:           User,
  release:        DateTime,
  fileName:       String,
  comment:        Option[String]
)