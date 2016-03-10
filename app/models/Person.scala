package models

import play.api.libs.json.Json


case class Person(var pass: String)

object Person {
  
  implicit val personFormat = Json.format[Person];
  
}