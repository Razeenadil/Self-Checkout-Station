package org.lsmr.SelfCheckoutSoftwareTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.SelfCheckoutSoftware.SelfCheckoutMachine;
import org.lsmr.selfcheckout.Coin;

public class OurCoinSlotListenerTest {
	
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
			int b = mac.cSlotListener.getInsertedCoinCount();
			assertTrue("Bad return value for accepted count", b >= 0);
		} catch (Exception e) {
			fail("Get methods threw exception.");
		}
	}
	
	@Test
	public void insertNote() {
		int current = mac.cSlotListener.getInsertedCoinCount();
		try {
			mac.checkoutStation.coinSlot.accept(new Coin(new BigDecimal(5), cur));
		} catch (Exception e) {
			fail("Inserting coin threw an exception");
		}
		int next = mac.cSlotListener.getInsertedCoinCount();
		assertEquals("Listener didn't notice that coin was inserted.",current+1,next);
	}
	
	
	@Test
	public void enableDisable() {
		try {
			mac.checkoutStation.coinSlot.disable();
			mac.checkoutStation.coinSlot.enable();
		} catch (Exception e) {
			fail("disabling and enabling throws an error");
		}
	}
	
	@After
	public void removeMachine() {
		mac = null;
	}
	
}
