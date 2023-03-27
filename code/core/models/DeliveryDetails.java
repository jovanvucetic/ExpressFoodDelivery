package core.models;

import java.util.Date;

public class DeliveryDetails {

	private Address pickUpAddress;
	
	private Address deliveryAddress;
	
	private Date orderTime;

	public DeliveryDetails(Address pickUpAddress, Address deliveryAddress) {
		this.pickUpAddress = pickUpAddress;
		this.deliveryAddress = deliveryAddress;
	}

	public Address getPickUpAddress() {
		return pickUpAddress;
	}

	public void setPickUpAddress(Address pickUpAddress) {
		this.pickUpAddress = pickUpAddress;
	}

	public Address getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(Address deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}
	
	public Date getOrderDate() {
		return orderTime;
	}
	
	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}
}
