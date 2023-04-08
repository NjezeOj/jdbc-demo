package com.njezeoj.jdbcdemo.jdbc;

import com.njezeoj.jdbcdemo.domains.Trade;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
public class JdbcTemplateTest {
    private JdbcTemplate template;

    public JdbcTemplateTest(JdbcTemplate template){
        this.template = template;
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


    public int getTradesCount(){
        int numOfTrades = template.queryForObject("select count(*) from TRADES", Integer.class);
        return numOfTrades;
    }

    public Map<String,Object> getTradeAsMap(){
        Map<String, Object> tradeAsMap = template.queryForMap("select * from TRADES where id=1");
        System.out.println("Trades Map:"+tradeAsMap);
        return tradeAsMap; }

    public String getTradeStatus(int id,  String security){
        String sql = "select STATUS from TRADES where id = ? and security=?";
        String status =
                template.queryForObject(sql, String.class, new Object[]{id, security});
        System.out.println(status);

        return status;
    }

    public Trade getMappedTrade(int id){
        Trade trade = template.queryForObject("select * from TRADES where id = ?", new Object[]{id} ,
                new TradeMapper());
        return trade;
    }

    public List<Trade> getAllMappedTrades(){
        List<Trade> trades =
            template.query("select * from TRADES", new TradeMapper());
        return trades;
    }

    private void insertTrade() {
        int rowsUpdated =
                template.update("insert into TRADES values(?,?,?,?,?,?)",
                        61,"JSDATA","REV",500000,"NEW","SELL");
        System.out.println("Rows Updated:"+rowsUpdated);
    }

    private void updateTrade(String status, int id) {
        int rowsUpdated =
            template.update("update TRADES set status=? where id=?",status, id);
        System.out.println("Rows Updated:"+rowsUpdated);
    }

    //However, see the updated snippet below where using SQL
    // types comes necessary—we pass in all the arguments as String objects.
    private void updateTradeUsingTypes() {
        int rowsUpdated = template.update(
                "update TRADES set status=? where id=?",
                new Object[] { "UNKNOWN","6" },
                new int[] { java.sql.Types.VARCHAR,java.sql.Types.INTEGER });
        System.out.println("Rows Updated:" + rowsUpdated);
    }

    private void replayTradesUsingSP(List tradeIds) {
        template.update(
                "call JSDATA.REPLAY_TRADES_SP (?)", tradeIds);
    }

    //The JdbcTemplate exposes execute methods so you can run Data Definition Language (DDL) statements easily:
    public void createAndDropPersonTable(){
        template.execute("create table PERSON (FIRST_NAME varchar(50) not null, LAST_NAME varchar(50) not null)");
        // drop the table
        template.execute("drop table PERSON");
        System.out.println("Table dropped");
    }
}
