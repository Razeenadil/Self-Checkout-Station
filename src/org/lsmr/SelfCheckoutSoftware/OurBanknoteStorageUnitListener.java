package org.lsmr.SelfCheckoutSoftware;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BanknoteStorageUnit;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.BanknoteStorageUnitListener;

public class OurBanknoteStorageUnitListener implements BanknoteStorageUnitListener {

	private int storedBanknoteCount = 0;
	
	
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
	 * Announces that the indicated banknote storage unit is full of banknotes.
	 * 
	 * @param unit
	 *            The storage unit where the event occurred.
	 */
	@Override
	public void banknotesFull(BanknoteStorageUnit unit) {
		
		
	}

	/**
	 * Announces that a banknote has been added to the indicated storage unit.
	 * 
	 * Also counts the number of banknotes in the storage unit
	 * 
	 * @param unit
	 *            The storage unit where the event occurred.
	 */
	@Override
	public void banknoteAdded(BanknoteStorageUnit unit) {
		/**
		 * Adds a banknote to this instance's OurBanknoteStorageUnitListener.storedBanknoteCount
		 */

		this.storedBanknoteCount ++;
		
		
	}

	/**
	 * Announces that the indicated storage unit has been loaded with banknotes.
	 * Used to simulate direct, physical loading of the unit.
	 * 
	 * @param unit
	 *            The storage unit where the event occurred.
	 */
	@Override
	public void banknotesLoaded(BanknoteStorageUnit unit) {
		
		
	}

	/**
	 * Announces that the storage unit has been emptied of banknotes. Used to
	 * simulate direct, physical unloading of the unit.
	 * 
	 * @param unit
	 *            The storage unit where the event occurred.
	 */
	@Override
	public void banknotesUnloaded(BanknoteStorageUnit unit) {
		
		
	}

	/**
	 * This method gets the number of banknote in the banknote storage unit
	 * @return
	 * 		The number of banknote in the storage unit
	 */
	public int getStoredBanknoteCount() {
		/**
		 * Getter method for OurBanknoteStorageUnitListener.storedBanknoteCount
		 */
		return storedBanknoteCount;
	}
	
}
