package controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import models.BalanceSheet
import play.api.data.Form
import play.api.data.Forms._
import models.DB
import models.Ledger
import common.Round

class Administration extends Controller {

  def getAdminPanel(message: String) =
    Action { request =>
      request.session.get("authourization").map { status =>
        Ok(views.html.adminPanel(DB.getNames,message,DB.computeBalance))
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
        Redirect(routes.Administration.getAdminPanel("Payment collection registered successfully"))
      }.getOrElse(Redirect(routes.Authentication.getLogin()))
  }

  val addRoundInfo = Form(
    "round" -> text)

  def addRound = Action {
    implicit request =>
      request.session.get("authourization").map { status =>
        val round = addRoundInfo.bindFromRequest.get
        DB.addRound(round.toUpperCase())
        Redirect(routes.Administration.getAdminPanel("New Round added"))
      }.getOrElse(Redirect(routes.Authentication.getLogin()))
  }
  
  val addExpenseInfo: Form[Ledger] = Form(
      mapping("name" -> text, "contribution" -> number, "round" -> text)(Ledger.apply)(Ledger.unapply)
  )
  
  def addExpense = Action {
    implicit request => 
      request.session.get("authourization").map { status =>
        val ledger = addExpenseInfo.bindFromRequest.get
        DB.makeEntryToLedger(ledger)
        Redirect(routes.Administration.getAdminPanel("Expense added"))
      }.getOrElse(Redirect(routes.Authentication.getLogin()))
  }
}