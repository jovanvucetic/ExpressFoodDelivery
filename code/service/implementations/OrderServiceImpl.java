package service.implementations;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import core.exceptions.CardAuthorizationException;
import core.exceptions.InvalidOrderDetailsException;
import core.interfaces.repositories.DeliveryRepository;
import core.interfaces.repositories.OrderRepository;
import core.interfaces.repositories.PaymentRepository;
import core.interfaces.repositories.RestaurantRepository;
import core.interfaces.services.OrderService;
import core.models.*;

public class OrderServiceImpl implements OrderService {

    private DeliveryRepository deliveryRepository;
    private PaymentRepository paymentRepository;
    private RestaurantRepository restaurantRepository;
    private OrderRepository orderRepository;
	
	public OrderServiceImpl(DeliveryRepository deliveryRepository, PaymentRepository paymentRepository,
			RestaurantRepository restaurantRepository, OrderRepository orderRepository) {
		this.deliveryRepository = deliveryRepository;
		this.paymentRepository = paymentRepository;
		this.restaurantRepository = restaurantRepository;
		this.orderRepository = orderRepository;
	}

	@Override
	public AcceptedOrderDetails order(Order orderDetails) throws InvalidOrderDetailsException, CardAuthorizationException {
		if (orderDetails != null)
        {
            if (orderDetails.getDeliveryDetails() != null)
            {
                if (orderDetails.getPaymentDetails() != null && (orderDetails.getPaymentDetails().getPaymentType() != PaymentType.CREDIT_CARD || (orderDetails.getPaymentDetails().getCardNumber() != null && !orderDetails.getPaymentDetails().getCardNumber().equals(""))))
                {
                    if (orderDetails.getOrderItems() != null && orderDetails.getOrderItems().size() != 0)
                    {
                        for (OrderItem orderItem : orderDetails.getOrderItems())
                        {
                            if (orderItem == null || orderItem.getCount() <= 0)
                            {
                                throw new InvalidOrderDetailsException("Order item count must be higher than 0");
                            }
                        }

                        var fullOrderPrice = new BigDecimal(0);
                        for (OrderItem orderItem : orderDetails.getOrderItems())
                        {
                        	BigDecimal orderItemPrice = orderItem.getPrice().multiply(new BigDecimal(orderItem.getCount()));
                            
                            fullOrderPrice = fullOrderPrice.add(orderItemPrice);
                        }

                        //Adding delivery fee
                        fullOrderPrice = fullOrderPrice.add(new BigDecimal(199.99));

                        if (orderDetails.getPaymentDetails().getPaymentType() == PaymentType.CREDIT_CARD)
                        {
                            if (!paymentRepository.authoriseCreditCard(orderDetails.getPaymentDetails().toCreditCardPaymentDetails(fullOrderPrice)))
                            {
                                throw new CardAuthorizationException();
                            }
                        }

                        AcceptedKitchenResponse kitchenResponse = restaurantRepository.createOrder(orderDetails);
                        AcceptedDeliveryResponse deliveryServiceResponse = deliveryRepository.createDelivery(orderDetails.getDeliveryDetails());
                       
                        if( orderDetails.getPaymentDetails().getPaymentType() == PaymentType.CREDIT_CARD ) {
                        	paymentRepository.executePayment(orderDetails.getPaymentDetails().toCreditCardPaymentDetails(fullOrderPrice));
                        }
                        UUID orderId = orderRepository.createOrder(orderDetails);
                        
                        int estimatedDeliveryTimeInMinutes = kitchenResponse.getEstimatedPreparationTimeInMinutes() + deliveryServiceResponse.getEstimatedDeliveryTimeInMinutes();   
                        
                        Date orderExpectedOn = new Date(Calendar.getInstance().getTimeInMillis() + (estimatedDeliveryTimeInMinutes * 60 * 1000));
                        //report string builder
                        var reportStringBuilder = new StringBuilder("Order summary: \n");
                        for (var orderItem : orderDetails.getOrderItems())
                        {
                        	var fullPrice = orderItem.getCount() * orderItem.getPrice().doubleValue();
                            var orderReportItem = orderItem.getCount() + " x, " + orderItem.getName() + " - " +fullPrice + " din\n";
                            reportStringBuilder.append(orderReportItem);
                        }
                        reportStringBuilder.append("Delivery price: 199.99M");

                        Date orderAcceptedOn = new Date();
                        return new AcceptedOrderDetails(orderId, orderAcceptedOn, orderExpectedOn, reportStringBuilder.toString());
                    }
                    else
                    {
                        throw new InvalidOrderDetailsException("Order items must be defined");
                    }
                }
                else
                {
                    throw new InvalidOrderDetailsException("Payment details are not valid");
                }
            }
            else
            {
                throw new InvalidOrderDetailsException("Delivery details object cannot be null");
            }
        }
        else
        {
            throw new InvalidOrderDetailsException("Order details object cannot be null");
        }
    }
}
