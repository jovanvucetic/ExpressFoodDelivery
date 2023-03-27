package core.interfaces.repositories;

import core.models.AcceptedDeliveryResponse;
import core.models.DeliveryDetails;

public interface DeliveryRepository {
	public AcceptedDeliveryResponse createDelivery(DeliveryDetails deliveryDetails);
}
