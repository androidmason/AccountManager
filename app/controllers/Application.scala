package controllers

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import models.{ DB, Person }
import play.api.db.Databases
import models.BalanceSheet
import play.api.libs.json.Json
import models.Ledger

class Application extends Controller {

  def index = Action {
    Ok(views.html.index("Hello"))
  }

  def members = Action {
    implicit val jsonFormat = Json.format[BalanceSheet]
    val members = DB.loadData()
    Ok(Json.toJson(members))
  }
  
  def getLedger = Action {
//    implicit val jsonFormat = Json.format[Ledger]
//    val ledger = DB.loadLedger
//    Ok(Json.toJson(ledger))
    Ok(views.html.ledger("Hello",DB.loadLedger))
  }

  def addPerson = Action {
    //DB.executeDDL
    DB.loadData()
    DB.updateBalanceSheet("Monil", 150);
    DB.loadData()
    Redirect(routes.Application.index())
  }
}
