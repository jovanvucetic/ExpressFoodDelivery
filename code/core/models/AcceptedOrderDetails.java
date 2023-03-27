package core.models;

import java.util.Date;
import java.util.UUID;

public class AcceptedOrderDetails {
	
	private UUID orderId;

	private Date orderAcceptedOn;

    private Date deliveryExpectedOn;

	private String orderedItemsSummary;

	public UUID getOrderId() {
		return orderId;
	}

	public void setOrderId(UUID orderId) {
		this.orderId = orderId;
	}

	public Date getOrderAcceptedOn() {
		return orderAcceptedOn;
	}

	public void setOrderAcceptedOn(Date orderAcceptedOn) {
		this.orderAcceptedOn = orderAcceptedOn;
	}

	public Date getDeliveryExpectedOn() {
		return deliveryExpectedOn;
	}

	public void setDeliveryExpectedOn(Date deliveryExpectedOn) {
		this.deliveryExpectedOn = deliveryExpectedOn;
	}

	public String getOrderedItemsSummary() {
		return orderedItemsSummary;
	}

	public void setOrderedItemsSummary(String orderedItemsSummary) {
		this.orderedItemsSummary = orderedItemsSummary;
	}

	public AcceptedOrderDetails(UUID orderId, Date orderAcceptedOn, Date deliveryExpectedOn,
			String orderedItemsSummary) {
		super();
		this.orderId = orderId;
		this.orderAcceptedOn = orderAcceptedOn;
		this.deliveryExpectedOn = deliveryExpectedOn;
		this.orderedItemsSummary = orderedItemsSummary;
	}
}
