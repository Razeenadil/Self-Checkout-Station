package org.lsmr.SelfCheckoutSoftwareTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.SelfCheckoutSoftware.SelfCheckoutMachine;
import org.lsmr.selfcheckout.Coin;

public class OurCoinValidatorListenerTest {
	
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
			mac.checkoutStation.coinValidator.disable();
			mac.checkoutStation.coinValidator.enable();
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
		while (SelfCheckoutMachine.ACCEPTEDCOINDENOMINATIONS.contains(new BigDecimal(denom))) {
			denom += 1;
		}
		
		int badBefore = mac.cValidatorListener.getInvalidCoinCount();
		int goodBefore = mac.cValidatorListener.getValidCoinCount();
		BigDecimal valBefore = mac.MoneyPutIntoMachine;
		try {
			mac.checkoutStation.coinValidator.accept(new Coin(new BigDecimal(denom), cur));
		} catch (Exception e) {
			fail("Could not insert bad banknote");
		}
		int badAfter = mac.cValidatorListener.getInvalidCoinCount();
		int goodAfter = mac.cValidatorListener.getValidCoinCount();
		BigDecimal valAfter = mac.MoneyPutIntoMachine;
		assertEquals("Failed to note that bad note was added",badBefore+1, badAfter);
		assertEquals("Treated bad note as good",goodBefore, goodAfter);
		assertEquals("Submitting bad note changed result",valBefore, valAfter);
	}
	
	@Test
	public void acceptedCoin() {
		
		BigDecimal denom = SelfCheckoutMachine.ACCEPTEDCOINDENOMINATIONS.get(0);
		
		int badBefore = mac.cValidatorListener.getInvalidCoinCount();
		int goodBefore = mac.cValidatorListener.getValidCoinCount();
		BigDecimal valBefore = mac.MoneyPutIntoMachine;
		try {
			mac.checkoutStation.coinValidator.accept(new Coin(denom, cur));
		} catch (Exception e) {
			fail("Could not insert good banknote");
		}
		int badAfter = mac.cValidatorListener.getInvalidCoinCount();
		int goodAfter = mac.cValidatorListener.getValidCoinCount();
		BigDecimal valAfter = mac.MoneyPutIntoMachine;
		assertEquals("Thought bad note was added",badBefore, badAfter);
		assertEquals("Didn't treat good note as good",goodBefore+1, goodAfter);
		
		assertEquals("Submitting good note didn't change result",valBefore.add(denom), valAfter);
		
	}
	
	
	@After
	public void removeMachine() {
		mac = null;
	}

}
