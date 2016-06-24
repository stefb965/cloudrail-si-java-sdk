package com.cloudrail.si.paymentExampleApp.bean;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import com.cloudrail.si.interfaces.Email;
import com.cloudrail.si.services.MailJet;
import com.cloudrail.si.services.Stripe;

/**
 * 
 * @author eleftheria
 *
 */
@SessionScoped
@ManagedBean(name= "stripePayment", eager = true)
public class StripePayment {
	
	private com.cloudrail.si.interfaces.Payment stripe;	
	private Email mailJet;
	private Payment payment;
	
	public StripePayment() {
		stripe = new Stripe(null, "sk_test_BqBCfLlMpRgSra0L6jm1wc0P");
		mailJet = new MailJet(null, "af3ad68114be8f4515894dcfa5f84a06", "3a2ac1bf0048b174e402da0637ed3b41");
		
		payment = new Payment(stripe, mailJet);
	}
	
	@PostConstruct
	public void init() {
		FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		payment.getAllCharges();
		payment.getAllSubscriptionPlans();
	}	
	
	public Payment getPayment() {
		return payment;
	}
	
	public void setPayment(Payment payment) {
		this.payment = payment;
	}

}
