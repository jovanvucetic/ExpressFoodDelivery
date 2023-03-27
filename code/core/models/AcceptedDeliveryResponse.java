package core.models;

public class AcceptedDeliveryResponse {
	
	public int estimatedDeliveryTimeInMinutes;

	public int getEstimatedDeliveryTimeInMinutes() {
		return estimatedDeliveryTimeInMinutes;
	}

	public void setEstimatedDeliveryTimeInMinutes(int estimatedDeliveryTimeInMinutes) {
		this.estimatedDeliveryTimeInMinutes = estimatedDeliveryTimeInMinutes;
	}

	public AcceptedDeliveryResponse(int estimatedDeliveryTimeInMinutes) {
		super();
		this.estimatedDeliveryTimeInMinutes = estimatedDeliveryTimeInMinutes;
	}
}
