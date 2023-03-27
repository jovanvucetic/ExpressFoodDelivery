package core.models;

import java.util.List;

public class Order {
	
	private List<OrderItem> orderItems;
	
	private DeliveryDetails deliveryDetails;
	
	private PaymentDetails paymentDetails;

	public List<OrderItem> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	public DeliveryDetails getDeliveryDetails() {
		return deliveryDetails;
	}

	public void setDeliveryDetails(DeliveryDetails deliveryDetails) {
		this.deliveryDetails = deliveryDetails;
	}

	public PaymentDetails getPaymentDetails() {
		return paymentDetails;
	}

	public void setPaymentDetails(PaymentDetails paymentDetails) {
		this.paymentDetails = paymentDetails;
	}

	public Order(List<OrderItem> orderItems, DeliveryDetails deliveryDetails, PaymentDetails paymentDetails) {
		super();
		this.orderItems = orderItems;
		this.deliveryDetails = deliveryDetails;
		this.paymentDetails = paymentDetails;
	}
}
