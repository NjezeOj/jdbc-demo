package com.njezeoj.jdbcdemo.jdbc;

import com.njezeoj.jdbcdemo.domains.Trade;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SimpleJdbc{
    private SimpleJdbcInsert jdbcInsert;
    private SimpleJdbcCall jdbcCall;

    public SimpleJdbc(SimpleJdbcInsert jdbcInsert, SimpleJdbcCall jdbcCall) {
        this.jdbcInsert = jdbcInsert.withTableName("TRADES");
        this.jdbcCall = jdbcCall.withProcedureName("trade_by_quantity");

        //Should you wish to insert only one or two columns, you
        // can set the column names using the usingColumns() method
        // as shown below:
        // this.jdbcInsert = jdbcInsert.withTableName("TRADES")
    }

    public void insertTrade(Trade t) {
        Map tradeMap = new HashMap();
        tradeMap.put("id", t.getId());
        tradeMap.put("account", t.getAccount());
        tradeMap.put("security", t.getSecurity());
        tradeMap.put("quantity", t.getQuantity());
        // tradeMap.put("status", t.getStatus());
        // tradeMap.put("direction", t.getDirection());

        jdbcInsert.execute(tradeMap);
    }

//    To simplify things further, instead of using Map to set the values,
//    you can use one of the SqlParameterSource implementations.
//    The BeanPropertySqlParameterSource will work on the Trade object to extract the values
//    and pass it on to the PreparedStatement.
//    We have seen an example of this class’s usage earlier in the chapter.

    public void insertTradeUsingSqlParameterSource(Trade t) {
        SqlParameterSource source = new BeanPropertySqlParameterSource(t);

        jdbcInsert.execute(source);
    }

    public Trade createDummyTrade() {
        Trade t = new Trade();
        t.setId(232);
        t.setAccount("ABCDUMMY");
        t.setSecurity("SECDUMMY");
        t.setQuantity(100);
        t.setDirection("BUY");
        t.setStatus("NEW");

        return t;
    }

    public Trade getBigTradeUsingSimpleJdbcCall(String quantity) {
        SqlParameterSource inValues = new MapSqlParameterSource().addValue(
                "quantity", quantity);

        Map bigTrades = jdbcCall.execute(inValues);

        Trade t = new Trade();
        t.setId((Integer) bigTrades.get("id"));

        return t;

    }

    //You can also use SimpleJdbcCall to invoke a StoredProc that returns a ResultSet.
    public List getBigTradesUsingSimpleJdbcCall(int quantity) {
        SqlParameterSource inValues = new MapSqlParameterSource().addValue(
                "quantity", quantity);
        jdbcCall = jdbcCall.withProcedureName(
                "big_trades").returningResultSet("trades",
                new BeanPropertyRowMapper());

        Map bigTrades = jdbcCall.execute();

        return (List)bigTrades.get("trades");
    }




}
