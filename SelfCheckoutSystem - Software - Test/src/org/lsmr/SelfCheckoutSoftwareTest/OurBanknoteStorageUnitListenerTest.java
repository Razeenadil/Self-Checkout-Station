package org.lsmr.SelfCheckoutSoftwareTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Currency;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.SelfCheckoutSoftware.OurBanknoteStorageUnitListener;
import org.lsmr.SelfCheckoutSoftware.SelfCheckoutMachine;
import org.lsmr.selfcheckout.Banknote;

public class OurBanknoteStorageUnitListenerTest {

	SelfCheckoutMachine mac = null;
	Currency cur;
	OurBanknoteStorageUnitListener lis;
	
	@Before
	public void makeMachine() {
		mac = new SelfCheckoutMachine();
		cur = Currency.getInstance("CAD");
		lis = mac.bStorageUnitListener;
	}
	
	
	// Only tests for exceptions
	@Test
	public void enableDisable() {
		try {
			// mac.AcceptedBanknoteStorage.disable();
			// mac.AcceptedBanknoteStorage.enable();
			mac.checkoutStation.banknoteStorage.enable();
			mac.checkoutStation.banknoteStorage.disable();
			
		} catch (Exception e) {
			fail("disabling and enabling throws an error");
		}
	}
	
	// Try to fill the storage unit
	// Will empty the storage unit afterwards
	@Test
	public void RespondToFullStorage() {
		try {
			while (mac.checkoutStation.banknoteStorage.getCapacity() > mac.checkoutStation.banknoteStorage.getBanknoteCount()) {
				int val = mac.checkoutStation.banknoteStorage.getCapacity() - mac.checkoutStation.banknoteStorage.getBanknoteCount();
				mac.checkoutStation.banknoteStorage.accept(new Banknote(5, cur));
				if (val < mac.checkoutStation.banknoteStorage.getCapacity() - mac.checkoutStation.banknoteStorage.getBanknoteCount()) {
					fail("Loop may never terminate!");
				}
			}
		} catch (Exception e) {
			fail("Cannot deal with full storage");
		}
		mac.checkoutStation.banknoteStorage.unload();
	}
	
	@Test
	public void addBanknote() {
		int val = lis.getStoredBanknoteCount();
		try {
			mac.checkoutStation.banknoteStorage.accept(new Banknote(5, cur));
		} catch (Exception e) {
			fail("Inserting a banknote threw an error!");
		}
		int next = lis.getStoredBanknoteCount();
		assertEquals(val+1,next);
		mac.checkoutStation.banknoteStorage.unload();
	}
	
	@Test
	public void loadUnloadNotes() {
		try {
			mac.checkoutStation.banknoteStorage.load(new Banknote(5, cur));
		} catch (Exception e) {
			fail("Could not load a banknote");
		}
		mac.checkoutStation.banknoteStorage.unload();
	}
	
	
	@After
	public void removeMachine() {
		mac = null;
		lis = null;
	}

}
