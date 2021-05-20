package org.lsmr.SelfCheckoutSoftware;

import java.math.BigDecimal;
import java.util.Currency;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BanknoteValidator;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.BanknoteValidatorListener;

public class OurBanknoteValidatorListener implements BanknoteValidatorListener {

	private int validBanknoteCount = 0;
	private int invalidBanknoteCount = 0;
	private SelfCheckoutMachine machine = null;
	
	
	/**
	 * Constructor of OurBanknoteValidatorListener
	 * @param parent
	 * 		The selfcheckout machine
	 */
	public OurBanknoteValidatorListener(SelfCheckoutMachine parent) {
		/**
		 * Sets the provided SelfCheckoutMachine to this instance's BanknoteValidatorListener.machine
		 */
		machine = parent;
	}
	
	/**
	 * Announces that the indicated device has been enabled.
	 * 
	 * @param device
	 *                 The device that has been enabled.
	 */
	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
		// Not needed for this iteration
		
	}

	/**
	 * Announces that the indicated device has been disabled.
	 * 
	 * @param device
	 *                 The device that has been enabled.
	 */
	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
		// Not needed for this iteration
		
	}

	/**
	 * An event announcing that the indicated banknote has been detected and
	 * determined to be valid.
	 * 
	 * Also counts the number of valid banknote and adds the value of the banknote into the machine 
	 * 
	 * @param validator
	 *            The device on which the event occurred.
	 * @param currency
	 *            The kind of currency of the inserted banknote.
	 * @param value
	 *            The value of the inserted banknote.
	 */
	@Override
	public void validBanknoteDetected(BanknoteValidator validator, Currency currency, int value) {
		/**
		 * Increments this instance's BanknoteValidatorListener.validBanknoteCount and add's value to machine.MoneyPutIntoMachine
		 * when a valid Banknote is detected in the validator
		 */
		
		machine.MoneyPutIntoMachine = machine.MoneyPutIntoMachine.add(BigDecimal.valueOf(value));
		
		this.validBanknoteCount++;
		//System.out.println("A valid banknote has been detected. Total valid banknote count: " + validBanknoteCount);
		
	}

	
	/**
	 * An event announcing that the indicated banknote has been detected and
	 * determined to be invalid.
	 * 
	 * Also counts the number of invalid banknote inserted into the machine
	 * 
	 * @param validator
	 *            The device on which the event occurred.
	 */
	@Override
	public void invalidBanknoteDetected(BanknoteValidator validator) {
		/**
		 * Increments this instance's BanknoteValidatorListener.invalidBanknoteCount when an invalid Banknote is detected
		 */
		
		this.invalidBanknoteCount ++;
		//System.out.println("An invalid banknote has been detected. Total invalid banknote count: " + invalidBanknoteCount);
		
	}

	/**
	 * This method gets the number of valid banknote inserted
	 * @return
	 * 		The number of valid banknote
	 */
	public int getValidBanknoteCount() {
		/**
		 * Getter method for this instance's BanknoteValidatorListener.validBanknoteCount
		 */
		return validBanknoteCount;
	}

	
	/**
	 * This method gets the number of invalid banknote inserted
	 * @return
	 * 		The number of invalid banknote
	 */
	public int getInvalidBanknoteCount() {
		/**
		 * Getter method for this instance's BanknoteValidatorListener.validBanknoteCount
		 */
		return invalidBanknoteCount;
	}

}
