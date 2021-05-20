package org.lsmr.SelfCheckoutSoftware;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.Math;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.PLUCodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.BanknoteDispenser;
import org.lsmr.selfcheckout.devices.CoinDispenser;
import org.lsmr.selfcheckout.devices.CoinStorageUnit;
import org.lsmr.selfcheckout.devices.CoinTray;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.devices.listeners.BanknoteDispenserListener;
import org.lsmr.selfcheckout.external.CardIssuer;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.products.Product;


public class SelfCheckoutMachine {
	
	// Global constants
	
	
	// Whether or not there are items still being added
	public boolean currently_scanning;
	public boolean waitingToPlaceItem = false;
	public double expectedWeight = 0.0;

	// The payment stats
	public BigDecimal MoneyPutIntoMachine = BigDecimal.ZERO;
	public int costOfItems = 0;
	
	// Weight limits and sensitivity for ElectronicScale(s)
	public static int WEIGHTLIMIT = 100;
	public static int SENSITIVITY = 1;
	
	// Currency Variables
	public static Currency ACCEPTEDCURRENCY = Currency.getInstance("CAD");
	public static List<BigDecimal> ACCEPTEDCOINDENOMINATIONS;
	public static int ACCEPTEDCOINCAPACITY = 1000;
	public static int REJECTEDCOINCAPACITY = 1000;
	
	// Map that represents the cart of the current transaction. If the key is an instance of BarcodedProduct, the 
	// value is a double representing how many times that product was scanned. 
	//	If it is an instance of PLUCodedProduct, the double represents the weight in grams
	public Map<Product, Double> cart;
	
	// Listener for banknotes
	public BanknoteDispenserListener banknoteDispenserListener;
	
	// Barcode scanner and listener
	public OurScannerListener scanListener;
	
	// Scale scanner and listener
	public OurScaleListener scaleListener;
	
	// Coin-related scanners and listeners
	public OurCoinSlotListener cSlotListener;
	public CoinTray RejectedCoinReturnTray;
	
	public OurCoinValidatorListener cValidatorListener;
	
	public OurCoinTrayListener cTrayListener;
	
	public CoinStorageUnit AcceptedCoinStorage;
	public OurCoinStorageUnitListener cStorageUnitListener;
	
	// Banknote-related scanners and listeners
	public OurBanknoteSlotListener bSlotListener;
	
	public OurBanknoteValidatorListener bValidatorListener;
	
	public OurBanknoteStorageUnitListener bStorageUnitListener;
	
	public static boolean ISINVERTED = false;
	public static int[] ACCEPTEDBANKNOTDENOMINATIONS = {5, 10, 20, 50, 100};
	
	// CardReader listener
	public OurCardReaderListener cReaderListener;
	
	//Recipt listener
	public OurReceiptPrinterListener RPListener;
	
	
	// Selected CardIssuer based on the customer's card
	public CardIssuer ciSelected;
	
	// Total and balance
	public double totalPrice;
	public double balance;
	
	// Membership number
	public String membershipNumber;
	
	
	//For attendant control console
	public boolean accessGranted;
	public boolean loggedIn;
		
	//Number of plastic bags used 
	public int numberOfPlasticBags;
	
	// Instantiating the main SelfCheckoutStation for initializing hardware
	public SelfCheckoutStation checkoutStation;
	
	public SelfCheckoutMachine() {
		/**
		 * Constructor used to initialize and connect devices
		 */
		
		//Populates Database
		
		// Setup PLU test attributes
		Map<Product, Integer> inv = ProductDatabases.INVENTORY;
		Map<PriceLookupCode, PLUCodedProduct> pluProductDb;
	    PriceLookupCode bannanaPLU;
	    PriceLookupCode applePLU;
	    PLUCodedProduct bannanaPLUProduct;
	    PLUCodedProduct applePLUProduct;
	    PLUCodedItem  bannanaPLUItem;
	    PLUCodedItem  applePLUItem;
	    
	    // Setup PLU test attributes
	    pluProductDb = ProductDatabases.PLU_PRODUCT_DATABASE;
	    pluProductDb.clear(); // Ensure only newly entered items are in the pluProductDb
	    bannanaPLU = new PriceLookupCode("4011");
	    applePLU = new PriceLookupCode("4128");
	    bannanaPLUProduct = new PLUCodedProduct(bannanaPLU, "Bannana", new BigDecimal(2500.00));
	    applePLUProduct = new PLUCodedProduct(applePLU, "Apple", new BigDecimal(3500.00));
	    
	    inv.put(applePLUProduct, 55);
	    inv.put(bannanaPLUProduct, 62);
	    
	    
	    bannanaPLUItem = new PLUCodedItem(bannanaPLU, 2.00);
	    applePLUItem = new PLUCodedItem(applePLU, 1.00);
	    
	    pluProductDb.put(applePLU, applePLUProduct);
	    pluProductDb.put(bannanaPLU, bannanaPLUProduct);
	        
	    // Setup Barcode test attributes
	    Barcode bC = new Barcode("567");
	    BarcodedItem bi1 = new BarcodedItem(bC, 15.00);
	    BarcodedProduct bp1 = new BarcodedProduct(bC, "Description", new BigDecimal(10.00));
	    inv.put(bp1, 22);
	    
	    Barcode bC2 = new Barcode("69");
	    BarcodedItem bi2 = new BarcodedItem(bC, 69.15);
	    BarcodedProduct bp2 = new BarcodedProduct(bC, "Description", new BigDecimal(5.00));
	    inv.put(bp2, 12);
	    
	    Map<Barcode, BarcodedProduct>  bcProductDb = ProductDatabases.BARCODED_PRODUCT_DATABASE;
	    bcProductDb.clear(); // Clear the productDb so it's only the new products in the db
	    bcProductDb.put(bC, bp1);
	    bcProductDb.put(bC2, bp2);
	    
	    
        
		// Set machine state to currently scanning
		currently_scanning = true;
		
		//Setting up accepted denominations
		ACCEPTEDCOINDENOMINATIONS = new ArrayList<BigDecimal>();
		ACCEPTEDCOINDENOMINATIONS.add(new BigDecimal("0.05"));
		ACCEPTEDCOINDENOMINATIONS.add(new BigDecimal("0.10"));
		ACCEPTEDCOINDENOMINATIONS.add(new BigDecimal("0.25"));
		ACCEPTEDCOINDENOMINATIONS.add(new BigDecimal("1.00"));
		ACCEPTEDCOINDENOMINATIONS.add(new BigDecimal("2.00"));
		
		// HACK to allow for instantiation of the SelfCheckoutStation
		BigDecimal[] COINDENOMINATIONSARRAY = new BigDecimal[ACCEPTEDCOINDENOMINATIONS.size()];
		COINDENOMINATIONSARRAY = ACCEPTEDCOINDENOMINATIONS.toArray(COINDENOMINATIONSARRAY);
		
		checkoutStation = new SelfCheckoutStation(ACCEPTEDCURRENCY, ACCEPTEDBANKNOTDENOMINATIONS, COINDENOMINATIONSARRAY , WEIGHTLIMIT,SENSITIVITY );
		
		// Setting up the Barcode Scanner and Listeners
		scanListener = new OurScannerListener(this);
		checkoutStation.mainScanner.register(scanListener);
		checkoutStation.handheldScanner.register(scanListener);
		
		// Setting up the Electronic Scale and Listener(s)
		scaleListener = new OurScaleListener(this);
		checkoutStation.scale.register(scaleListener);
		checkoutStation.baggingArea.register(scaleListener);
		
		
		// Setting up the CoinSlot
		cSlotListener = new OurCoinSlotListener();
		checkoutStation.coinSlot.register(cSlotListener);
		
		// CoinStorageUnit
		AcceptedCoinStorage = new CoinStorageUnit(ACCEPTEDCOINCAPACITY);
		cStorageUnitListener = new OurCoinStorageUnitListener();
		AcceptedCoinStorage.register(cStorageUnitListener);
		
		// CoinTray
		RejectedCoinReturnTray = checkoutStation.coinTray;
		cTrayListener = new OurCoinTrayListener();
		RejectedCoinReturnTray.register(cTrayListener);

		// CoinValidator
		
		cValidatorListener = new OurCoinValidatorListener(this);
		checkoutStation.coinValidator.register(cValidatorListener);
		
		// Setup the banknote slot 
		// BanknoteSlot
		
		bSlotListener = new OurBanknoteSlotListener();
		checkoutStation.banknoteInput.register(bSlotListener);
		
		// BanknoteStorageUnit
		bStorageUnitListener = new OurBanknoteStorageUnitListener();
		checkoutStation.banknoteStorage.register(bStorageUnitListener);
		
		// BanknoteValidator
		
		bValidatorListener = new OurBanknoteValidatorListener(this);
		checkoutStation.banknoteValidator.register(bValidatorListener);
		
		// CardReader
		cReaderListener = new OurCardReaderListener(this);
		checkoutStation.cardReader.register(cReaderListener);
		
		//Receipt Printer Listener
		RPListener = new OurReceiptPrinterListener();
		checkoutStation.printer.register(RPListener);
		
		
		
		
		// Cart HashMap
		cart = new HashMap<Product, Double>();
		
		// The touch-scren manager.
		// Not tracked anywhere, because it has no internal state.
		new OurTouchScreenManager(this);

	}
	
	public static void main(String[] args) {
		new SelfCheckoutMachine();
		while (true) {}
	}
	
	/**
	 * This method is used to scan a barcoded item/s
	 * @param item
	 * 		The barcoded item
	 */
	public void scanItem(BarcodedItem item) {
		/**
		 * The primary entrypoint for scanning an item
		 */
		// before scanning items, start scanning by calling method startScanning
		if (this.currently_scanning) {
			this.expectedWeight = item.getWeight();
			this.checkoutStation.mainScanner.scan(item);
		}
		
	}
	
	/**
	 * This method is used to finalize the transaction
	 *
	 * @return
	 * 		subtotal - it returns the subtotal that the customer needs to pay
	 * @throws SimulationException
	 */
	public double finalizeItems() throws SimulationException{
		/**
		 * Takes currently scanned items and return the 
		 * subtotal of scanned item costs
		 */
		
		
		// Disable devices so no more items can be added
		this.currently_scanning = false;
	
		
		// Get and return the subtotal of all the items that have been scanned 
		
		double subtotal = getTransactionTotal();
		
		// Update fields, totalPrice and balance (Added by Tian on April 7)
		this.totalPrice = subtotal;
		this.balance = subtotal;
		
		return subtotal;
	}
	
	/**
	 * This method resets the list of scanned item and lets the customer to scan again
	 */
	public void resetMachine() {
		// Called after checkout and payment is completed
		this.currently_scanning = true;
		this.scanListener.resetList(); // Clear items from scanner listener
		this.cart.clear();
	}

	/**
	 * This method lets the customer to return adding items
	 */
	public void returnToAddingItems() {
		this.currently_scanning = true;
	}
	
	/**
	 * This method sets the balance 
	 * @param balance
	 * 		The remaining balance of the customer
	 */
	public void setBalance(double balance) {
		this.balance = balance;
	}
	
	/**
	 * This method returns the balance
	 * @return
	 * 		The remaining balance of the customer
	 */
	public double getBalance() {
		return balance;
	}

	/**
	 *  (Only called by payWithCoin and payWithBanknote, card payments do not need this,
	 *  as the balance is set to 0.0 when the card transaction is complete.)
	 *  When banknotes and coins are inserted, balance is changed after each insertion
	 */
	private void updateCoinBanknoteBalance() {
		
		// Update the field, balance
		this.balance = this.totalPrice - this.MoneyPutIntoMachine.doubleValue();
	}
	
	/**
	 * This method is used when the customer pays with coin
	 * @param coin - A coin used for payment
	 * @throws DisabledException
	 */
	public void payWithCoin(Coin coin) throws DisabledException {
		/**
		 * The primary entrypoint for paying with a coin
		 */
		this.checkoutStation.coinSlot.accept(coin);
		
		// Update balance (money owed by the customer)
		updateCoinBanknoteBalance();
		
	}
	
	/**
	 * This method is used when the cutomer pays with banknote
	 * @param banknote - A banknote used for payment
	 * @throws DisabledException
	 * @throws OverloadException
	 */
	public void payWithBanknote(Banknote banknote) throws DisabledException, OverloadException {
		/**
		 * The primary entrypoint for paying with a banknote
		 */
		this.checkoutStation.banknoteInput.accept(banknote);
		
		// Update balance (money owed by the customer)
		updateCoinBanknoteBalance();
	}
	
	/**
	 * This method is used when the customer pays with debit card
	 * @param debit - A debit card from the database of a CardIssuer
	 * @param tapSwipeInsert - One of "tap", "swipe" or "insert"
	 * @param pin - The passed debit card's PIN
	 * @param signiture - (Can be null) An image of signature on the back of the card
	 * @throws IOException 
	 */
	public void payWithDebit(Card debit, String tapSwipeInsert, String pin, BufferedImage signiture) throws IOException {
		
		// Update the selected CardIssuer
		this.ciSelected = new CardIssuer("Interac");
		
		// A dummy variable, since CardReader's methods return CardData
		CardData data;
		
		// Based on the decision of the customer, CardReader either reads the card via tap, swipe or insert
		if (tapSwipeInsert.compareToIgnoreCase("tap") == 0) {
			data = this.checkoutStation.cardReader.tap(debit);
		}else if (tapSwipeInsert.compareToIgnoreCase("swipe") == 0) {
			data = this.checkoutStation.cardReader.swipe(debit, signiture);
		}else if (tapSwipeInsert.compareToIgnoreCase("insert") == 0) {
			data = this.checkoutStation.cardReader.insert(debit, pin);
		}else {
			return;
		}
		
	}
	
	/**
	 * This method is used when the customer pays with credit card
	 * @param credit - A credit card from the database of a CardIssuer
	 * @param ci - ***ONE OF THE EXISTING credit card CardIssuer, ciMC, ciVisa or ciAmex
	 * @param tapSwipeInsert - One of "tap", "swipe" or "insert"
	 * @param pin - The passed credit card's PIN
	 * @param signiture - (Can be null) An image of signature on the back of the card
	 * @throws IOException
	 */
	public void payWithCredit(Card credit, CardIssuer ci, String tapSwipeInsert, String pin, BufferedImage signiture) throws IOException {
		
		// Update the selected CardIssuer
		this.ciSelected = ci;
				
		// A dummy variable, since CardReader's methods return CardData
		CardData data;
		
		// Based on the decision of the customer, CardReader either reads the card via tap, swipe or insert
		if (tapSwipeInsert.compareToIgnoreCase("tap") == 0) {
			data = this.checkoutStation.cardReader.tap(credit);
		}else if (tapSwipeInsert.compareToIgnoreCase("swipe") == 0) {
			data = this.checkoutStation.cardReader.swipe(credit, signiture);
		}else if (tapSwipeInsert.compareToIgnoreCase("insert") == 0) {
			data = this.checkoutStation.cardReader.insert(credit, pin);
		}else {
			return;
		}
		
	}
	
	/**
	 * This method is used when the customer pays with gift card
	 * @param gift - A co-op gift card 
	 * @throws IOException
	 */
	public void payWithGift(Card gift) throws IOException {
		
		// Update the selected CardIssuer
		this.ciSelected = new CardIssuer("CoopGiftCard");
		
		// A dummy variable, since CardReader's methods return CardData
		CardData data;
		
		// Gift cards have to swiped and do not have card-holders' signatures 
		data = this.checkoutStation.cardReader.swipe(gift, null);
	}
	
	/**
	 * This method is used when the customer enters his/her membership card by swiping
	 * @param membership - A co-op membership card
	 * @throws IOException
	 */
	public void swipeMembershipCard(Card membership) throws IOException {
		
		// Update the selected CardIssuer
		this.ciSelected = new CardIssuer("CoopGiftCard");
		
		// A dummy variable, since CardReader's methods return CardData
		CardData data;
		
		// Gift cards have to swiped and do not have card-holders' signatures 
		data = this.checkoutStation.cardReader.swipe(membership, null);
		
		// Read and update the membership card number
		if (cReaderListener.isMembershipCardValid()) {
			
			// Update the field, membershipNumber
			this.membershipNumber = cReaderListener.getMemberNumber();
		}
	}
	
	/**
	 *  When the transaction is over (completed or failed), remove the card
	 *  This also applies to the membership card
	 */
	public void removeCard() {
		
		// If the transaction is done (completed or failed), remove the card
		if (cReaderListener.isTransactionCompleteOrFailed()) {
			
			// Remove the card in CardReader
			this.checkoutStation.cardReader.remove();
		}
		
	}
	
	/**
	 * This method gets the membership card number of the customer
	 * @param membershipNumber - Co-op number entered by the customer
	 */
	public void enterMembershipNumber(String membershipNumber) {
		
		// Verify the membership number
		if (cReaderListener.contains(cReaderListener.getValidMemberNumbers(), membershipNumber)) {
			
			// Update the field, membershipNumber
			this.membershipNumber = membershipNumber;
		}
		
	}
	
	/**
	 * This function dispenses change to the customer via the SelfCheckoutStation
	 * @param amount -> A double variable representing the amount of change required to be distributed (rounds to nearest 0.05)
	 * @param hardware -> The configured SelfCheckoutStation
	 * @param cur -> A variable representing the currency the station uses
	 * @return  Returns a double representing the amount unable to be distributed (should be 0.00)
	 * @throws SimulationException
	 * @author Austin Shawaga UCID 30086103
	 */
	public void dispenseChange(double amount, SelfCheckoutStation hardware, Currency cur, OurBanknoteSlotListener bsl)
			throws SimulationException {
		
		ArrayList<Integer> banknoteEmitQueue= new ArrayList<Integer>(); //A list of banknotes to be emitted
		ArrayList<BigDecimal> coinEmitQueue = new ArrayList<BigDecimal>(); //A list of coins to be emitted
		int amountTaken = 0; //Used to keep track of how many denominations have been taken from a particular dispenser
		
		
		
		//Dispencer variables
		BanknoteDispenser bd = null;
		CoinDispenser cd = null;

		//If the hardware passed is null, throw an exception
		if (hardware == null || bsl == null) {
			throw new SimulationException("Hardware pointer or Banknote Slot Listener is null when dispensing change");
		}
		
		if(amount == 0) {
			return; //No change is needed so simply do nothing
		}

		
		//Ensures that the amount value is positive (sometimes negative values are used to represent change)
		if (amount < 0) {
			amount *= -1;
		}

		// This rounds the value to the nearest 0.05 multiple
		// Found at:
		// https://stackoverflow.com/questions/9256005/java-rounding-to-nearest-0-05
		amount = Math.round(amount * 20.0) / 20.0;
		
		

		//Two maps from hardware which direct the software to proper dispensers
		Map<Integer, BanknoteDispenser> banknoteDisps = hardware.banknoteDispensers;
		Map<BigDecimal, CoinDispenser> coinDisps = hardware.coinDispensers;

		//Denominations within the hardware
		List<BigDecimal> coinValues = hardware.coinDenominations;
		int noteValues[] = hardware.banknoteDenominations;

		
		// Sort each list in ascending order

		// Simple insertion sort algorithm to sort banknote denominations
		for (int i = 1; i < noteValues.length; i++) {
			int j = i;
			while (j > 0 && noteValues[j] < noteValues[j - 1]) {
				int dummy = noteValues[j];
				noteValues[j] = noteValues[j - 1];
				noteValues[j - 1] = dummy;
				j--;
			}
		}

		coinValues.sort(null); // Sorts coin values in ascending order

		

		//First checks all banknote denominations and tries to give as much of the amount owed in them
		for (int i = noteValues.length - 1; i >= 0; i--) {
			
			//Checks to see if this denomination is less then the amount owed
			if ((double)noteValues[i] <= amount) {
				
				bd = banknoteDisps.get(noteValues[i]); //Retrieves corresponding dispenser
				
				//Ensures that the dispenser exists, is enabled, and has banknotes to dispense
				if ((bd != null && bd.isDisabled() == false) && bd.size() > 0) {

					// dispenser is not empty and enabled

					amountTaken = 0;
					//While the denomination is less then the amount owed
					while ((double)noteValues[i] <= amount && amountTaken < bd.size()) {

						banknoteEmitQueue.add(noteValues[i]); //adds banknote to list to be later emitted
						
						amount -= (double) noteValues[i]; //subtract value given from amount left
						
						amountTaken++;
						
						//hardware.banknoteOutput.removeDanglingBanknote(); //simulates customer grabbing banknote
						
						//bdl.setEmitted(false); //reset listener field

					}

				}

			}
		}

		// amount is now less then every denomination of noteValues

		//Distributes the rest of the amount owed in coins
		for (int i = coinValues.size() - 1; i >= 0; i--) {
			
			double value = coinValues.get(i).doubleValue(); //Gets the double value to make arithmetic easier
			
			//If the denomination is less then the amount owed
			if (value <= amount) {
				
				cd = coinDisps.get(coinValues.get(i)); //retrieves dispenser
				
				//Ensures that the dispenser exists, is enabled, and has coins to dispense
				if ((cd != null && cd.isDisabled() == false) && cd.size() > 0) {

					// dispenser is not empty and enabled

					amountTaken = 0;
					//emits all of the coins available.
					while (value <= amount && amountTaken < cd.size()) {

						coinEmitQueue.add(coinValues.get(i));
						
						amount -= value; //subtracts the coin value from the amount left
						amount = Math.round(amount * 100.0) / 100.0; //ensures amount is 2 decimal places
						
						amountTaken++;

					}

				}

			}
		}
		
		//If not all of the change could be accounted for, throw an exception
		if(amount != 0) {
			throw new SimulationException("Change amount could not be delivered!");
		}else{
			//If there is coins to be given as change
			if(coinEmitQueue.size() > 0) {
				emitCoins(hardware, coinEmitQueue); //should emit all the coins, exception thrown if error occurs during proccess
			}
			
			//If there is banknotes to be given as change
			if(banknoteEmitQueue.size() > 0) {
				int valueFirstEmitted; //Stores the value which initializes the emitting proccess
				
				valueFirstEmitted = banknoteEmitQueue.get(0);
				banknoteEmitQueue.remove(0); //Removes first value from list of values to be given as change
				
				bsl.setBanknoteQ(banknoteEmitQueue); //gives banknote emit Q to slot listener - first banknote to be omitted
				
				
				//Starts the process of emitting the banknotes as change
				//Everytime the user grabs a hanging note, the next one will be released, an exception is thrown if an error occurs
				emitBanknote(hardware,valueFirstEmitted); 
				
				
			}
		}
		
		

	}
	
	
	/** This function emits a list of coinValues (occurs all at once, no need to wait for customer interaction)
	 * 
	 * <p>
	 * Pre Condition: Change has already been calculated and each increment listed to be given 
	 * as change exists within the machine. (Validity of coin change list not checked)
	 * </p>
	 * @param hardware -> This is the SelfCheckoutStation distributing the change.
	 * @param coinQ -> A list of denominations of coins to be given as change
	 * @throws SimulationException
	 * @author Austin Shawaga UCID: 30086103
	 */
	public void emitCoins(SelfCheckoutStation hardware, ArrayList<BigDecimal> coinQ) throws SimulationException{
		CoinDispenser cd = null; //The dispenser of coins
		CoinDispenserListenerImplement cdl = new CoinDispenserListenerImplement(); //Listener to ensure coins are emitted correctly
		
		
		//For each coin value in the change list
		for(BigDecimal value : coinQ) {
			cd = hardware.coinDispensers.get(value); //Gets corresponding dispenser
			cd.register(cdl); //registers listener
			
			try {
				cd.emit(); //emits coin
				
				if(cdl.isEmitted() == false) {
					throw new Exception(); //Error occurred while emitting a coin
				}
				
			} catch (Exception e) {
				throw new SimulationException("Error emitting coins"); //throws exception
			}
			
			cd.deregister(cdl); //Deregisters listener
			cdl.resetEmitted(); //Resets listener field to be used in next dispenser
			
		}
	}
	
	
	/**This function emits one banknote, will trigger another banknote to be emitted if listener is configured
	 * @param hardware -> This is the SelfCheckoutStation distributing the change.
	 * @param value -> an integer value of the banknote to be distributed
	 * @throws SimulationException
	 * @author Austin Shawaga UCID 30086103
	 */
	public void emitBanknote(SelfCheckoutStation hardware, int value) throws SimulationException{
		BanknoteDispenser bd = null;
		
		bd = hardware.banknoteDispensers.get(value); //obtains corresponding dispenser
		
		
		try {
			bd.emit(); //Emits the banknote
			
			//This point in the function may not be reached until user grabs all of the queued banknotes
	
			
		} catch (Exception e) {
			throw new SimulationException("ERROR EMITTING BANKNOTE"); //Error occured
		}
		
		

	}
	
	
	/** This method uses a PLU code to find a product from the PLU database and adds it to the cart, with the product's weight as the value in the cart map
	 * 
	 * @param hardware 
	 * 	This is the selfcheckout machine that has the scale that weighs the PLUCodedProducts
	 * 
	 * @param code 
	 * 	This is the PLU code of the item that is being entered
	 * 
	 * @throws OverloadException 
	 * 	if the weight of the item exceeds the maximum weight of the scale
	 * 
	 * @throws SimulationException 
	 * 	if the PLU code is not in the database
	 * 
	 * 
	 */
	public void PLUEnter(PriceLookupCode code) throws OverloadException, SimulationException {
		SelfCheckoutStation hardware = this.checkoutStation;
		
		// check if item in database
		if(!ProductDatabases.PLU_PRODUCT_DATABASE.containsKey(code)) {
			throw new SimulationException("Product with entered code not found");
		}

		try {
			
			double ItemWeight = hardware.scale.getCurrentWeight();
			if(ItemWeight > 0) {
				Product product = ProductDatabases.PLU_PRODUCT_DATABASE.get(code);
				
				// if the cart does not contain the product, add the product as a key, with the weight as a value (to the  cart)
				// otherwise, add the weight to the item already in the cart
				if(!cart.containsKey(product)) {
					this.cart.put(product, ItemWeight);
				}
				else {
					this.cart.put(product, this.cart.get(product) + ItemWeight);
				}
				
			}
			else {
				throw new SimulationException("Scale is empty");
			}

		} 	catch(Exception e) {
				throw e;
		}
			
		
	}
	
	/**
	 * This method is used for finding a PLUCodedProduct that the user is searching for, by finding PLUCodedProducts, from the PLU_PRODUCT_DATABASE, that have a matching description.
	 * 
	 * @param stringSearch 
	 * 	the string to search for amongst all PLUCodedProduct descriptions (Note: This can be a substring of the full name of product, like "Ban" in "Banana")
	 * 
	 * @return an ArrayList of strings that have a matching substring with the paramater stringSearch, and null if there are no matching substrings
	 */
	public ArrayList<PLUCodedProduct> ProductSearch(String stringSearch) throws SimulationException{
		
		ArrayList<PLUCodedProduct> matchingProducts = new ArrayList<PLUCodedProduct>();
		
		//iterates through each code in the PLU Product database, then gets the description for the product that corresponds to that code, 
		//and if a substring of that description matches stringSearch, this product is added to the matchingProducts ArrayList
		if (stringSearch == null) {
			throw new SimulationException("Search term for product search cannot be null");
		}
		for(PriceLookupCode code : ProductDatabases.PLU_PRODUCT_DATABASE.keySet()) {
			if(ProductDatabases.PLU_PRODUCT_DATABASE.get(code).getDescription().contains(stringSearch)) {
				matchingProducts.add(ProductDatabases.PLU_PRODUCT_DATABASE.get(code));
			}
		}
		
		return matchingProducts;
	}
	
	/**
	 * This method finds the total of the transaction, by iterating through the cart and adding to the total based on the price of each product in the cart
	 * 
	 * 
	 * @return the total of the transaction in dollars, as a double
	 */
	public double getTransactionTotal() {
		
		double total = 0;
		//COOP is charging 10 cents per plastic bag
		double costOfPlasticBag = 0.10;
		
		for(Product product : this.cart.keySet()) {
			if(product instanceof BarcodedProduct) {
				total = total+ (product.getPrice().doubleValue() * this.cart.get(product));				
			}
			
			if(product instanceof PLUCodedProduct) {
				
				double priceToAdd = (this.cart.get(product).doubleValue()/1000) * (product.getPrice().doubleValue());
				
				total += priceToAdd;
				
			}
		}
		
		//Calculating the value of plastic bag/s used
		double valueOfPlasticBags = Double.valueOf(this.numberOfPlasticBags) * costOfPlasticBag ;
		
		
		return total + valueOfPlasticBags;
	}
	
	
	/**
	 * This method gets the number of plastic bags used by the customer
	 * The value of the plastic bag/s will be added in the transaction total
	 * 
	 * @param numberOfPlasticBags
	 * 		Number of plastic bag/s used
	 * 
	 * @author Angelica Eugenio
	 */
	public void enterNumberOfPlasticBags(int numberOfPlasticBags) throws SimulationException{
		
		//Making sure that the customer is done scanning all the items
		this.numberOfPlasticBags = numberOfPlasticBags;
	}
	
	
	/**
	 * This method is used when the customer does not want to bag a scanned item
	 * 
	 * @param item
	 * 		The current item (scanned item by the customer)
	 * @param hardware
	 * 		The self-checkout machine 
	 * 
	 * @author Angelica Eugenio
	 */
	public void doNotBagAScannedItem(BarcodedItem item, SelfCheckoutStation hardware) throws SimulationException{
		//Checking if the item is null
		if(item == null)
		{
			throw new SimulationException("The item is null");
		}
		
		//Getting the barcode of the barcoded item
		Barcode barcode = item.getBarcode();
		//Checking if the customer has already scanned the item
		if(this.cart.containsKey(ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode)))
		{
			//Setting these variables so that the discrepancy won't exist
			double itemWeight = item.getWeight();
			this.currently_scanning = true;
			this.expectedWeight = itemWeight;
			scaleListener.lastWeight = 0;
			scaleListener.nextItemisBag = false;
			
			//Calling this method - this will add the item weight in the itemWeightList instead in the bagWeightList
			scaleListener.weightChanged(hardware.baggingArea , itemWeight);
		
			
		}
		else
		{
			//The customer needs to scan the item first before placing it on the bagging area
			throw new SimulationException("The item is not yet scanned");
		}
	}
	
	
	/**
	 * This method simply returns if the bagging area scale is empty, if the current transaction has been paid for. 
	 * 
	 * @throws OverloadException if measured weight on the scale is over the limit (This might never happen, but it's more of a safeguard here).
	 * 
	 * @return true if the transaction has been paid for and the scale is empty, false otherwise
	 */
	public boolean bagScaleEmptyAfterPaid(boolean isPaid, SelfCheckoutStation hardware) throws OverloadException {
		if(isPaid) {
			return hardware.baggingArea.getCurrentWeight() == 0;
		}
		return  false;
	}
	
	
	
	/**
	 * This function is called when a discrepancy is detected and the attendant approves it.
	 * If the attendant approves this discrepancy this method is called to reset the state.
	 * @author Muhammad Adil
	 * 
	 */
	public void attendantApprovesWeight() {
		
		this.currently_scanning = true;	//set the station back to working state
	}
	
	
	/** Method that removes a given product from the "cart" hashmap 
	 * 
	 * @param product	
	 *		the product to be removed from the cart
	 *
	 *	@throw SimulationException 
	 *		if the given product is not in the cart, or is null.
	 * 
	 * @author Matheus Brandao
	 */
	public void attendantRemovesProduct(Product product) throws SimulationException {
		if(product == null) {
			throw new SimulationException(new NullPointerException("product is null"));
		}
		
		if(!cart.keySet().contains(product)) {
			throw new SimulationException("product not in cart");
		}
		
		if(product instanceof PLUCodedProduct) {
			cart.remove(product);
		}
		
		else if(product instanceof BarcodedProduct){
			if(cart.get(product) == 1.0) {
				cart.remove(product);
			}
			else {
				cart.replace(product, cart.get(product) - 1);
			}
		}
	}
	
	/**
	 * A method that takes in a PLU code, and returns information about the product that has that PLU code in the form of an ArrayList
	 * 
	 * @param plu 
	 * 		The PLU code of the PLUCodedProduct that the attendant is searching for
	 * 
	 * @throw SimulationException 
	 * 		if the PLU is not in the database.
	 * 
	 * @return an ArrayList with the 0 index element being the product description, the 1 index element being the price per kg, and the 2 index element being the inventory of that item
	 * @author Matheus Brandao
	 */
	public ArrayList<Object> attendantLooksUpProduct(PriceLookupCode plu) throws SimulationException{
		
		if( ProductDatabases.PLU_PRODUCT_DATABASE.keySet().contains(plu)) {
			PLUCodedProduct product = ProductDatabases.PLU_PRODUCT_DATABASE.get(plu);
			return new ArrayList<Object>(Arrays.asList(product.getDescription(), product.getPrice(), ProductDatabases.INVENTORY.get(product)));
		}
		else {
			throw new SimulationException("PLU code not in database");
		}
	}
	
	/**
	 * A method that takes in a barcode, and returns information about the product that has that barcode in the form of an ArrayList
	 * 
	 * @param barcode
	 * 		The barcode of the BarcodedProduct that the attendant is searching for
	 * 
	 * @throw SimulationException 
	 * 		if the barcode is not in the database.
	 * 
	 * @return an ArrayList with the 0 index element being the product description, the 1 index element being the price per unit, and the 2 index element being the inventory of that item
	 * @author Matheus Brandao
	 */
	public ArrayList<Object>  attendantLooksUpProduct(Barcode barcode) {
		
		if(ProductDatabases.BARCODED_PRODUCT_DATABASE.keySet().contains(barcode)) {
			BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode);
			return new ArrayList<Object>(Arrays.asList(product.getDescription(), product.getPrice(), ProductDatabases.INVENTORY.get(product)));
		}
		else {
			throw new SimulationException("Barcode not in databse");
		}
		
	}
	
	/**
	 * Refills the ink in the receipt printer.
	 * @author Braden
	 */
	public void refillInk() {
		// Should only ever trigger if ink is empty.
		try {
			checkoutStation.printer.addInk(ReceiptPrinter.MAXIMUM_INK);
		} catch (Exception e) {
			System.out.println("Some ink overflowed. Oops!");
		}
	}
	
	/**
	 * Refills the paper in the receipt printer.
	 * @author Braden
	 */
	public void refillPaper() {
		try {
			checkoutStation.printer.addPaper(ReceiptPrinter.MAXIMUM_PAPER);
		} catch (Exception e) {
			System.out.println("Tried to stuff in too much paper. Oops!");
		}
	}
	
	/**
	 * This function lets the attendant block the station.
	 * It is blocked by  changing stations state, and making all listeners wait.
	 * @param hardware -> This is the SelfCheckoutStation that is to be blocked.
	 * @author Muhammad Adil
	 */
	public void attendantBlocksStation() throws Exception {
		//put station in false state so you can't scan anything
		SelfCheckoutStation hardware = this.checkoutStation; 
		this.currently_scanning = false;	
		this.waitingToPlaceItem = false;
	}
	
	/**
	 * This function lets the attendant unblock the station.
	 * It is unblocked by changing stations state, and notifying all listers.
	 * @param hardware -> This is the SelfCheckoutStation that is to be unblocked.
	 * @author Muhammad Adil
	 */
	public void attendantUnblocksStation() throws Exception {
		//put station in true state
		SelfCheckoutStation hardware = this.checkoutStation;
		this.currently_scanning = true;	
		this.waitingToPlaceItem = true;
	}
	
	/**A method that allows the attendant to empty the coin storage
	 * 
	 * @param hardware
	 * 		the selfcheckout station of which the storage is being emptied
	 * 
	 * @return a list containing the coins that were removed, presumably this list will be used to place the coins in long term storage
	 * 
	 * @author Matheus Brandao
	 */
	public List<Coin> attendantEmptyCoinStorage(){
		return checkoutStation.coinStorage.unload();
	}
	
	/**A method that allows the attendant to empty the banknote storage
	 * 
	 * @param hardware the selfcheckout station of which the storage is being emptied
	 * @return a list containing the banknotes that were removed
	 * @author Matheus Brandao
	 */
	public List<Banknote> attendantEmptyBanknoteStorage(){
		return checkoutStation.banknoteStorage.unload();
	}
	
	/**A method that refills some number of a specified coin into a specified coin dispenser
	 * 
	 * @param coinDispenser -> The coin dispenser that needs to be refilled
	 * @param coin -> The coin to refill the dispenser with
	 * @param amount -> the amount of coins to be loaded
	 * 
	 * @throws SimulationException -> if coin is null
	 * @throws OverloadException -> if the capacity of the dispenser is exceeded.
	 * 
	 * @author Matheus Brandao
	 */
	public void attendantRefillCoinDispenser(CoinDispenser coinDispenser, Coin coin, int amount) throws SimulationException, OverloadException{
		
		// the code for CoinDispenser will be angry if too many coins are loaded, so a try catch is appropriate here
		try {
			for(int i = 0; i < amount; i++) {
				coinDispenser.load(coin);
			}
		} catch(OverloadException e){
			throw e;
		} catch(SimulationException e2) {
			throw e2;
		}
	}
	
	/**A method that refills some number of a specified banknote into a specified banknote dispenser
	 * 
	 * @param banknoteDispenser -> the banknote dispenser
	 * @param banknote -> the type of banknote to refill the despenser
	 * @param amount -> the amount of banknotes to be added
	 * 
	 * @throws OverloadException -> if the capacity of the dispenser is exceeded
	 * @throws SimulationException -> if any of the banknotes are null, or if the dispenser is null
	 * 
	 * @author Matheus Brandao
	 */
	public void attendantRefillBanknoteDispenser(BanknoteDispenser banknoteDispenser, Banknote banknote, int amount) throws OverloadException, SimulationException {
		
		if(banknoteDispenser != null) {
			try {
				
				for(int i = 0; i < amount; i++) {
					banknoteDispenser.load(banknote);
				}
			} catch(OverloadException e) {
				throw e;
			} catch(SimulationException e2) {
				throw e2;
			}
		}
		else {
			throw new SimulationException("Banknote dispenser is null");
		}
	}
	
	/**
	 * The console has a preset password. Attendant enters the password from the GUI, and if they match the attendant gets access.
	 * 
	 * @param password -> the password attendant enters in the GUI
	 * @return returns[0] accessGranted -> true if the password is correct, false otherwise
	 * 		   returns[1] loggedIn -> true if password is correct and user in, false if pass is incorrect.
	 * 
	 * @author Muhammad Adil	
	 * 
	 */
	public boolean[] attendantLogsIn(String password) {
		
		this.accessGranted = false;
		this.loggedIn = false;
		
		String correct = "CoopCalgary";	//password needed to sign in 
		int correctHash = correct.hashCode();	//hash it for security LOL
		int userHash = password.hashCode();		//password user enters in from GUI
		
		if(correctHash == userHash) {	//check to see if they match 
			this.loggedIn = true;
			this.accessGranted = true;
		}else {	//if they dont match
			this.loggedIn = false;
			this.accessGranted = false;
		}
		
		boolean[] returns = new boolean[2];
		returns[0] = this.accessGranted;
		returns[1] = this.loggedIn;
		
		return returns;
		
	}
	
	/**
	 * This function relies on the GUI. If the attendant presses the log out button
	 * this method runs and logs out the attendant.
	 * <p>
	 * I am assuming that logOut is a button in the GUI that returns true when pressed,
	 * otherwise it is false.
	 * </p>
	 * 
	 * @param logOut -> Button that is true when pressed otherwise it is false.
	 * @author Muhammad Adil
	 * @return true -> if user is logged out successfully
	 * 		   false -> if user does not want to log off or if user is not logged-in in the first place.
	 */
	public boolean attendantLogsOut(Boolean logOut) {
		
		if(this.loggedIn == true && this.accessGranted == true) {	//make sure that the attendant is logged in first 
			
			if(logOut == true) {
				this.loggedIn = false;
				this.accessGranted = false;
				return true;
			}else {
				return false;
			}
			
		}
		return false;
	}
	
	
	
	/**
	 * Sets the machine state to true so all the listeners are active and ready to run.
	 * Also enables all the devices.
	 * @param hardware -> the station that is starting up
	 * @author Muhammad Adil
	 */
	public void attendantStartsStation() {
		SelfCheckoutStation hardware = this.checkoutStation;
		
		
		this.currently_scanning = true;
		hardware.baggingArea.enable();
		hardware.scale.enable();
		hardware.banknoteValidator.enable();
		hardware.banknoteStorage.enable();
		hardware.cardReader.enable();
		hardware.coinSlot.enable();
		hardware.coinStorage.enable();
		hardware.coinTray.enable();
		hardware.coinValidator.enable();
		hardware.handheldScanner.enable();
		hardware.mainScanner.enable();
		hardware.printer.enable();
		hardware.screen.enable();
		hardware.banknoteOutput.enable();
		hardware.banknoteInput.enable();
	}
	
	
	

	/**
	 * Sets the machine state to false so all the listeners are de-active.
	 * Also disables all the devices.
	 * @param hardware -> the station that is shutting down
	 * @author Muhammad Adil
	 */
	public void attendantShutsDownStation() {
		SelfCheckoutStation hardware = this.checkoutStation;
		
		this.currently_scanning = false;
		hardware.baggingArea.disable();
		hardware.scale.disable();
		hardware.banknoteValidator.disable();
		hardware.banknoteStorage.disable();
		hardware.cardReader.disable();
		hardware.coinSlot.disable();
		hardware.coinStorage.disable();
		hardware.coinTray.disable();
		hardware.coinValidator.disable();
		hardware.handheldScanner.disable();
		hardware.mainScanner.disable();
		hardware.printer.disable();
		hardware.screen.disable();
		hardware.banknoteOutput.disable();
		hardware.banknoteInput.disable();
	}
	
	
	
	
	
}
