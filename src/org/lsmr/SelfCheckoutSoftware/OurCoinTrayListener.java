package org.lsmr.SelfCheckoutSoftware;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CoinTray;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.CoinTrayListener;

public class OurCoinTrayListener implements CoinTrayListener {

	private int ejectedCoinCount = 0;
	
	
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
	 * This method counts the number of coin added in the tray
	 */
	@Override
	public void coinAdded(CoinTray tray) {
		/**
		 * Increments OurCoinTrayListener.ejectedCoinCount when a coin is added
		 */
		
		this.ejectedCoinCount ++;
		
		
	}

	/**
	 * This method gets the number of coin in the tray
	 * @return
	 */
	public int getEjectedCoinCount() {
		/**
		 * Getter method for OurCoinTrayListener.ejectedCoinCount
		 */
		return ejectedCoinCount;
	}

}
