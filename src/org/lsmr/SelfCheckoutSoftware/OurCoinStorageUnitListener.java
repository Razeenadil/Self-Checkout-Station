package org.lsmr.SelfCheckoutSoftware;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CoinStorageUnit;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.CoinStorageUnitListener;

public class OurCoinStorageUnitListener implements CoinStorageUnitListener {

	private int storedCoinCount = 0;
	
	
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
	 * Announces that the indicated coin storage unit is full of coins.
	 * 
	 * @param unit
	 *            The storage unit where the event occurred.
	 */
	@Override
	public void coinsFull(CoinStorageUnit unit) {
		
		
	}

	/**
	 * Announces that a coin has been added to the indicated storage unit.
	 * Also counts the number of coins in the coin storage unit
	 * 
	 * @param unit
	 *            The storage unit where the event occurred.
	 */
	@Override
	public void coinAdded(CoinStorageUnit unit) {
		/**
		 * Increments OurCoinStorageUnitListener.storedCoinCount when a coin is added
		 */

		this.storedCoinCount ++;
		
		
	}

	/**
	 * Announces that the indicated storage unit has been loaded with coins.
	 * Used to simulate direct, physical loading of the unit.
	 * 
	 * @param unit
	 *            The storage unit where the event occurred.
	 */
	@Override
	public void coinsLoaded(CoinStorageUnit unit) {
		
		
	}

	/**
	 * Announces that the storage unit has been emptied of coins. Used to
	 * simulate direct, physical unloading of the unit.
	 * 
	 * @param unit
	 *            The storage unit where the event occurred.
	 */
	@Override
	public void coinsUnloaded(CoinStorageUnit unit) {
		
		
	}

	/**
	 * Gets the number of coins in the coin storage unit
	 * @return
	 * 		The number of coins in the coin storage unit
	 */
	public int getStoredCoinCount() {
		return storedCoinCount;
	}
	

}
