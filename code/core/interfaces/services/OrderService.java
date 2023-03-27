package core.interfaces.services;

import core.exceptions.CardAuthorizationException;
import core.exceptions.InvalidOrderDetailsException;
import core.models.AcceptedOrderDetails;
import core.models.Order;

public interface OrderService {
	public AcceptedOrderDetails order(Order orderDetails) throws InvalidOrderDetailsException, CardAuthorizationException;
}
