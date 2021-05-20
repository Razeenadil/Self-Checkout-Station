package org.lsmr.SelfCheckoutSoftwareTest;

import java.math.BigDecimal;
import java.util.Calendar;

import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.external.CardIssuer;

public class Interac extends CardIssuer {
	
	Calendar calendarNow = Calendar.getInstance();
	
	// An Interac debit card has 16-digit card number, 3-digit cvv and 4-digit PIN.  
	
	// Credit card 1 info
	private final String typeDebit1 = "debit";
	private final String numberDebit1 = "9999000011112222";
	private final String cardholderDebit1 = "Jane Bob";
	private final String cvvDebit1 = "123";
	private final String pinDebit1 = "1234";
	public final boolean isTapEnabledDebit1 = true;
	public final boolean hasChipDebit1 = true;
	private BigDecimal amountDebit1 = new BigDecimal("1000.00");
	
	// Credit card 2 info
	private final String typeDebit2 = "debit";
	private final String numberDebit2 = "1111222233334444";
	private final String cardholderDebit2 = "Billy Smith";
	private final String cvvDebit2 = "321";
	private final String pinDebit2 = "4321";
	public final boolean isTapEnabledDebit2 = true;
	public final boolean hasChipDebit2 = true;
	private BigDecimal amountDebit2 = new BigDecimal("5000.00");
	
	// Credit card 3 info
	private final String typeDebit3 = "debit";
	private final String numberDebit3 = "5555666677778888";
	private final String cardholderDebit3 = "John Doe";
	private final String cvvDebit3 = "789";
	private final String pinDebit3 = "5678";
	public final boolean isTapEnabledDebit3 = true;
	public final boolean hasChipDebit3 = true;
	private BigDecimal amountDebit3 = new BigDecimal("8000.00");
	
	public Card visaCredit1; 
	public Card visaCredit2;
	public Card visaCredit3;

	public Interac() {
		
		super("Interac");
		
		calendarNow.add(Calendar.YEAR, 5);
		
		// As stated on Line 134 of CardIssuer.java, card information is added to the company's database when the card is created.
		this.addCardData(numberDebit1, cardholderDebit1, calendarNow, cvvDebit1, amountDebit1);
		this.addCardData(numberDebit2, cardholderDebit2, calendarNow, cvvDebit2, amountDebit2);
		this.addCardData(numberDebit3, cardholderDebit3, calendarNow, cvvDebit3, amountDebit3);
		
		// To simulate a card issuer, cards are created after data is entered into database
		visaCredit1 = new Card(typeDebit1, numberDebit1, cardholderDebit1, cvvDebit1, pinDebit1, isTapEnabledDebit1, hasChipDebit1);
		visaCredit2 = new Card(typeDebit2, numberDebit2, cardholderDebit2, cvvDebit2, pinDebit2, isTapEnabledDebit2, hasChipDebit2);
		visaCredit3 = new Card(typeDebit3, numberDebit3, cardholderDebit3, cvvDebit3, pinDebit3, isTapEnabledDebit3, hasChipDebit3);
		
	}
}
