package models

import org.joda.time.DateTime

case class Question(
  id:       Int,
  user:     User,
  text:     String,
  created:  DateTime,
  delay:    Option[Int],
  closed:   Option[DateTime]
)