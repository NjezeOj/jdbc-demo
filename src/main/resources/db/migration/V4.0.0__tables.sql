CREATE PROCEDURE big_trades (in_qty IN INTEGER)
LANGUAGE 'plpgsql'
AS $$
BEGIN
SELECT * FROM trades WHERE quantity > in_qty;
END;
$$;


