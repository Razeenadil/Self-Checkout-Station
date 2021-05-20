package org.lsmr.SelfCheckoutSoftwareTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.SelfCheckoutSoftware.CoinDispenserListenerImplement;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.CoinDispenser;
import org.lsmr.selfcheckout.devices.CoinStorageUnit;
import org.lsmr.selfcheckout.devices.UnidirectionalChannel;

public class CoinDispenserListenerImplementTest {
	
	CoinDispenser cd = null;
	CoinDispenserListenerImplement cdL = null;
	Currency cur = Currency.getInstance(Locale.CANADA);
	CoinStorageUnit cs = null;
	int maxC = 10;
	
	// Most tests are black-box tests in testChangeDispensing.java.
	// These are just a few white-box tests for 100% coverage
	
	@Before
	public void setup() {
		cd = new CoinDispenser(maxC);
		cdL = new CoinDispenserListenerImplement();
		cd.register(cdL);
		cs = new CoinStorageUnit(1000);
		UnidirectionalChannel<Coin> uc = new UnidirectionalChannel<>(cs);
		cd.connect(uc);
	}
	
	@Test
	public void enableDisable() {
		// Make sure they don't throw errors
		try {
			cd.disable();
			cd.enable();
		} catch (Exception e) {
			fail("Exception thrown");
		}
	}
	
	@Test
	public void addRemoveCoin() {
		cdL.resetEmitted();
		int emitStart = cdL.getAmountEmitted();
		assertEquals("When resetting coins emmitted, set total to 0",0,emitStart);
		try {
			cd.accept(new Coin(BigDecimal.ONE, cur));
		} catch (Exception e) {
			fail("Error thrown when loading coin");
		}
		assertEquals("Adding coin shouldn't change emitted count",emitStart,cdL.getAmountEmitted());
		try {
			cd.emit();
		} catch (Exception e) {
			fail(e.toString());
		}
		assertEquals("Emitting coin didn't change count",emitStart+1,cdL.getAmountEmitted());
	}
	
	@Test
	public void loadUnloadCoin() {
		// Check for exceptions
		try {
			cd.load(new Coin(BigDecimal.ONE,cur));
		} catch (Exception e) {
			fail("Error thrown when loading coin");
		}
		try {
			cd.unload();
		} catch (Exception e) {
			fail("Error thrown when unloading coin");
		}
	}
	
	@Test
	public void emptyNotEmpty() {
		// Check for exceptions
		try {
			cd.load(new Coin(BigDecimal.ONE,cur));
		} catch (Exception e) {
			fail("Error thrown when loading coin");
		}
		assertTrue(!cdL.isEmpty());
		try {
			cd.emit();
		} catch (Exception e) {
			fail("Error thrown when emitting coin");
		}
		assertTrue(cdL.isEmpty());
		
		// Run once again, this time for unloading
		
		
		try {
			cd.load(new Coin(BigDecimal.ONE,cur));
		} catch (Exception e) {
			fail("Error thrown when loading coin");
		}
		assertTrue(!cdL.isEmpty());
		try {
			cd.unload();
		} catch (Exception e) {
			fail("Error thrown when emitting coin");
		}
		assertTrue(cdL.isEmpty());
		
	}
	
	@Test
	public void emptyAndFullTest() {
		try {
			cd.unload();
			assertTrue(cdL.isEmpty());
			for (int i = 1; i <= maxC; i++) {
				cd.accept(new Coin(BigDecimal.ONE,cur));
				assertTrue("Says it's empty when it isn't",!cdL.isEmpty());
				assertEquals("Says it is full when it isn't",cdL.isFull(),i==maxC);
			}
			
			int count = 0;
			while (!cdL.isEmpty()) {
				cd.emit();
				assertTrue("Says it's empty when it isn't",!cdL.isFull());
				count += 1;
			}
			assertTrue("Incorrectly shows when it is empty",count==maxC);
		} catch (Exception e) {
			fail("Error thrown when running test");
		}
		
		
	}
	
	
	@After
	public void teardown() {
		cd = null;
		cdL = null;
		cs = null;
	}

}
