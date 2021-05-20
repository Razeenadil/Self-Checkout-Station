package org.lsmr.SelfCheckoutSoftwareTest;

import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.SelfCheckoutSoftware.SelfCheckoutMachine;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.external.CardIssuer;

public class OurCardReaderListenerTest {

	SelfCheckoutMachine mac = null;
	CardReader c = null;
	
	@Before
	public void setup() {
		mac = new SelfCheckoutMachine();
		c = mac.checkoutStation.cardReader;
		mac.ciSelected = new CardIssuer("shenanigans");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 5);
		mac.ciSelected.addCardData("6123", "Dylan", cal, "195", BigDecimal.valueOf(1000000000));
		mac.ciSelected.addCardData("6124", "Dylan2", cal, "195", BigDecimal.ONE);
		mac.totalPrice = 100;
		mac.cReaderListener.validMemberNumbers.add("1234");
	}
	
	@Test
	public void enableDisable() {
		// Make sure no exceptions are thrown
		try {
			c.disable();
			c.enable();
		} catch (Exception e) {
			fail("Error thrown when disabling/enabling");
		}
	}
	
	@Test
	public void waysOfReading() {
		Card cr = new Card("Credit", "6123", "Dylan", "195", "4567", true, true);
		try {
			c.swipe(cr, null);
		} catch (Exception e) {
			fail("Error thrown when swiping card" + e.toString());
		}
		try {
			c.insert(cr, "4567");
			c.remove();
		} catch (Exception e) {
			fail("Error thrown when insterting card");
		}
		try {
			c.tap(cr);
		} catch (Exception e) {
			fail("Error thrown when tapping card");
		}
	}
	
	// Insert requires chip
	// Swipe is free
	// Tap needs to have tap enabled
	
	@Test
	public void goodTap() {
		mac.balance = 100;
		Card cr = new Card("Credit", "6123", "Dylan", "195", "4567", true, true);
		try {
			c.tap(cr);
		} catch (Exception e) {
			fail("Error thrown when tapping card");
		}
		assertTrue("Balance not reset", mac.balance == 0);
	}
	
	@Test
	public void badTap() {
		mac.balance = 100;
		Card cr = new Card("Credit", "6123", "Dylan", "195", "4567", false, true);
		try {
			c.tap(cr);
		} catch (Exception e) {
			fail("Error thrown when tapping card");
		}
		assertTrue("Balance wrongly reset", mac.balance == 100);
	}
	
	@Test
	public void GoodSwipe() {
		mac.balance = 100;
		Card cr = new Card("Credit", "6123", "Dylan", "195", "4567", false, true);
		try {
			c.swipe(cr,null);
		} catch (Exception e) {
			fail("Error thrown when swiping card");
		}
		assertTrue("Balance not reset", mac.balance == 0);
	}
	
	@Test
	public void GoodInsert() {
		mac.balance = 100;
		Card cr = new Card("Credit", "6123", "Dylan", "195", "4567", false, true);
		try {
			c.insert(cr,"4567");
		} catch (Exception e) {
			fail("Error thrown when inserting card");
		}
		try {
			c.remove();
		}  catch (Exception e) {
			// Ignore
		}
		assertTrue("Balance not reset", mac.balance == 0);
	}
	
	@Test
	public void BadInsertNotAllowed() {
		mac.balance = 100;
		Card cr = new Card("Credit", "6123", "Dylan", "195", "4567", false, false);
		try {
			c.insert(cr,"4567");
		} catch (Exception e) {
			// Error expected
		}
		try {
			c.remove();
		}  catch (Exception e) {
			// Ignore
		}
		assertTrue("Balance not reset", mac.balance == 100);
	}
	
	@Test
	public void BadInsertWrongPin() {
		mac.balance = 100;
		Card cr = new Card("Credit", "6123", "Dylan", "195", "4567", false, true);
		try {
			c.insert(cr,"4568");
			c.remove();
		} catch (Exception e) {
			// Error expected
		}
		try {
			c.remove();
		}  catch (Exception e) {
			// Ignore
		}
		assertTrue("Balance not reset", mac.balance == 100);
	}
	
	@Test
	public void brokePeopleCantPay() {
		mac.balance = 100;
		Card cr = new Card("Credit", "6124", "Dylan2", "195", "4567", true, true);
		try {
			c.swipe(cr, null);
		} catch (Exception e) {
			// Do nothing
		}
		assertTrue("Failed transaction shouldn't go through!", mac.balance == 100);
		
	}
	
	@Test
	public void goodBadMemberCard() {
		try {
			Card cr = new Card("Membership", "1234", "Dylan3", "195", "4567", true, true);
			c.swipe(cr, null);
			assertTrue("Membership card not read",mac.cReaderListener.memberNumber.equals("1234"));
			Card cr2 = new Card("Membership", "1235", "Dylan3", "195", "4567", true, true);
			c.swipe(cr2, null);
			assertTrue("Membership card not read",mac.cReaderListener.memberNumber.equals("1234"));
		} catch (Exception e) {
			fail("Error thrown when reading membership cards");
		}
	}
	
	
	// Test the two enter MemberShip Card 
		@Test
		public void enterGoodMemberCard() {
			HashMap<String, String> database = new HashMap<>();
			try {
				Card cr = new Card("Membership", "1234", "Dylan3", "195", "4567", true, true);
				
				database.put("1234", "Dylan3");
				 
				mac.enterMembershipNumber("1234");
				assertTrue("Membership card isVaild",database.containsKey(mac.membershipNumber));
				
			} catch (Exception e) {
				fail("Error thrown when reading membership cards");
			}
			
		}
		@Test
		public void enterBadMemberCard() {
			HashMap<String, String> database = new HashMap<>();
			try {
				Card cr = new Card("Membership", "1234", "Dylan3", "195", "4567", true, true);
				
				database.put("1234", "Dylan3");
				
				mac.enterMembershipNumber("5645");
				assertFalse("Membership card isVaild",database.containsKey(mac.membershipNumber));
				
			} catch (Exception e) {
				fail("Error thrown when reading membership cards");
			}
			
		}

		@Test// test paywithGiftCard
		public void paywithGiftTest() {
			Card cr = new Card("Gift", "1234", "John Doe", null, "4567", true, true);
			try {
				
				mac.payWithGift(cr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				fail("Error thrown when  paywithGiftTest is invaild");
			}
			
			
		}
		
	@After
	public void cleanup() {
		mac = null;
		c = null;
	}

}
