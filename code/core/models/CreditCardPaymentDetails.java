package core.models;

import java.math.BigDecimal;

public class CreditCardPaymentDetails {
	
	private BigDecimal amount;
	
	private String cardNumber;

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public CreditCardPaymentDetails(BigDecimal amount, String cardNumber) {
		this.amount = amount;
		this.cardNumber = cardNumber;
	}
}
