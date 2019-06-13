CREATE TABLE IF NOT EXISTS products
(
  prod_id     varchar PRIMARY KEY,
  description varchar,
  in_kg       boolean NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS storage
(
  prod_id                    varchar,
  amount                     numeric NOT NULL DEFAULT 0,
  last_sell_cost_per_measure money   NOT NULL DEFAULT 0, -- per measure
  last_buy_cost_per_measure  money   NOT NULL DEFAULT 0, -- per measure
  FOREIGN KEY (prod_id) REFERENCES products (prod_id) DEFERRABLE
);

CREATE TABLE IF NOT EXISTS transactions
(
  transaction_id    timestamp PRIMARY KEY DEFAULT CURRENT_TIMESTAMP,
  prod_id           varchar,
  amount            numeric NOT NULL,
  price_per_measure money   NOT NULL,
  FOREIGN KEY (prod_id) REFERENCES products (prod_id) DEFERRABLE
);

-- Trigger updating the storage
CREATE OR REPLACE FUNCTION update_storage()
  RETURNS TRIGGER AS
$BODY$
BEGIN
  IF NEW.prod_id NOT IN (SELECT prod_id FROM products) THEN -- when product doesn't exist in catalog
    RAISE EXCEPTION '% is not a product in catalog', NEW.prod_id;
    -- selling products
  ELSEIF NEW.price_per_measure >= 0::money THEN -- if price = 0 -> giveaway
    IF (NEW.prod_id NOT IN (SELECT prod_id FROM storage) OR -- trying to sell missing product
        NEW.amount > (SELECT amount FROM storage WHERE storage.prod_id = NEW.prod_id)) THEN
      RAISE EXCEPTION 'quantity of % in storage is too small', NEW.prod_id;
    ELSE -- selling successful
      UPDATE storage
      SET amount                     = amount - NEW.amount,
          last_sell_cost_per_measure = NEW.price_per_measure
      WHERE prod_id = NEW.prod_id;
    END IF;
    -- buying products
  ELSEIF NEW.price_per_measure < 0::money THEN
    IF NEW.prod_id NOT IN (SELECT prod_id FROM storage) THEN -- create item in storage
      INSERT INTO storage(prod_id, amount, last_buy_cost_per_measure)
      VALUES (NEW.prod_id, NEW.amount, 0::money - NEW.price_per_measure);
    ELSE -- add to existing storage
      UPDATE storage
      SET amount                    = amount + NEW.amount,
          last_buy_cost_per_measure = 0::money - NEW.price_per_measure
      WHERE prod_id = NEW.prod_id;
    END IF; -- update on buy
  END IF; -- update storage
  RETURN NEW;
END;
$BODY$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS on_transaction_add_trig ON transactions;
CREATE TRIGGER on_transaction_add_trig
  BEFORE INSERT
  ON transactions
  FOR EACH ROW
EXECUTE PROCEDURE update_storage();

CREATE OR REPLACE FUNCTION withdraw_last_transaction() RETURNS VOID AS
$BODY$
BEGIN
  WITH last_trans AS (SELECT * FROM transactions WHERE transaction_id >= ALL (SELECT transaction_id FROM transactions))
  UPDATE storage

  SET amount = (CASE
                  WHEN last_trans.price_per_measure >= 0 THEN amount + last_trans.amount
                  ELSE amount - last_trans.amount END)
  WHERE prod_id = last_trans.prod_id;

  DELETE FROM transactions WHERE transaction_id = MAX(transaction_id);
END;
$BODY$ LANGUAGE plpgsql;