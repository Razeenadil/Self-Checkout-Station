package org.lsmr.SelfCheckoutSoftwareTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.SelfCheckoutSoftware.OurCoinStorageUnitListener;
import org.lsmr.SelfCheckoutSoftware.SelfCheckoutMachine;
import org.lsmr.selfcheckout.Coin;

public class OurCoinStorageUnitListenerTest {

	SelfCheckoutMachine mac = null;
	Currency cur;
	OurCoinStorageUnitListener lis;
	
	@Before
	public void makeMachine() {
		mac = new SelfCheckoutMachine();
		cur = Currency.getInstance("CAD");
		lis = mac.cStorageUnitListener;
	}
	
	
	// Only tests for exceptions
	@Test
	public void enableDisable() {
		try {
			mac.AcceptedCoinStorage.disable();
			mac.AcceptedCoinStorage.enable();
		} catch (Exception e) {
			fail("disabling and enabling throws an error");
		}
	}
	
	// Try to fill the storage unit
	// Will empty the storage unit afterwards
	@Test
	public void RespondToFullStorage() {
		try {
			while (mac.AcceptedCoinStorage.getCapacity() > mac.AcceptedCoinStorage.getCoinCount()) {
				int val = mac.AcceptedCoinStorage.getCapacity() - mac.AcceptedCoinStorage.getCoinCount();
				mac.AcceptedCoinStorage.accept(new Coin(new BigDecimal(5), cur));
				if (val < mac.AcceptedCoinStorage.getCapacity() - mac.AcceptedCoinStorage.getCoinCount()) {
					fail("Loop may never terminate!");
				}
			}
		} catch (Exception e) {
			fail("Cannot deal with full storage");
		}
		mac.AcceptedCoinStorage.unload();
	}
	
	@Test
	public void addCoin() {
		int val = lis.getStoredCoinCount();
		try {
			mac.AcceptedCoinStorage.accept(new Coin(new BigDecimal(5), cur));
		} catch (Exception e) {
			fail("Inserting a Coin threw an error!");
		}
		int next = lis.getStoredCoinCount();
		assertEquals(val+1,next);
		mac.AcceptedCoinStorage.unload();
	}
	
	@Test
	public void loadUnloadNotes() {
		try {
			mac.AcceptedCoinStorage.load(new Coin(new BigDecimal(5), cur));
		} catch (Exception e) {
			fail("Could not load a Coin");
		}
		mac.AcceptedCoinStorage.unload();
	}
	
	
	@After
	public void removeMachine() {
		mac = null;
		lis = null;
	}

}
