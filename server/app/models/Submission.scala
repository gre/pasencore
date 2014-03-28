package models

import org.joda.time.DateTime

case class Submission(
  id:             Long,
  idQuestion:     Long,
  user:           User,
  release:        DateTime,
  fileName:       String,
  comment:        Option[String]
)