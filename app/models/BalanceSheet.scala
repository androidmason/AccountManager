package models

import scalikejdbc._
import play.api.libs.json.Json


case class BalanceSheet(var name: String,var balance: Int)

   
object BalanceSheet  extends SQLSyntaxSupport[BalanceSheet]
{
  
  implicit val balanceSheetFormat = Json.format[BalanceSheet];
  override val tableName = "balance_sheet"
 

}