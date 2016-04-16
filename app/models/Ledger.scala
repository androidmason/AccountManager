package models

import common.Members
import common.Round
import java.sql.Timestamp

case class Ledger(var name: String, var contribution: Int, var roundkey:String)