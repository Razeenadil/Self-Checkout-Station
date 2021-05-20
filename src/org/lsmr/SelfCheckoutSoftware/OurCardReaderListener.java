package org.lsmr.SelfCheckoutSoftware;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.CardReaderListener;


public class OurCardReaderListener implements CardReaderListener {

	private SelfCheckoutMachine machine = null;
	public String memberNumber = "";
	public ArrayList<String> validMemberNumbers = new ArrayList<>();
	
	// Signals the machine that transaction is completed
	private boolean isTransactionCompleteOrFailed = false;
	
	// Signals the machine a valid membership card is read
	private boolean isMembershipCardValid = false;
	

	/**
	 * Constructor of OurCardReaderListener
	 * @param parent
	 * 			The selfcheckout machine
	 */
	public OurCardReaderListener(SelfCheckoutMachine parent) {
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
	 * Announces that a card has been inserted in the indicated card reader.
	 * 
	 * @param reader
	 *            The reader where the event occurred.
	 */
	@Override
	public void cardInserted(CardReader reader) {
		
		
	}

	/**
	 * Announces that a card has been removed from the indicated card reader.
	 * 
	 * @param reader
	 *            The reader where the event occurred.
	 */
	@Override
	public void cardRemoved(CardReader reader) {
		
		
	}

	/**
	 * Announces that a (tap-enabled) card has been tapped on the indicated card
	 * reader.
	 * 
	 * @param reader
	 *            The reader where the event occurred.
	 */
	@Override
	public void cardTapped(CardReader reader) {
	
	}

	/**
	 * Announces that a card has swiped on the indicated card reader.
	 * 
	 * @param reader
	 *            The reader where the event occurred.
	 */
	@Override
	public void cardSwiped(CardReader reader) {
		
		
	}

	
	/**
	 * Announces that the data has been read from a card.
	 * This method lets the customer make the payment using cards (debit card and credit card)
	 * Also reads the membership card of the customer
	 * 
	 * @param reader
	 *            The reader where the event occurred.
	 * @param data
	 *            The data that was read. Note that this data may be corrupted.
	 */
	@Override
	public void cardDataRead(CardReader reader, CardData data) {
		
		if (data.getType().equals("Membership")) {
			
			readMembershipCard(data);
			
			// Notify the transaction is done.
			this.isTransactionCompleteOrFailed = true;
			
			return;
		}
		
		// Convert the price from double to BigDecimal
		BigDecimal priceBD = BigDecimal.valueOf(machine.totalPrice);
		
		// Hold money at an amount of totalPrice on card
		int holdNum = machine.ciSelected.authorizeHold(data.getNumber(), priceBD);
		
		// Complete the transaction
		boolean isPosted = machine.ciSelected.postTransaction(data.getNumber(), holdNum, priceBD);
		
		// Check if the transaction is actually completed
		if (isPosted == true) {
			// System.out.println("Transaction completed");
			
			// Clear all the balance
			machine.setBalance(0.0);
			
			// Notify the transaction is done.
			this.isTransactionCompleteOrFailed = true;
			
		// If the transaction is not completed, release the hold
		} else if (isPosted == false) {
			
			// Release the hold
			boolean isReleased = machine.ciSelected.releaseHold(data.getNumber(), holdNum);
			
			if (isReleased) {
				// System.out.println("Transaction failed. Hold has been released.");
				
				// Notify the transaction is done.
				this.isTransactionCompleteOrFailed = true;
			}
		}
	}
	
	/**
	 * This method checks if the given string list contains the given string
	 * @param list		
	 * 		Array list of strings
	 * @param val
	 * 		String value
	 * @return
	 * 		true- if the string list contains the string
	 * 		false - if does not contain
	 */
	public boolean contains(ArrayList<String> list, String val) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equals(val)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method reads the data of a membership card
	 * @param data
	 * 		The CardData that contains the information of the membership card
	 */
	private void readMembershipCard(CardData data) {
		if (contains(this.validMemberNumbers,data.getNumber())) {
			this.memberNumber = data.getNumber();
			this.isMembershipCardValid = true;
		} else {
			// bad card
		}
	}

	/**
	 * This method returns a boolean value that indicates if the transaction is failed or completed
	 * @return
	 * 		true - if the transaction is completed
	 * 		false - if the transaction is failed
	 */
	public boolean isTransactionCompleteOrFailed() {
		return isTransactionCompleteOrFailed;
	}

	/**
	 * This method returns an array list of valid membership card numbers
	 * @return
	 * 		An array list of valid membership card numbers
	 */
	public ArrayList<String> getValidMemberNumbers() {
		return validMemberNumbers;
	}

	/**
	 * This method returns the membership card number
	 * @return
	 * 		The membership card number
	 */
	public String getMemberNumber() {
		return memberNumber;
	}

	/**
	 * This method returns a boolean value that indicates if the membership card is valid or not
	 * @return
	 * 		true - if the membership card is valid
	 * 		false - if the membership cars is invalid
	 */
	public boolean isMembershipCardValid() {
		return isMembershipCardValid;
	}
	
}
