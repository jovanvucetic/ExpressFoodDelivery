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
	public AcceptedOrderDetails order(Order arg1) throws InvalidOrderDetailsException, CardAuthorizationException {
		if (arg1 != null)
        {
            if (arg1.getDeliveryDetails() != null)
            {
                if (arg1.getPaymentDetails() != null && (arg1.getPaymentDetails().getPaymentType() != PaymentType.CREDIT_CARD || (arg1.getPaymentDetails().getCardNumber() != null && !arg1.getPaymentDetails().getCardNumber().equals(""))))
                {
                    if (arg1.getOrderItems() != null && arg1.getOrderItems().size() != 0)
                    {
                        for (OrderItem item : arg1.getOrderItems())
                        {
                            if (item == null || item.getCount() <= 0)
                            {
                                throw new InvalidOrderDetailsException("Order item count must be higher than 0");
                            }
                        }

                        var temp = new BigDecimal(0);
                        for (OrderItem item : arg1.getOrderItems())
                        {
                        	BigDecimal price = item.getPrice().multiply(new BigDecimal(item.getCount()));
                            
                            temp = temp.add(price);
                        }

                        //Adding delivery fee
                        temp = temp.add(new BigDecimal(199.99));

                        if (arg1.getPaymentDetails().getPaymentType() == PaymentType.CREDIT_CARD)
                        {
                            if (!paymentRepository.authoriseCreditCard(arg1.getPaymentDetails().toCreditCardPaymentDetails(temp)))
                            {
                                throw new CardAuthorizationException();
                            }
                        }

                        AcceptedKitchenResponse result1 = restaurantRepository.createOrder(arg1);
                        AcceptedDeliveryResponse result2 = deliveryRepository.createDelivery(arg1.getDeliveryDetails());
                       
                        if( arg1.getPaymentDetails().getPaymentType() == PaymentType.CREDIT_CARD ) {
                        	paymentRepository.executePayment(arg1.getPaymentDetails().toCreditCardPaymentDetails(temp));
                        }
                        UUID result3 = orderRepository.createOrder(arg1);
                        
                        Calendar date = Calendar.getInstance();
                        long timeInSecs = date.getTimeInMillis();
                        int numberOfMinutes = result1.getEstimatedPreparationTimeInMinutes() + result2.getEstimatedDeliveryTimeInMinutes();
                        
                        
                        Date deliveryTime = new Date(timeInSecs + (numberOfMinutes * 60 * 1000));
                        //report string builder
                        var sb = new StringBuilder("Order summary: \n");
                        for (var item : arg1.getOrderItems())
                        {
                        	var fullPrice = item.getCount() * item.getPrice().doubleValue();
                            var str = item.getCount() + " x, " + item.getName() + " - " +fullPrice + " din\n";
                            sb.append(str);
                        }
                        sb.append("Delivery price: 199.99M");

                        return new AcceptedOrderDetails(result3, new Date(), deliveryTime, sb.toString());
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
