package com.example.newssummary.service.payment;

import java.util.Map;

public class ProductCatalog {

	public static final Map<String, Item> ITEMS = Map.of(
			"COIN_1000", new Item(9900, 1000),
			"COIN_3000", new Item(27000, 3000)
	);
	
	public record Item(long price, long coin) {}
}
