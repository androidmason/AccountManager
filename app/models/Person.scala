package models

import play.api.libs.json.Json


case class Person(var name: String)

object Person {
  
  implicit val personFormat = Json.format[Person];
  
}