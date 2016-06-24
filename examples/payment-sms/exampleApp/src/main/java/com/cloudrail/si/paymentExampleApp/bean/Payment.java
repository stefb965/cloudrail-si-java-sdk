package com.cloudrail.si.paymentExampleApp.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.cloudrail.si.interfaces.Email;
import com.cloudrail.si.types.Charge;
import com.cloudrail.si.types.CreditCard;
import com.cloudrail.si.types.SubscriptionPlan;

/**
 * 
 * @author eleftheria
 *
 */
public class Payment {
	
	private com.cloudrail.si.interfaces.Payment paymentVendor;
	private Email emailVendor;
	
	private List<Charge> charges = new ArrayList<Charge>();
	private String item = "Stripe";
	private Double amount = 0.0;
	private String cardNumber;
	private String firstName;
	private String lastName;
	private String currency;
	private String email;
	private boolean isChargeCreated;
	private List<SubscriptionPlan> subcriptionPlans = new ArrayList<SubscriptionPlan>();
	private String subscriptionName;
	private String subscriptionDescription;
	private String subscriptionPlanId;
	private boolean isSubscriptionCreated;
	private String customerCardNumber;
	private String customerFirstName;
	private String customerLastName;
	
	public Payment(com.cloudrail.si.interfaces.Payment paymentVendor, Email emailVendor) {
		this.paymentVendor = paymentVendor;
		this.emailVendor  = emailVendor;
	}
	
	public List<Charge> getCharges() {
		return charges;
	}
	
	public void getAllCharges() {
        Calendar thisDay = Calendar.getInstance();
        thisDay.add(Calendar.DATE, -1);

        Calendar today = Calendar.getInstance();
        charges = paymentVendor.listCharges(thisDay.getTimeInMillis(), today.getTimeInMillis(), null);		
	}
	
	public void getAllSubscriptionPlans() {
		subcriptionPlans = paymentVendor.listSubscriptionPlans();
	}
	
	public void createCharge() {
		try {
			amount = amount * 100;
			Long longAmount = amount.longValue();
			CreditCard creditCard = new CreditCard("123", 3L, 2017L, cardNumber, "visa", firstName, lastName, null);
			paymentVendor.createCharge(longAmount, currency, creditCard);
			amount = amount / 100;
			
			if(email!=null && !email.isEmpty()) {
				List<String> emails = new ArrayList<String>();
				emails.add(email);
				
				// Mailjet on test environment doesn't actually send the email
				emailVendor.sendEmail("eleftheria@cloudrail.com", "Cloudrail", emails, "Payment Test App", "You have created a new charge", null, null, null);
			}
			getAllCharges();
			isChargeCreated = true;

		} catch (Exception e) {
			isChargeCreated = false;
			e.printStackTrace();
		}		
	}
	
	
	public void refundCharge(String chargeId) {
		paymentVendor.refundCharge(chargeId);
		getAllCharges();
	}
	
	public void createSubscription() {
		System.out.println("Sub Plan id: "+subscriptionPlanId);
		CreditCard creditCard = new CreditCard("123", 3L, 2017L, customerCardNumber, "visa", customerFirstName, customerLastName, null);
		
		paymentVendor.createSubscription(subscriptionPlanId, subscriptionName, subscriptionDescription, creditCard);
		System.out.println("Subscription is created");
	}
	
	public void setCharges(List<Charge> charges) {
		this.charges = charges;
	}
	
	public String getCharge() {
		return paymentVendor.getCharge("ch_18H9cCHmQek6gVivKyicf0MT").toString();
	}
	
	public String getItem() {
		return item;
	}
	
	public void setItem(String item) {
		this.item = item;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getCurrency() {
		return currency;
	}
	
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public boolean getIsChargeCreated() {
		return isChargeCreated;
	}
	
	public void setChargeCreated(boolean isChargeCreated) {
		this.isChargeCreated = isChargeCreated;
	}
	
	public List<SubscriptionPlan> getSubcriptionPlans() {
		return subcriptionPlans;
	}
	
	public void setSubcriptionPlans(List<SubscriptionPlan> subcriptionPlans) {
		this.subcriptionPlans = subcriptionPlans;
	}

	public String getSubscriptionName() {
		return subscriptionName;
	}

	public void setSubscriptionName(String subscriptionName) {
		this.subscriptionName = subscriptionName;
	}

	public String getSubscriptionDescription() {
		return subscriptionDescription;
	}

	public void setSubscriptionDescription(String subscriptionDescription) {
		this.subscriptionDescription = subscriptionDescription;
	}

	public String getSubscriptionPlanId() {
		return subscriptionPlanId;
	}

	public void setSubscriptionPlanId(String subscriptionPlanId) {
		this.subscriptionPlanId = subscriptionPlanId;
	}
	
	public boolean getIsSubscriptionCreated() {
		return isSubscriptionCreated;
	}
	
	public void setSubscriptionCreated(boolean isSubscriptionCreated) {
		this.isSubscriptionCreated = isSubscriptionCreated;
	}

	public String getCustomerCardNumber() {
		return customerCardNumber;
	}

	public void setCustomerCardNumber(String customerCardNumber) {
		this.customerCardNumber = customerCardNumber;
	}

	public String getCustomerFirstName() {
		return customerFirstName;
	}

	public void setCustomerFirstName(String customerFirstName) {
		this.customerFirstName = customerFirstName;
	}

	public String getCustomerLastName() {
		return customerLastName;
	}

	public void setCustomerLastName(String customerLastName) {
		this.customerLastName = customerLastName;
	}
	

}
