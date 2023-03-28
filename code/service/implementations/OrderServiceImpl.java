package service.implementations;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.function.BinaryOperator;

import core.exceptions.CardAuthorizationException;
import core.exceptions.InvalidOrderDetailsException;
import core.interfaces.repositories.DeliveryRepository;
import core.interfaces.repositories.OrderRepository;
import core.interfaces.repositories.PaymentRepository;
import core.interfaces.repositories.RestaurantRepository;
import core.interfaces.services.OrderService;
import core.models.*;

public class OrderServiceImpl implements OrderService {

	private static final double DEFAULT_DELIVERY_FEE = 199.99;
	
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
		
		validateInputRequest(orderDetails);	

		var totalOrderPrice = calculateTotalOrderPrice(orderDetails);

		if (orderDetails.getPaymentDetails().getPaymentType() == PaymentType.CREDIT_CARD) {
			authorizePaymentMethod(orderDetails, totalOrderPrice);
		}

		AcceptedKitchenResponse kitchenResponse = restaurantRepository.createOrder(orderDetails);
		AcceptedDeliveryResponse deliveryServiceResponse = deliveryRepository.createDelivery(orderDetails.getDeliveryDetails());

		executePaymentPaymentIfNeeded(orderDetails, totalOrderPrice);
		UUID orderId = orderRepository.createOrder(orderDetails);

		int estimatedDeliveryTimeInMinutes = kitchenResponse.getEstimatedPreparationTimeInMinutes()
				+ deliveryServiceResponse.getEstimatedDeliveryTimeInMinutes();

		Date orderExpectedOn = new Date(Calendar.getInstance().getTimeInMillis() + (estimatedDeliveryTimeInMinutes * 60 * 1000));
		
		var reportStringBuilder = generateOrderReport(orderDetails);

		Date orderAcceptedOn = new Date();
		
		return new AcceptedOrderDetails(orderId, orderAcceptedOn, orderExpectedOn, reportStringBuilder.toString());
	}

	private void executePaymentPaymentIfNeeded(Order orderDetails, BigDecimal totalOrderPrice) {
		if (orderDetails.getPaymentDetails().getPaymentType() == PaymentType.CREDIT_CARD) {
			paymentRepository
					.executePayment(orderDetails.getPaymentDetails().toCreditCardPaymentDetails(totalOrderPrice));
		}
	}

	private StringBuilder generateOrderReport(Order orderDetails) {
		// report string builder
		var reportStringBuilder = new StringBuilder("Order summary: \n");
		
		orderDetails.getOrderItems().stream().forEach(x -> generateOrderItemReport(reportStringBuilder, x));
		
		reportStringBuilder.append("Delivery price: " + DEFAULT_DELIVERY_FEE);
		
		return reportStringBuilder;
	}

	private void generateOrderItemReport(StringBuilder reportStringBuilder, OrderItem orderItem) {
		var fullPrice = orderItem.getCount() * orderItem.getPrice().doubleValue();
		var orderReportItem = orderItem.getCount() + " x, " + orderItem.getName() + " - " + fullPrice + " din\n";
		reportStringBuilder.append(orderReportItem);
	}

	private BigDecimal calculateTotalOrderPrice(Order orderDetails) {
		BigDecimal fullOrderPrice = orderDetails.getOrderItems().stream()
				.map(x -> getOrderItemPrice(x))
				.reduce(new BigDecimal(0), (a, b) -> a.add(b))
				.add(new BigDecimal(DEFAULT_DELIVERY_FEE));
				
		return fullOrderPrice;
	}

	private BigDecimal getOrderItemPrice(OrderItem orderItem) {
		return orderItem.getPrice().multiply(new BigDecimal(orderItem.getCount()));
	}
	
	private void validateInputRequest(Order orderDetails) throws InvalidOrderDetailsException {
		if (orderDetails == null) {
			throw new InvalidOrderDetailsException("Order details object cannot be null");
		}
		
		if (orderDetails.getDeliveryDetails() == null) {
			throw new InvalidOrderDetailsException("Delivery details object cannot be null");
		}

		if (!isPaymentMethodValid(orderDetails)) {
			throw new InvalidOrderDetailsException("Payment details are not valid");
		}
		
		if (orderDetails.getOrderItems() == null || orderDetails.getOrderItems().size() == 0) {
			throw new InvalidOrderDetailsException("Order items must be defined");
		}
		
		for (OrderItem orderItem : orderDetails.getOrderItems()) {
			validateOrderItems(orderItem);
		}
	}

	private void validateOrderItems(OrderItem orderItem) throws InvalidOrderDetailsException {
		if (orderItem == null || orderItem.getCount() <= 0) {
			throw new InvalidOrderDetailsException("Order item count must be higher than 0");
		}
	}
	
	private void authorizePaymentMethod(Order orderDetails, BigDecimal fullOrderPrice)
			throws CardAuthorizationException {
		if (!paymentRepository
				.authoriseCreditCard(orderDetails.getPaymentDetails().toCreditCardPaymentDetails(fullOrderPrice))) {
			throw new CardAuthorizationException();
		}
	}
	
	private boolean isPaymentMethodValid(Order orderDetails) {
		return orderDetails.getPaymentDetails() != null
				&& (orderDetails.getPaymentDetails().getPaymentType() != PaymentType.CREDIT_CARD
						|| (orderDetails.getPaymentDetails().getCardNumber() != null
								&& !orderDetails.getPaymentDetails().getCardNumber().equals("")));
	}
}