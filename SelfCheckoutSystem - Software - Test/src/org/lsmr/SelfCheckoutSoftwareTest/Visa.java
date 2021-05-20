package org.lsmr.SelfCheckoutSoftwareTest;

import java.math.BigDecimal;
import java.util.Calendar;

import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.external.CardIssuer;

public class Visa extends CardIssuer {

	Calendar calendarNow = Calendar.getInstance();
	
	// A Visa credit card has 16-digit card number, 3-digit cvv and 4-digit PIN.  
	
	// Credit card 1 info
	private final String typeCredit1 = "credit";
	private final String numberCredit1 = "5555666677778888";
	private final String cardholderCredit1 = "Jane Bob";
	private final String cvvCredit1 = "123";
	private final String pinCredit1 = "1234";
	public final boolean isTapEnabledCredit1 = true;
	public final boolean hasChipCredit1 = true;
	private BigDecimal amountCredit1 = new BigDecimal("1000.00");
	
	// Credit card 2 info
	private final String typeCredit2 = "credit";
	private final String numberCredit2 = "1111222233334444";
	private final String cardholderCredit2 = "Billy Smith";
	private final String cvvCredit2 = "321";
	private final String pinCredit2 = "4321";
	public final boolean isTapEnabledCredit2 = true;
	public final boolean hasChipCredit2 = true;
	private BigDecimal amountCredit2 = new BigDecimal("5000.00");
	
	// Credit card 3 info
	private final String typeCredit3 = "credit";
	private final String numberCredit3 = "9999000011112222";
	private final String cardholderCredit3 = "John Doe";
	private final String cvvCredit3 = "789";
	private final String pinCredit3 = "5678";
	public final boolean isTapEnabledCredit3 = true;
	public final boolean hasChipCredit3 = true;
	private BigDecimal amountCredit3 = new BigDecimal("8000.00");
	
	public Card visaCredit1; 
	public Card visaCredit2;
	public Card visaCredit3;

	public Visa() {
		
		super("Visa");
		
		calendarNow.add(Calendar.YEAR, 5);
		
		// As stated on Line 134 of CardIssuer.java, card information is added to the company's database when the card is created.
		this.addCardData(numberCredit1, cardholderCredit1, calendarNow, cvvCredit1, amountCredit1);
		this.addCardData(numberCredit2, cardholderCredit2, calendarNow, cvvCredit2, amountCredit2);
		this.addCardData(numberCredit3, cardholderCredit3, calendarNow, cvvCredit3, amountCredit3);
		
		// To simulate a card issuer, cards are created after data is entered into database
		visaCredit1 = new Card(typeCredit1, numberCredit1, cardholderCredit1, cvvCredit1, pinCredit1, isTapEnabledCredit1, hasChipCredit1);
		visaCredit2 = new Card(typeCredit2, numberCredit2, cardholderCredit2, cvvCredit2, pinCredit2, isTapEnabledCredit2, hasChipCredit2);
		visaCredit3 = new Card(typeCredit3, numberCredit3, cardholderCredit3, cvvCredit3, pinCredit3, isTapEnabledCredit3, hasChipCredit3);
		
	}
}
