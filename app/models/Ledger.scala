package models

import common.Members
import common.Round

case class Ledger(var name: String, var contribution: Int, var roundkey:String = Round.EXPENSE.toString())