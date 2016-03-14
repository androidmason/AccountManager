package controllers

import play.api.mvc.Controller
import play.api.mvc.Action

class Administration extends Controller{
  
 def getAdminPanel = Action {
    Ok(views.html.adminPanel("Admin Panel"))
  }
  
}