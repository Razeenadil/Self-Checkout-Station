package org.lsmr.SelfCheckoutSoftwareTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Currency;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.SelfCheckoutSoftware.SelfCheckoutMachine;
import org.lsmr.selfcheckout.Banknote;

public class OurBanknoteSlotListenerTest {
	
	// Note: we don't test functionality that hasn't been implemented yet.
	// These are white-box tests for the specific listener.
	
	SelfCheckoutMachine mac = null;
	Currency cur;
	
	@Before
	public void makeMachine() {
		mac = new SelfCheckoutMachine();
		cur = Currency.getInstance("CAD");
	}
	
	// Tests getInsertedBanknoteCount and getEjectedBanknoteCount
	@Test
	public void getMethods() {
		try {
			int a = mac.bSlotListener.getEjectedBanknoteCount();
			int b = mac.bSlotListener.getInsertedBanknoteCount();
			assertTrue("Bad return value for ejected count", a >= 0);
			assertTrue("Bad return value for accepted count", b >= 0);
		} catch (Exception e) {
			fail("Get methods threw exception.");
		}
	}
	
	@Test
	public void insertNote() {
		int current = mac.bSlotListener.getInsertedBanknoteCount();
		try {
			mac.checkoutStation.banknoteInput.accept(new Banknote(1, cur));
		} catch (Exception e) {
			fail("Inserting note threw an exception");
		}
		int next = mac.bSlotListener.getInsertedBanknoteCount();
		assertEquals("Listener didn't notice that note was inserted.",current+1,next);
	}
	
	@Test
	public void ejectRemoveNote() {
		int current = mac.bSlotListener.getEjectedBanknoteCount();
		try {
			mac.checkoutStation.banknoteInput.accept(new Banknote(1, cur));
		} catch (Exception e) {
			fail("Ejecting note threw an exception");
		}
		int next = mac.bSlotListener.getEjectedBanknoteCount();
		assertEquals("Listener didn't notice that note was ejected.",current+1,next);
		
		try {
			mac.checkoutStation.banknoteInput.removeDanglingBanknote();
		} catch (Exception e) {
			fail("removing note threw an exception");
		}
		
	}
	
	
	@Test
	public void enableDisable() {
		try {
			mac.checkoutStation.banknoteInput.disable();
			mac.checkoutStation.banknoteInput.enable();
		} catch (Exception e) {
			fail("disabling and enabling throws an error");
		}
	}
	
	
	@After
	public void removeMachine() {
		mac = null;
	}
	
}
