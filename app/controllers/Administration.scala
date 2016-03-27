package controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import models.BalanceSheet
import play.api.data.Form
import play.api.data.Forms._
import models.DB
import models.Ledger

class Administration extends Controller {

  def getAdminPanel =
    Action { request =>
      request.session.get("authourization").map { status =>
        Ok(views.html.adminPanel(DB.getNames))
      }.getOrElse {
        Unauthorized("Oops, you are not connected")
      }
    }

  val collectPaymentInfo: Form[BalanceSheet] = Form {
    mapping("name" -> text, "balance" -> number)(BalanceSheet.apply)(BalanceSheet.unapply)
  }

  def collectPayment = Action {
    implicit request =>
      request.session.get("authourization").map { status =>
        val balanceSheet = collectPaymentInfo.bindFromRequest.get
        DB.updateBalanceSheet(balanceSheet.name, balanceSheet.balance)
        DB.makeEntryToLedger(new Ledger(balanceSheet.name, balanceSheet.balance, "PAYMENT"))
        Redirect(routes.Administration.getAdminPanel())
      }.getOrElse(Redirect(routes.Authentication.getLogin()))

  }

  val addRoundInfo = Form(
    "round" -> text)

  def addRound = Action {
    implicit request =>
      request.session.get("authourization").map { status =>
        val round = addRoundInfo.bindFromRequest.get
        DB.addRound(round.toUpperCase())
        Redirect(routes.Administration.getAdminPanel())
      }.getOrElse(Redirect(routes.Authentication.getLogin()))
  }

}