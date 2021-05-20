package org.lsmr.SelfCheckoutSoftware;

import java.math.BigDecimal;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CoinValidator;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.CoinValidatorListener;


public class OurCoinValidatorListener implements CoinValidatorListener {

	private int validCoinCount = 0;
	private int invalidCoinCount = 0;
	private SelfCheckoutMachine machine = null;
	
	
	/**
	 * Constructor for OurCoinValidatorListener
	 * @param parent
	 * 		The selfcheckout machine
	 */
	public OurCoinValidatorListener(SelfCheckoutMachine parent) {
		/**
		 * Constructs a OurCoinValidatorListener and assigns parent to OurCoinValidatorListener.machine
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

		
	}

	/**
	 * Announces that the indicated device has been disabled.
	 * 
	 * @param device
	 *                 The device that has been enabled.
	 */
	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
		
		
	}

	/**
	 * This method counts the number of valid coin and adds the value of the coin into the machine
	 */
	@Override
	public void validCoinDetected(CoinValidator validator, BigDecimal value) {
		/**
		 * Increments OurCoinValidatorListener.validCoinCount and add's value to 
		 * OurCoinValidatorListener.machine.MoneyPutIntoMachine when a valid Coin is detected
		 */
		
		machine.MoneyPutIntoMachine = machine.MoneyPutIntoMachine.add(value);
		
		this.validCoinCount ++;
		
	}

	/**
	 * This method counts the number of invalid coins
	 */
	@Override
	public void invalidCoinDetected(CoinValidator validator) {
		/**
		 * Increments OurCoinValidatorListener.invalidCoinCount when an invalid coin is detected
		 */
		
		this.invalidCoinCount ++;
		
		
	}

	/**
	 * Getter method for OurCoinValidatorListener.validCoinCount
	 */
	public int getValidCoinCount() {
	
		return validCoinCount;
	}

	
	/**
	 * Getter method for OurCoinValidatorListener.invalidCoinCount
	 */
	public int getInvalidCoinCount() {
		
		return invalidCoinCount;
	}

}
