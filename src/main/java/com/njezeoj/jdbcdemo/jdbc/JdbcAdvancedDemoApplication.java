package com.njezeoj.jdbcdemo.jdbc;

import com.njezeoj.jdbcdemo.domains.Trade;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JdbcAdvancedDemoApplication {
    private NamedParameterJdbcTemplate template;
    private JdbcTemplate jdbcTemplate;

    public JdbcAdvancedDemoApplication(NamedParameterJdbcTemplate template, JdbcTemplate jdbcTemplate) {
        this.template = template;
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final class TradeMapper implements RowMapper<Trade> {

        @Override
        public Trade mapRow(ResultSet rs, int rowNum) throws SQLException {

            System.out.println("rowNum: "+rowNum);

            Trade t = new Trade();
            t.setId(rs.getInt("ID"));
            t.setAccount(rs.getString("ACCOUNT"));
            t.setSecurity(rs.getString("SECURITY"));
            t.setQuantity(rs.getInt("QUANTITY"));
            t.setStatus(rs.getString("STATUS"));
            t.setDirection(rs.getString("DIRECTION"));
            return t;
        }

    }

    //NamedParameterJdbcTemplate

    //Using map
    public void getTradeObject(String s, String a){
        Map bindValues = new HashMap();
        bindValues.put("status", s);
        bindValues.put("account", a);

        Trade tradeObj = template.queryForObject("select * from TRADES where account=:account and status=:status",
                bindValues, BeanPropertyRowMapper.newInstance(Trade.class));

        System.out.println("Advanced Trade obj"+tradeObj);
    }

    public void getTradeCount(String s, String a){
        Map bindValues = new HashMap();
        bindValues.put("status", s);
        bindValues.put("account", a);

        int numOfTrades = template.queryForObject("select count(*) from TRADES where account=:account and status=:status",
                bindValues, Integer.class);

        System.out.println("Advanced Obj trade count"+numOfTrades);
    }

    public void getTradesCountUsingSqlParameterSource(String s, String a){
        SqlParameterSource bindValues =
                new MapSqlParameterSource().addValue("status", s).addValue("account", a);
        int numOfTrades = template.queryForObject ("select count(*) from TRADES where account=:account and status=:status",
                bindValues, Integer.class);

        System.out.println("Advanced Obj trade count SqlParameterSource"+numOfTrades);
    }

    //There is another implementation of the same interface which works on extracting the values from a Java
    // object that complies to JavaBean standards. The BeanPropertySql ParameterSource takes an instance and
    // finds the values of the properties.

    public void getTradesCountUsingBeanSqlParameterSource(Trade t){
        SqlParameterSource bindValues = new BeanPropertySqlParameterSource(t);
        int numOfTrades = template.queryForObject
            ("select count(*) from TRADES where account=:account and status=:status", bindValues, Integer.class);

        System.out.println("Advanced Obj trade count BeanSqlParameterSource"+numOfTrades);
    }

    //using SQLParameterSourceUtils
    public int[] insertTradesList(final List<Trade> trades) {
        SqlParameterSource[] tradesList =
            SqlParameterSourceUtils.createBatch(trades.toArray());
        int[] updatesCount = template.batchUpdate( "insert into TRADES values (:id,:account,:security,:quantity,:status,:direction)",
                tradesList);
        System.out.println("Advanced Obj insertTradesList"+updatesCount);
        return updatesCount; //returns the rows that were inserted into database.
    }

    public int[] insertTrades(final List<Trade> trades) {
        int[] updatesCount = jdbcTemplate.batchUpdate(
                "insert into TRADES values(?,?,?,?,?,?)",
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i)
                            throws SQLException {
                        Trade t = trades.get(i);
                        ps.setInt(1, t.getId());
                        ps.setString(2, t.getAccount());
                        ps.setString(3, t.getSecurity());
                        ps.setInt(4, t.getQuantity());
                        ps.setString(5, t.getStatus());
                        ps.setString(6, t.getDirection());
                    }

                    @Override
                    public int getBatchSize() {
                        System.out.println("batch size");
                        return 20;
                    }
                });

        return updatesCount;
    }

    public int[][] insertTradesInBatches(final List<Trade> trades, int batchSize){
        int[][] updateCount = jdbcTemplate.batchUpdate(
                "insert into TRADES values(?,?,?,?,?,?)",trades, batchSize,new ParameterizedPreparedStatementSetter<Trade>(){
                    @Override
                    public void setValues(PreparedStatement ps, Trade t)
                            throws SQLException {
                        ps.setInt(1, t.getId());
                        ps.setString(2, t.getAccount());
                        ps.setString(3, t.getSecurity());
                        ps.setInt(4, t.getQuantity());
                        ps.setString(5, t.getStatus());
                        ps.setString(6, t.getDirection());
                    }});
        System.out.println("-->"+updateCount);

        return updateCount;
    }



    public List<Trade> createTrades() {
        List<Trade> trades = new ArrayList<Trade>();
        Trade t = null;
        for (int i = 510; i < 600; i++) {
            t = new Trade();
            t.setId(i);
            t.setAccount("ABC" + i);
            t.setSecurity("SEC" + i + "XXX");
            t.setQuantity(i * i * 100);
            t.setDirection("BUY");
            t.setStatus("NEW");
            trades.add(t);
        }
        return trades;
    }

}
