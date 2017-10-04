package pl.training.groovy.bank.accounts.repository

import groovy.sql.Sql
import pl.training.groovy.bank.accounts.Account

import javax.sql.DataSource
import java.util.concurrent.atomic.AtomicLong

/**
 * Created by PMUZYKA on 2017-10-04.
 */
class PostgresAccountsRepository implements AccountsRepository{

    // it is a good practice to keep your SQLs inside a separate file - in order not to recompile all of the project when
    private static final String INSERT_ACCOUNT = "insert into accounts (number,balance) values(:number,:balance)"
    private static final String UPDATE_ACCOUNT_BALANCE = "update accounts set balance = :balance where id = :id"
    private static final String SELECT_ACCOUNT_BY_NUMBER = "select * from accounts where number = :number" // we will insert a parameter for a :number

    private Sql sql

    PostgresAccountsRepository(DataSource dataSource){
        sql = new Sql(dataSource) // we are referencing a method
    }

    Account save(Account account) {
        def keys = sql.executeInsert(INSERT_ACCOUNT, [number: account.number, balance: account.balance])
        account.id = keys[0][0] as Long
        return account
    }

    void update(Account account) {
        Integer updatedRecords = sql.executeUpdate(UPDATE_ACCOUNT_BALANCE, [id: account.id, balance: account.balance])
        if (!updatedRecords) {
            throw new AccountNotFoundException()
        }
    }

    Account getByNumber(String number) {
        Account account
        sql.eachRow(SELECT_ACCOUNT_BY_NUMBER, ['number': number]) {
            account = new Account(id: it.id, number: it.number, balance: it.balance)
        }
        if (!account) {
            throw new AccountNotFoundException()
        }
        return account
    }

}
