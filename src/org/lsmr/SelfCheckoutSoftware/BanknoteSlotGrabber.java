package org.lsmr.SelfCheckoutSoftware;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BanknoteSlot;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.BanknoteSlotListener;

/**
 * This listener grabs a dangling Banknote every time it is ejected from a Banknote slot
 * @author Austin Shawaga UCID 30086103
 */
public class BanknoteSlotGrabber implements BanknoteSlotListener{

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
		//not used
		
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
		//not used
		
	}

	@Override
	public void banknoteInserted(BanknoteSlot slot) {
		//not used
		
	}

	/**
	 * Everytime a banknote is ejected from the slot, simulate a user grabbing it
	 */
	@Override
	public void banknoteEjected(BanknoteSlot slot) {
		slot.removeDanglingBanknote();	
	}

	@Override
	public void banknoteRemoved(BanknoteSlot slot) {
		//not used
	
		
	}
	
}
