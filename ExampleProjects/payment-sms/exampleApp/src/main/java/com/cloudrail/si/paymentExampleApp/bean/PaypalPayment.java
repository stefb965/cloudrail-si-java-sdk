package com.cloudrail.si.paymentExampleApp.bean;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import com.cloudrail.si.interfaces.Email;
import com.cloudrail.si.services.MailJet;
import com.cloudrail.si.services.PayPal;

/**
 * 
 * @author eleftheria
 *
 */
@SessionScoped
@ManagedBean(name= "paypalPayment", eager = true)
public class PaypalPayment {
	
	private com.cloudrail.si.interfaces.Payment paypal;
	private Email mailJet;
	private Payment payment;
	
	public PaypalPayment() {
		paypal = new PayPal(null, true, "AVeum9__j-L3R4d5kCxB6WBHwU6_D2t572vIcsXt5NVJGCfckrFaZUiAQSU9iX2RzS9I6YN8PhWFUzJ7",
                "EACwCe3Rmq7NgSWu0HNOXvxsC_q-SW4NRDUkOsE0vPRCVYZxE2V7bUqJA32j7Da_sW_XJXJOUgtUO2nf");
		
		mailJet = new MailJet(null, "af3ad68114be8f4515894dcfa5f84a06", "3a2ac1bf0048b174e402da0637ed3b41");
		
		payment = new Payment(paypal, mailJet);
	
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
