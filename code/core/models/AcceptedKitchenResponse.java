package core.models;

public class AcceptedKitchenResponse {

	public int estimatedPreparationTimeInMinutes;

	public int getEstimatedPreparationTimeInMinutes() {
		return estimatedPreparationTimeInMinutes;
	}

	public void setEstimatedPreparationTimeInMinutes(int estimatedPreparationTimeInMinutes) {
		this.estimatedPreparationTimeInMinutes = estimatedPreparationTimeInMinutes;
	}

	public AcceptedKitchenResponse(int estimatedPreparationTimeInMinutes) {
		super();
		this.estimatedPreparationTimeInMinutes = estimatedPreparationTimeInMinutes;
	}
}
