package com.njezeoj.jdbcdemo;

import com.njezeoj.jdbcdemo.jdbc.JdbcAdvancedDemoApplication;
import com.njezeoj.jdbcdemo.jdbc.JdbcTemplateTest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JdbcDemoApplication {

	private static JdbcTemplateTest t;
	private static JdbcAdvancedDemoApplication adv;

	public JdbcDemoApplication(JdbcTemplateTest t, JdbcAdvancedDemoApplication adv) {
		this.t = t;
		this.adv = adv;
	}

	public static void main(String[] args) {
		SpringApplication.run(JdbcDemoApplication.class, args);

//		int tradeCount = t.getTradesCount();
//		System.out.println(tradeCount);

		//t.getTradeAsMap();

		//t.getTradeStatus(1, "MDMD");

		//CHAPTER 2
//		adv.getTradeObject("NEW", "ABC444");
//		adv.getTradeCount("NEW", "ABC444");

//		Trade trade = new Trade();
//		trade.setAccount("1234AAA");
//		trade.setId(1234);
//		trade.setStatus("NEW");

		//adv.getTradesCountUsingBeanSqlParameterSource(trade);

		//adv.insertTradesList(adv.createTrades());

//		int[] batchPreparedInsertTrades = adv.insertTrades(adv.createTrades());
//		System.out.println(batchPreparedInsertTrades);
		int sum = 0;
		int[][] parameterizedPreparedStatementSetterInsertTrades = adv.insertTradesInBatches(adv.createTrades(),10);
		System.out.println(parameterizedPreparedStatementSetterInsertTrades.length);
		for(int i = 0; i < parameterizedPreparedStatementSetterInsertTrades.length; i ++){
			for(int j = 0; j < parameterizedPreparedStatementSetterInsertTrades.length; j ++){
				sum += 1;
				System.out.println("Element: "+i+","+j+","+parameterizedPreparedStatementSetterInsertTrades[i][j]);
				//The returned two dimensional integer array indicates the number of batches against updates in each batch.
			}
		}
	}

}
