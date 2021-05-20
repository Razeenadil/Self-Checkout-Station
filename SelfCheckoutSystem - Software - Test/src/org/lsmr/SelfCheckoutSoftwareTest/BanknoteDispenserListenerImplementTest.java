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
import org.lsmr.SelfCheckoutSoftware.BanknoteDispenserListenerImplement;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.devices.BanknoteDispenser;
import org.lsmr.selfcheckout.devices.BanknoteStorageUnit;
import org.lsmr.selfcheckout.devices.UnidirectionalChannel;

public class BanknoteDispenserListenerImplementTest {
	
	BanknoteDispenser cd = null;
	BanknoteDispenserListenerImplement cdL = null;
	Currency cur = Currency.getInstance(Locale.CANADA);
	BanknoteStorageUnit cs = null;
	int maxC = 10;
	
	// Most tests are black-box tests in testChangeDispensing.java.
	// These are just a few white-box tests for 100% coverage
	
	@Before
	public void setup() {
		cd = new BanknoteDispenser(maxC);
		cdL = new BanknoteDispenserListenerImplement();
		cd.register(cdL);
		cs = new BanknoteStorageUnit(1000);
		UnidirectionalChannel<Banknote> uc = new UnidirectionalChannel<>(cs);
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
	public void addRemoveBanknote() {
		cdL.resetEmitted();
		int emitStart = cdL.getAmountEmitted();
		assertEquals("When resetting Banknotes emmitted, set total to 0",0,emitStart);
		try {
			cd.load(new Banknote(5, cur));
		} catch (Exception e) {
			fail("Error thrown when loading Banknote");
		}
		assertEquals("Adding Banknote shouldn't change emitted count",emitStart,cdL.getAmountEmitted());
		try {
			cd.emit();
		} catch (Exception e) {
			fail(e.toString());
		}
		assertEquals("Emitting Banknote didn't change count",emitStart+1,cdL.getAmountEmitted());
	}
	
	@Test
	public void loadUnloadBanknote() {
		// Check for exceptions
		try {
			cd.load(new Banknote(5,cur));
		} catch (Exception e) {
			fail("Error thrown when loading Banknote");
		}
		try {
			cd.unload();
		} catch (Exception e) {
			fail("Error thrown when unloading Banknote");
		}
	}
	
	@Test
	public void emptyNotEmpty() {
		// Check for exceptions
		try {
			cd.load(new Banknote(5,cur));
		} catch (Exception e) {
			fail("Error thrown when loading Banknote");
		}
		assertTrue(!cdL.isEmpty());
		try {
			cd.emit();
		} catch (Exception e) {
			fail("Error thrown when emitting Banknote");
		}
		assertTrue(cdL.isEmpty());
		
	}
	
	@Test
	public void emptyAndFullTest() {
		try {
			cd.unload();
			assertTrue(cdL.isEmpty());
			for (int i = 1; i <= maxC; i++) {
				cd.load(new Banknote(5,cur));
				assertTrue("Says it's empty when it isn't",!cdL.isEmpty());
			}
			
			int count = 0;
			while (!cdL.isEmpty()) {
				cd.emit();
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
