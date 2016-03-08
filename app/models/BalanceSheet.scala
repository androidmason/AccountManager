package models

import scalikejdbc._


case class BalanceSheet(var name: String,var balance: Int)

   
object BalanceSheet  extends SQLSyntaxSupport[BalanceSheet]
{
  
  override val tableName = "balance_sheet"

  // If you use NamedDB for this entity, override connectionPoolName
  //override val connectionPoolName = 'anotherdb

}