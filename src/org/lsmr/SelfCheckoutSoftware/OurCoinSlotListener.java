package org.lsmr.SelfCheckoutSoftware;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CoinSlot;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.CoinSlotListener;

public class OurCoinSlotListener implements CoinSlotListener {
	
	private int insertedCoinCount = 0;

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
	 * An event announcing that a coin has been inserted.
	 * Also counts the number of coins inserted in the coin slot
	 * @param slot
	 *             The device on which the event occurred.
	 */
	@Override
	public void coinInserted(CoinSlot slot) {
		/**
		 * Increments this instance's OurCoinSlotListener.insertedCoinCount when a coin is inserted
		 */

		this.insertedCoinCount ++;
		
		
	}

	/**
	 * Gets the number of coin inserted to the coin slot
	 * @return
	 * 		The number of coins inserted to the coin slot
	 */
	public int getInsertedCoinCount() {
		/**
		 * Getter method for OurCoinSlotListener.insertedCoinCount
		 */
		return insertedCoinCount;
	}

}
