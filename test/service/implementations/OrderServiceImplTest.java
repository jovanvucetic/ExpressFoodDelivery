package service.implementations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import core.exceptions.*;
import core.interfaces.repositories.*;
import core.models.*;

@RunWith(Suite.class)
@SuiteClasses({})
public class OrderServiceImplTest {
	
    private DeliveryRepository deliveryRepositoryMock;
    private PaymentRepository paymentRepositoryMock;
    private RestaurantRepository restaurantRepositoryMock;
    private OrderRepository orderRepositoryMock;
    
    private OrderServiceImpl orderService;
    
    @BeforeEach
    public void setUp() {
    	deliveryRepositoryMock = mock(DeliveryRepository.class);
    	paymentRepositoryMock = mock(PaymentRepository.class);
    	restaurantRepositoryMock = mock(RestaurantRepository.class);
    	orderRepositoryMock = mock(OrderRepository.class);

    	orderService = new OrderServiceImpl(
    			deliveryRepositoryMock, 
    			paymentRepositoryMock, 
    			restaurantRepositoryMock, 
    			orderRepositoryMock
        );
    }
	
	@Test
	public void order_whenCreditCardAuthorisationFails_thenCardAuthorizationExceptionThrown() {
		Order orderDetails = getMockedOrderDetails();
		 when(paymentRepositoryMock.authoriseCreditCard(any(CreditCardPaymentDetails.class))).thenReturn(false);	          

		 CardAuthorizationException thrownException = assertThrows(
				 CardAuthorizationException.class,
 	           () -> orderService.order(orderDetails),
 	           "Expected order() to throw CardAuthorizationException, but it didn't"
 	    );
 
		 assertTrue(thrownException != null); 
	}
	
	@Test
	public void order_DeliveryDetailsNotProvided_InvalidOrderDetailsExceptionThrown() {
		InvalidOrderDetailsException thrownException = assertThrows(
				InvalidOrderDetailsException.class,
 	           () -> orderService.order(null),
 	           "Expected order() to throw InvalidOrderDetailsException, but it didn't"
 	    );
 
		 assertTrue(thrownException != null); 
	}
	
	@Test
	public void order_PaymentDetailsNotProvided_InvalidOrderDetailsExceptionThrown() {
		Order orderDetails = getMockedOrderDetails();
		orderDetails.setPaymentDetails(null);
		
		InvalidOrderDetailsException thrownException = assertThrows(
				InvalidOrderDetailsException.class,
 	           () -> orderService.order(orderDetails),
 	           "Expected order() to throw InvalidOrderDetailsException, but it didn't"
 	    );
 
		 assertTrue(thrownException != null); 
	}
	
	@Test
	public void order_PaymentDetailsInvalid_InvalidOrderDetailsExceptionThrown() {
		PaymentDetails paymentDetails = new PaymentDetails(PaymentType.CREDIT_CARD, null);
		Order orderDetails = getMockedOrderDetails();
		orderDetails.setPaymentDetails(paymentDetails);
		
		InvalidOrderDetailsException thrownException = assertThrows(
				InvalidOrderDetailsException.class,
 	           () -> orderService.order(orderDetails),
 	           "Expected order() to throw InvalidOrderDetailsException, but it didn't"
 	    );
 
		 assertTrue(thrownException != null); 
	}
	
	@Test
	public void order_OrderItemsNotProvided_InvalidOrderDetailsExceptionThrown() {
		Order orderDetails = getMockedOrderDetails();
		orderDetails.setOrderItems(null);
		
		InvalidOrderDetailsException thrownException = assertThrows(
				InvalidOrderDetailsException.class,
 	           () -> orderService.order(orderDetails),
 	           "Expected order() to throw InvalidOrderDetailsException, but it didn't"
 	    );
 
		 assertTrue(thrownException != null); 
	}
	
	@Test
	public void order_OrderItemsCollectionIsEmpty_InvalidOrderDetailsExceptionThrown() {
		Order orderDetails = getMockedOrderDetails();
		orderDetails.setOrderItems(new ArrayList<OrderItem>());
		
		InvalidOrderDetailsException thrownException = assertThrows(
				InvalidOrderDetailsException.class,
 	           () -> orderService.order(orderDetails),
 	           "Expected order() to throw InvalidOrderDetailsException, but it didn't"
 	    );
 
		 assertTrue(thrownException != null); 		
	}
	
	@Test
	public void order_OrderItemCollectionContainsNull_InvalidOrderDetailsExceptionThrown() {
		Order orderDetails = getMockedOrderDetails();
		orderDetails.getOrderItems().add(null);
		
		InvalidOrderDetailsException thrownException = assertThrows(
				InvalidOrderDetailsException.class,
 	           () -> orderService.order(orderDetails),
 	           "Expected order() to throw InvalidOrderDetailsException, but it didn't"
 	    );
 
		 assertTrue(thrownException != null); 		
	}
	
	@Test
	public void order_OrderItemHasZeroCount_InvalidOrderDetailsExceptionThrown() {
		Order orderDetails = getMockedOrderDetails();
		orderDetails.getOrderItems().get(0).setCount(0);
		
		InvalidOrderDetailsException thrownException = assertThrows(
				InvalidOrderDetailsException.class,
 	           () -> orderService.order(orderDetails),
 	           "Expected order() to throw InvalidOrderDetailsException, but it didn't"
 	    );
 
		 assertTrue(thrownException != null); 	
	}
	
	@Test
	public void orderAsync_OrderPlacedCorrectly_AcceptedOrderDetailsReturned() throws InvalidOrderDetailsException, CardAuthorizationException {
		Order orderDetails = getMockedOrderDetails();
		AcceptedKitchenResponse kitchenResponse = new AcceptedKitchenResponse(10);
		AcceptedDeliveryResponse deliveryResponse = new AcceptedDeliveryResponse(15);
		UUID expectedOrderId = UUID.randomUUID();
		BigDecimal orderItemPrice = orderDetails.getOrderItems().get(0).getPrice();
		BigDecimal deliveryPrice = new BigDecimal(199.99);
		BigDecimal expectedPrice = orderItemPrice.add(deliveryPrice);
		when(paymentRepositoryMock.authoriseCreditCard(any(CreditCardPaymentDetails.class))).thenReturn(true);
		when(deliveryRepositoryMock.createDelivery(orderDetails.getDeliveryDetails())).thenReturn(deliveryResponse);
		when(restaurantRepositoryMock.createOrder(orderDetails)).thenReturn(kitchenResponse);
		when(orderRepositoryMock.createOrder(orderDetails)).thenReturn(expectedOrderId);

		AcceptedOrderDetails result = orderService.order(orderDetails);

    	assertTrue(result != null);
    	assertTrue(expectedOrderId.equals(result.getOrderId()));

        verify(paymentRepositoryMock, times(1)).authoriseCreditCard(argThat(x -> x.getAmount().doubleValue() == expectedPrice.doubleValue()));
        verify(paymentRepositoryMock, times(1)).executePayment(argThat(x -> x.getAmount().doubleValue() == expectedPrice.doubleValue()));
        verify(deliveryRepositoryMock, times(1)).createDelivery(orderDetails.getDeliveryDetails());
        verify(restaurantRepositoryMock, times(1)).createOrder(orderDetails);
        verify(orderRepositoryMock, times(1)).createOrder(orderDetails);
	}
	
	 private Order getMockedOrderDetails()
     {
         var orderItems = new ArrayList<OrderItem>();
         orderItems.add(new OrderItem(UUID.randomUUID(),"Pizza", 1, new BigDecimal(333.420)));
         Address pickupAddress = new Address("Diagon Alley", "", "London", "", "UK");
         Address deliveryAddress = new Address("221b Baker Street", "", "London", "", "UK");
         DeliveryDetails deliveryDetails = new DeliveryDetails(pickupAddress, deliveryAddress);
         PaymentDetails paymentDetails = new PaymentDetails(PaymentType.CREDIT_CARD, "4242-4242-4242-4242");
         return new Order(orderItems, deliveryDetails, paymentDetails);
     }
}
