package controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import models.Person
import play.api.data.Form
import play.api.data.Forms._

class Authentication extends Controller{
  
 def getLogin = Action {
    Ok(views.html.login("Hello"))
  }
 
 val loginForm: Form[Person] = Form {
    mapping(
      "pass" -> text)(Person.apply)(Person.unapply)
  }
  
 def authenticate = Action { implicit request =>
   val person = loginForm.bindFromRequest.get
   if (person.pass.equals("committee@321"))
      Redirect(routes.Administration.getAdminPanel()).withSession(request.session + ("authourization" -> "success") )
    else
     Redirect(routes.Authentication.getLogin())
 }

}