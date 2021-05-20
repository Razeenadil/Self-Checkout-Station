package org.lsmr.SelfCheckoutSoftwareTest;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.SelfCheckoutSoftware.SelfCheckoutMachine;
import org.lsmr.selfcheckout.Coin;

public class OurCoinTrayListenerTest {
	
	SelfCheckoutMachine mac = null;
	Currency cur;
	
	@Before
	public void makeMachine() {
		mac = new SelfCheckoutMachine();
		cur = Currency.getInstance("CAD");
	}
	
	@Test
	public void enableDisable() {
		try {
			mac.RejectedCoinReturnTray.disable();
			mac.RejectedCoinReturnTray.enable();
		} catch (Exception e) {
			fail("disabling and enabling throws an error");
		}
	}
	
	@Test
	public void addCoin() {
		int coinCount = mac.cTrayListener.getEjectedCoinCount();
		try {
			mac.RejectedCoinReturnTray.accept(new Coin(new BigDecimal(1), cur));
		} catch (Exception e) {
			fail("Threw error when returning coins");
		}
		int newCoinCount = mac.cTrayListener.getEjectedCoinCount();
		assertEquals("Coin addition to the tray was not recorded.", coinCount + 1, newCoinCount);
	}
	
	
	@After
	public void removeMachine() {
		mac = null;
	}
}
