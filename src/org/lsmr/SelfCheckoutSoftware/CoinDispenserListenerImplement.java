package org.lsmr.SelfCheckoutSoftware;

import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CoinDispenser;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.CoinDispenserListener;

public class CoinDispenserListenerImplement implements CoinDispenserListener{

	private boolean empty, emitted;
	private int amountEmitted;
	private boolean isFull;
	
	
	/**
	 * Constructor of CoinDispenserListenerImplement
	 * Initializes the global variables
	 */
	public CoinDispenserListenerImplement() {
		this.empty = true;
		this.resetEmitted();
		this.amountEmitted = 0;
		this.isFull = false;
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
	 * Announces that the indicated coin dispenser is full of coins.
	 * 
	 * @param dispenser
	 *             The dispenser where the event occurred.
	 */
	@Override
	public void coinsFull(CoinDispenser dispenser) {
		
		isFull = true;
	}

	
	/**
	 * Announces that the indicated coin dispenser is empty of coins.
	 * 
	 * @param dispenser
	 *             The dispenser where the event occurred.
	 */
	@Override
	public void coinsEmpty(CoinDispenser dispenser) {
		this.empty = true;
		isFull = false;
	}

	
	/**
	 * Announces that the indicated coin has been added to the indicated coin dispenser.
	 * 
	 * @param dispenser
	 *             The dispenser where the event occurred.
	 * @param coin
	 *             The coin that was added.
	 */
	@Override
	public void coinAdded(CoinDispenser dispenser, Coin coin) {
		empty = false;
		
	}

	
	/**
	 * Announces that the indicated coin has been emitted from the coin dispenser.
	 * Also increments the amount of coin emitted
	 * 
	 * @param dispenser
	 *             The dispenser where the event occurred.
	 * @param coin
	 *             The coin that was removed.
	 */
	@Override
	public void coinRemoved(CoinDispenser dispenser, Coin coin) {
		this.emitted = true;
		this.amountEmitted += 1;
		isFull = false;
		
	}

	/**
	 * Announces that the indicated sequence of coins has been added to the
	 * indicated coin dispenser. Used to simulate direct, physical loading of the dispenser.
	 * 
	 * @param dispenser
	 *              The dispenser where the event occurred.
	 * @param coins
	 *              The coins that were loaded.
	 */
	@Override
	public void coinsLoaded(CoinDispenser dispenser, Coin... coins) {
		empty = false;
		
	}

	/**
	 * Announces that the indicated sequence of coins has been removed to the
	 * indicated coin dispenser. Used to simulate direct, physical unloading of the dispenser.
	 * 
	 * @param dispenser
	 *              The dispenser where the event occurred.
	 * @param coins
	 *              The coins that were unloaded.
	 */
	@Override
	public void coinsUnloaded(CoinDispenser dispenser, Coin... coins) {
		isFull = false;
		empty = true;
		
	}

	/**
	 * This method returns a boolean value that indicates if the coin dispenser is empty or not
	 * @return
	 * 		false- if the coin dispenser is not empty
	 * 		true - if the coin dispernser is empty
	 */
	public boolean isEmpty() {
		return this.empty;
	}

	/**
	 * This method returns a boolean value that indicates if a coin is emitted from the coin dispenser
	 * @return
	 * 		true- if the coin is emitted from the machine
	 * 		false - if not
	 */
	public boolean isEmitted() {
		return this.emitted;
	}

	/**
	 * This method resets the value of the emitted variable to false
	 */
	public void resetEmitted() {
		this.emitted = false;
	}

	/**
	 * This method gets the number of coins emitted from the coin dispenser
	 * @return
	 * 		The number of coins emitted from the coin dispenser
	 */
	public int getAmountEmitted() {
		return this.amountEmitted;
	}
	
	/**
	 * This method returns a boolean value that indicates if the coin dispenser is full or not
	 * @return
	 * 		true - if the coin dispenser is full
	 * 		false - if not
	 */
	public boolean isFull() {
		return this.isFull;
	}
	
}
