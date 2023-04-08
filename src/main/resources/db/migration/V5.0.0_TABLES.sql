REATE PROCEDURE trade_by_quantity ( OUT big_trade_id INTEGER)
LANGUAGE 'plpgsql'
AS $$
BEGIN
SELECT id
INTO big_trade_id
FROM TRADES where quantity = in_qty;
END;
$$;

SELECT id INTO big_trade_id FROM trades where quantity > 10000;
