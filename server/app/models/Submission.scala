package models

import org.joda.time.DateTime

case class Submission(
  id:             Int,
  idQuestion:     Int,
  user:           User,
  release:        DateTime,
  fileName:       String,
  comment:        Option[String]
)