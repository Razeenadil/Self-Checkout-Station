package org.lsmr.SelfCheckoutSoftware;

import java.util.ArrayList;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BanknoteSlot;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.BanknoteSlotListener;

public class OurBanknoteSlotListener implements BanknoteSlotListener {

	private int insertedBanknoteCount;
	private int ejectedBanknoteCount;
	
	private ArrayList<Integer> banknoteEmitQueue; //A list representing a sequence of banknotes to be given as change
	private SelfCheckoutMachine sm;
	private SelfCheckoutStation hardware;
	
	
	/**
	 * Constructor of OurBanknoteSlotListener
	 * Initializes the global variables
	 */
	public OurBanknoteSlotListener() {
		this.insertedBanknoteCount = 0;
		this.ejectedBanknoteCount = 0;
		this.banknoteEmitQueue = null;
		this.sm = null;
		this.hardware = null;
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
	 * An event announcing that a banknote has been inserted.
	 * 
	 * Also counts the number of banknote inserted into the machine
	 * 
	 * @param slot
	 *            The device on which the event occurred.
	 */
	@Override
	public void banknoteInserted(BanknoteSlot slot) {

		this.insertedBanknoteCount ++;
		//System.out.println("A banknote has been inserted. Total inserted banknote count: " + insertedBanknoteCount);
		
	}

	/**
	 * An event announcing that a banknote has been returned to the user, dangling
	 * from the slot.
	 * 
	 * Also counts the number of banknote returned to the user
	 * 
	 * @param slot
	 *            The device on which the event occurred.
	 */
	@Override
	public void banknoteEjected(BanknoteSlot slot) {

		this.ejectedBanknoteCount ++;
		//System.out.println("A banknote has been ejected. Total ejected banknote count: " + ejectedBanknoteCount);
		
	}

	/**
	 * This causes the next banknote in the change queue to be emitted when the customer grabs a dangling banknote from the slot
	 * @author Austin Shawaga UCID 30086103
	 */
	@Override
	public void banknoteRemoved(BanknoteSlot slot) {
				
		//If there is a valid hardware/banknoteEmitQueue and there is something within the queue
		if ((this.sm != null && this.hardware != null) && (this.banknoteEmitQueue != null && this.banknoteEmitQueue.size() > 0)) { //short circuiting
			int valueFirstEmitted;
			valueFirstEmitted = this.banknoteEmitQueue.get(0); //Gets value to be emitted
				
			this.banknoteEmitQueue.remove(0); //removes soon to be emitted value from queue
				
			sm.emitBanknote(this.hardware,valueFirstEmitted); //Emmits value
				
				
		}
		
	}

	/**
	 * This method gets the number of banknotes inserted into the machine
	 * @return
	 * 		The number of banknote inserted
	 */
	public int getInsertedBanknoteCount() {
		return insertedBanknoteCount;
	}

	/**
	 * This method gets the number of banknotes returned to the user
	 * @return
	 * 		The number of banknotes returned to the user
	 */
	public int getEjectedBanknoteCount() {
		return ejectedBanknoteCount;
	}
	
	
	/**
	 * This method sets an array list that represents a sequence of banknotes to be given as change
	 * @param q
	 * 		The array list of integer - seqeunce of banknote to be given as change
	 */
	public void setBanknoteQ(ArrayList<Integer> q) {
		this.banknoteEmitQueue = q;
	}

	/**
	 * This method sets the selfcheckout station
	 * @param h
	 * 		The selfcheckout station
	 */
	public void setHardware(SelfCheckoutStation h) {
		this.hardware = h;
	}
	
	/**
	 * This method sets the selfcheckout machine
	 * @param sm1
	 * 		The selfcheckout machine
	 */
	public void setMachine(SelfCheckoutMachine sm1) {
		this.sm = sm1;
		this.hardware = sm1.checkoutStation;
	}
	
}
