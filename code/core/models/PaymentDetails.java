package core.models;

import java.math.BigDecimal;

public class PaymentDetails {
	
	private PaymentType paymentType;
	
	private String cardNumber;

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public PaymentDetails(PaymentType paymentType, String cardNumber) {
		super();
		this.paymentType = paymentType;
		this.cardNumber = cardNumber;
	}
	
	public CreditCardPaymentDetails toCreditCardPaymentDetails(BigDecimal amount) {
		return new CreditCardPaymentDetails(amount, cardNumber);
	}
}
