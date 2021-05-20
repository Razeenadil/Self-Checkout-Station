package org.lsmr.SelfCheckoutSoftware;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BanknoteDispenser;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.BanknoteDispenserListener;

public class BanknoteDispenserListenerImplement implements BanknoteDispenserListener {

	private boolean empty, emitted;
	private int amountEmitted;
	
	
	/**
	 * Constructor of BanknoteDispenserListenerImplement
	 * Initializes the global variables
	 */
	public BanknoteDispenserListenerImplement() {
		this.empty = true;
		this.emitted = false;
		this.amountEmitted = 0;
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
	 * Announces that the indicated banknote dispenser is full of banknotes.
	 * 
	 * @param dispenser
	 *             The dispenser where the event occurred.
	 */
	@Override
	public void banknotesFull(BanknoteDispenser dispenser) {
		//Not currently used by hardware
	}

	/**
	 * Announces that the indicated banknote dispenser is empty of banknotes.
	 * 
	 * Also increments the number of banknotes emitted from the banknote dispenser 
	 * 
	 * @param dispenser
	 *             The dispenser where the event occurred.
	 */
	@Override
	public void banknotesEmpty(BanknoteDispenser dispenser) {
		this.emitted = true; //Will occur if the last banknote in queue was emitted
		this.empty = true;
		this.amountEmitted += 1;
		
	}

	/**
	 * Announces that the indicated banknote has been added to the indicated banknote dispenser.
	 * 
	 * @param dispenser
	 *             The dispenser where the event occurred.
	 * @param banknote
	 *             The banknote that was added.
	 */
	@Override
	public void banknoteAdded(BanknoteDispenser dispenser, Banknote banknote) {
		//Not currently used by hardware
		
	}

	/**
	 * Announces that the indicated banknote has been added to the indicated banknote dispenser.
	 * 
	 * Also increments the number of of banknotes emitted from the banknote dispenser
	 * 
	 * @param dispenser
	 *             The dispenser where the event occurred.
	 * @param banknote
	 *             The banknote that was removed.
	 */
	@Override
	public void banknoteRemoved(BanknoteDispenser dispenser, Banknote banknote) {
		this.emitted = true;
		this.amountEmitted += 1;
		
	}

	/**
	 * Announces that the indicated sequence of banknotes has been added to the
	 * indicated banknote dispenser. Used to simulate direct, physical loading of the dispenser.
	 * 
	 * Also declares that the banknote dispenser is not empty
	 * 
	 * @param dispenser
	 *              The dispenser where the event occurred.
	 * @param banknotes
	 *              The banknotes that were loaded.
	 */
	@Override
	public void banknotesLoaded(BanknoteDispenser dispenser, Banknote... banknotes) {
		empty = false;
		
	}

	/**
	 * Announces that the indicated sequence of banknotes has been removed to the
	 * indicated banknote dispenser. Used to simulate direct, physical unloading of the dispenser.
	 * 
	 * Also declares that the banknote dispenser is empty
	 * 
	 * @param dispenser
	 *              The dispenser where the event occurred.
	 * @param banknotes
	 *              The banknotes that were unloaded.
	 */
	@Override
	public void banknotesUnloaded(BanknoteDispenser dispenser, Banknote... banknotes) {
		empty = true;
		
	}
	
	/**
	 * This method returns a boolean value that indicates that the banknote dispenser is empty or not
	 * 
	 * @return
	 * 		true - if the banknote dispenser is empty
	 * 		false - if its not
	 */
	public boolean isEmpty() {
		return this.empty;
	}

	/**
	 * This method returns a boolean value that indicates if the banknote is emitted from the banknote dispenser
	 * @return
	 * 		true - if the banknote is emitted
	 * 		false - if its not
	 */
	public boolean isEmitted() {
		return this.emitted;
	}
	
	/**
	 * This method resets the value of emitted variable to false
	 */
	public void resetEmitted() {
		this.emitted = false;
	}
	
	/**
	 * This method gets the number of banknotes emitted from the banknote dispenser
	 * @return
	 * 		The number of banknotes emitted from the banknote dispenser
	 */
	public int getAmountEmitted() {
		return this.amountEmitted;
	}
	

}
