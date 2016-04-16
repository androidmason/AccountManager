package models

import scalikejdbc._
import java.sql.Date
import java.util.Calendar
import java.sql.Timestamp
import common.Round

object DB {

  // initialize JDBC driver & connection pool
  Class.forName("org.h2.Driver")
  ConnectionPool.singleton("jdbc:h2:~/test", "sa", "")
  ConnectionPool.add('foo, "jdbc:h2:~/test", "sa", "")

  val settings = ConnectionPoolSettings(
    initialSize = 5,
    maxSize = 20,
    connectionTimeoutMillis = 3000L,
    validationQuery = "select 1 from dual")

  implicit val session = AutoSession

  def executeDDL = {
    // table creation, you can run DDL by using #execute as same as JDBC
    sql"""

drop table balance_sheet;

create table balance_sheet (
  id serial not null primary key,
  name varchar2(64) unique,
  balance int,
  pending_rounds varchar2(100),
  updated_at timestamp not null,
  created_at timestamp not null
);

create table ledger(
 id serial not null primary key,
 name varchar2(64),
 contribution int,
 round_key varchar2(64),
 round_details varchar2(254),
 updated_at timestamp not null,
 total_balance int,
);

""".execute.apply()

  }

  def loadData(): List[BalanceSheet] =
    {
      val balanceSheet: List[BalanceSheet] = scalikejdbc.DB readOnly { implicit session =>
        sql"select name,balance from balance_sheet order by name".map(rs => new BalanceSheet(rs.string("name"), rs.int("balance"))).list.apply()
      }
      balanceSheet
    }

  def updateBalanceSheet(name: String, receivedAmount: Int) =
    {
      scalikejdbc.DB localTx { implicit session =>
        sql"update balance_sheet set balance = balance-${receivedAmount}, updated_at=current_timestamp where name = ${name}".update.apply()
      }
    }

  def makeEntryToLedger(ledger: Ledger) = {

    var contribution: Int = ledger.contribution
    if (ledger.roundkey == Round.EXPENSE.toString())
      contribution = ledger.contribution * -1

    val previousBalance: Option[Int] = scalikejdbc.DB readOnly { implicit session =>
      sql"SELECT TOP 1 total_balance FROM ledger ORDER BY id desc".map(rs => rs.int("total_balance")).first.apply()
    }
    val newBalance = contribution + previousBalance.getOrElse(0)

    scalikejdbc.DB localTx { implicit session =>
      sql"insert into ledger(name, contribution, round_key, updated_at, total_balance) values (${ledger.name},${contribution},${ledger.roundkey}, current_timestamp, ${newBalance})".update.apply()
    }
  }

  def addRound(round: String) = {

    var contribution = 200
    if (round.equals("FAREWELL"))
      contribution = 150
    else if (round.equals("WEDDING"))
      contribution = 100

    scalikejdbc.DB localTx { implicit session =>
      sql"update balance_sheet set balance = balance+${contribution}".update.apply()
    }

  }
  
  def getNames: List[String] = {
    val names: List[String] = scalikejdbc.DB readOnly { implicit session =>
        sql"select name from balance_sheet".map(rs => (rs.string("name"))).list.apply()
      }
      names
  }
  
  def loadLedger: List[Ledger] = {
    val ledgers: List[Ledger] = scalikejdbc.DB readOnly { implicit session =>
        sql"select name,contribution,round_key from ledger order by updated_at desc LIMIT 50".map(rs => new Ledger(rs.string("name"), rs.int("contribution"), rs.string("round_key"))).list.apply()
      }
    ledgers
  }
  
  def computeBalance: Int = {
    val balance: Option[Int] = scalikejdbc.DB readOnly { implicit session =>
      sql"SELECT TOP 1 total_balance FROM ledger ORDER BY id desc".map(rs => rs.int("total_balance")).first.apply()
    }
    balance.get
  }

}