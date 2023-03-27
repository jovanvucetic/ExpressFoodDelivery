package core.interfaces.repositories;

import java.util.UUID;
import core.models.Order;

public interface OrderRepository {
	public UUID createOrder(Order order);
}
