package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;

public class OrderBook {
    private final TreeMap<Integer, Integer> bids = new TreeMap<>((a, b) -> b - a);
    private final TreeMap<Integer, Integer> asks = new TreeMap<>();

    public void update(String[] fields) {
        int price = Integer.parseInt(fields[1]);
        int size = Integer.parseInt(fields[2]);
        String type = fields[3];
        if (type.equals("bid")) {
            if (size == 0) {
                bids.remove(price);
            } else {
                bids.put(price, size);
            }
        } else if (type.equals("ask")) {
            if (size == 0) {
                asks.remove(price);
            } else {
                asks.put(price, size);
            }
        }
    }

    public void market_order(String[] fields) {
        int size = Integer.parseInt(fields[2]);
        if (fields[1].equals("buy")) {
            execute_market_order(size, asks, true);
        } else if (fields[1].equals("sell")) {
            execute_market_order(size, bids, false);
        }
    }

    public String query(String[] fields) {
        if (fields[1].equals("best_bid")) {
            return bids.isEmpty() ? "0" : bids.firstKey() + "," + bids.get(bids.firstKey());
        } else if (fields[1].equals("best_ask")) {
            return asks.isEmpty() ? "0" : asks.firstKey() + "," + asks.get(asks.firstKey());
        } else if (fields[1].equals("size")) {
            int price = Integer.parseInt(fields[2]);
            if (bids.containsKey(price)) {
                return bids.get(price).toString();
            } else if (asks.containsKey(price)) {
                return asks.get(price).toString();
            } else {
                return "0";
            }
        } else {
            return "0";
        }
    }

    private void execute_market_order(int size, TreeMap<Integer, Integer> book, boolean ascending) {
        while (size > 0 && !book.isEmpty()) {
            int price = ascending ? book.lastKey() : book.firstKey();
            int book_size = book.get(price);
            if (book_size <= size) {
                size -= book_size;
                book.remove(price);
            } else {
                book.put(price, book_size - size);
                size = 0;
            }
        }
    }

    public static void main(String[] args) {
        OrderBook orderBook = new OrderBook();

        try (BufferedReader reader = new BufferedReader(new FileReader("input.txt"));
             FileWriter writer = new FileWriter("output.txt")) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields[0].equals("u")) {
                    orderBook.update(fields);
                } else if (fields[0].equals("o")) {
                    orderBook.market_order(fields);
                } else if (fields[0].equals("q")) {
                    String result = orderBook.query(fields);
                    writer.write(result + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
