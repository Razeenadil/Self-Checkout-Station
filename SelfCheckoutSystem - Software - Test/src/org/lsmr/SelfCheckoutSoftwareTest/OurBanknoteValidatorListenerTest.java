package org.lsmr.SelfCheckoutSoftwareTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.SelfCheckoutSoftware.SelfCheckoutMachine;
import org.lsmr.selfcheckout.Banknote;

public class OurBanknoteValidatorListenerTest {
	
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
			mac.checkoutStation.banknoteValidator.disable();
			mac.checkoutStation.banknoteValidator.enable();
		} catch (Exception e) {
			fail("disabling and enabling throws an error");
		}
	}
	
	// Hopefully bug-free
	// NOT A TEST, used by rejectedNote to find a note that will be rejected
	public boolean contains(int[] list, int val) {
		for (int i = 0; i < list.length; i++) {
			if (list[i] == val) {
				return true;
			}
		}
		return false;
	}
	
	@Test
	public void rejectedNote() {
		// Try for an invalid denomination. Since the list is finite, this will eventually work.
		int denom = 1;
		while (contains(SelfCheckoutMachine.ACCEPTEDBANKNOTDENOMINATIONS, denom)) {
			denom += 1;
		}
		
		int badBefore = mac.bValidatorListener.getInvalidBanknoteCount();
		int goodBefore = mac.bValidatorListener.getValidBanknoteCount();
		BigDecimal valBefore = mac.MoneyPutIntoMachine;
		try {
			mac.checkoutStation.banknoteValidator.accept(new Banknote(denom, cur));
		} catch (Exception e) {
			fail("Could not insert bad banknote");
		}
		int badAfter = mac.bValidatorListener.getInvalidBanknoteCount();
		int goodAfter = mac.bValidatorListener.getValidBanknoteCount();
		BigDecimal valAfter = mac.MoneyPutIntoMachine;
		assertEquals("Failed to note that bad note was added",badBefore+1, badAfter);
		assertEquals("Treated bad note as good",goodBefore, goodAfter);
		assertEquals("Submitting bad note changed result",valBefore, valAfter);
	}
	
	@Test
	public void acceptedNote() {
		
		int denom = SelfCheckoutMachine.ACCEPTEDBANKNOTDENOMINATIONS[0];
		
		int badBefore = mac.bValidatorListener.getInvalidBanknoteCount();
		int goodBefore = mac.bValidatorListener.getValidBanknoteCount();
		BigDecimal valBefore = mac.MoneyPutIntoMachine;
		try {
			mac.checkoutStation.banknoteValidator.accept(new Banknote(denom, cur));
		} catch (Exception e) {
			fail("Could not insert good banknote");
		}
		int badAfter = mac.bValidatorListener.getInvalidBanknoteCount();
		int goodAfter = mac.bValidatorListener.getValidBanknoteCount();
		BigDecimal valAfter = mac.MoneyPutIntoMachine;
		assertEquals("Thought bad note was added",badBefore, badAfter);
		assertEquals("Didn't treat good note as good",goodBefore+1, goodAfter);
		
		assertEquals("Submitting good note didn't change result",valBefore.add(BigDecimal.valueOf(denom)), valAfter);
		
	}
	
	
	@After
	public void removeMachine() {
		mac = null;
	}

}
