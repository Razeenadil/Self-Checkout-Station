package org.lsmr.SelfCheckoutSoftwareTest;

import java.math.BigDecimal;
import java.util.Calendar;

import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.external.CardIssuer;

public class Amex extends CardIssuer {

	Calendar calendarNow = Calendar.getInstance();
	
	// An Amex credit card has 15-digit card number, 4-digit cvv and 4-digit PIN.  
	
	// Credit card 1 info
	private final String typeCredit1 = "credit";
	private final String numberCredit1 = "333334444455555";
	private final String cardholderCredit1 = "Billy Smith";
	private final String cvvCredit1 = "344";
	private final String pinCredit1 = "1234";
	public final boolean isTapEnabledCredit1 = true;
	public final boolean hasChipCredit1 = true;
	private BigDecimal amountCredit1 = new BigDecimal("5000.00");
	
	// Credit card 2 info
	private final String typeCredit2 = "credit";
	private final String numberCredit2 = "222221111166666";
	private final String cardholderCredit2 = "John Bob";
	private final String cvvCredit2 = "566";
	private final String pinCredit2 = "4321";
	public final boolean isTapEnabledCredit2 = true;
	public final boolean hasChipCredit2 = true;
	private BigDecimal amountCredit2 = new BigDecimal("9000.00");
	
	// Credit card 3 info
	private final String typeCredit3 = "credit";
	private final String numberCredit3 = "777778888899999";
	private final String cardholderCredit3 = "Jane Doe";
	private final String cvvCredit3 = "778";
	private final String pinCredit3 = "5678";
	public final boolean isTapEnabledCredit3 = true;
	public final boolean hasChipCredit3 = true;
	private BigDecimal amountCredit3 = new BigDecimal("2500.00");
	
	public Card amexCredit1; 
	public Card amexCredit2;
	public Card amexCredit3;

	public Amex() {
		
		super("AmericanExpress");
		
		calendarNow.add(Calendar.YEAR, 5);
		
		// As stated on Line 134 of CardIssuer.java, card information is added to the company's database when the card is created.
		this.addCardData(numberCredit1, cardholderCredit1, calendarNow, cvvCredit1, amountCredit1);
		this.addCardData(numberCredit2, cardholderCredit2, calendarNow, cvvCredit2, amountCredit2);
		this.addCardData(numberCredit3, cardholderCredit3, calendarNow, cvvCredit3, amountCredit3);
		
		// To simulate a card issuer, cards are created after data is entered into database
		amexCredit1 = new Card(typeCredit1, numberCredit1, cardholderCredit1, cvvCredit1, pinCredit1, isTapEnabledCredit1, hasChipCredit1);
		amexCredit2 = new Card(typeCredit2, numberCredit2, cardholderCredit2, cvvCredit2, pinCredit2, isTapEnabledCredit2, hasChipCredit2);
		amexCredit3 = new Card(typeCredit3, numberCredit3, cardholderCredit3, cvvCredit3, pinCredit3, isTapEnabledCredit3, hasChipCredit3);
		
	}
}
