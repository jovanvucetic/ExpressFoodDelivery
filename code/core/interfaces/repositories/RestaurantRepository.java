package core.interfaces.repositories;

import core.models.AcceptedKitchenResponse;
import core.models.Order;

public interface RestaurantRepository {
	AcceptedKitchenResponse createOrder(Order order);
}
