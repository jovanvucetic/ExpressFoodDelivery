package core.interfaces.repositories;

import core.models.CreditCardPaymentDetails;

public interface PaymentRepository {
	
	void executePayment(CreditCardPaymentDetails payment);

    boolean authoriseCreditCard(CreditCardPaymentDetails payment);
}