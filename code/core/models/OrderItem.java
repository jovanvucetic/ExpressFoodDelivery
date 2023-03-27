package core.models;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderItem {
	
	private UUID id;
	
	private String name;
	
	private int count;
	
	private BigDecimal price;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public OrderItem(UUID id, String name, int count, BigDecimal price) {
		super();
		this.id = id;
		this.name = name;
		this.count = count;
		this.price = price;
	}	
}
