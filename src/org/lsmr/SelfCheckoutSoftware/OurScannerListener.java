package org.lsmr.SelfCheckoutSoftware;

import java.util.ArrayList;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BarcodeScanner;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.BarcodeScannerListener;
import org.lsmr.selfcheckout.external.ProductDatabases;


public class OurScannerListener implements BarcodeScannerListener {
	
	private ArrayList<Barcode> scannedBarcodes; // The Barcode(s) that scanned properly
	private ArrayList<BarcodeScanner> enabledBarcodeScanners; // Keep track of registered BarcodeScanner's that have been enabled
	private ArrayList<BarcodeScanner> disabledBarcodeScanners ; // Keep track of registered BarcodeScanner's that have been disabled 
	private SelfCheckoutMachine checkoutMachine;
	
	/**
	 * Constructor of OurScannerListener
	 * Initializes the global variables
	 * @param cMachine
	 */
	public OurScannerListener(SelfCheckoutMachine cMachine) {
		this.checkoutMachine = cMachine;
		this.scannedBarcodes = new ArrayList<Barcode>();
		this.enabledBarcodeScanners = new ArrayList<BarcodeScanner>();
		this.disabledBarcodeScanners	= new ArrayList<BarcodeScanner>();
	}
	
	/**
	 * Returns an ArrayList of the unique Barcode(s) that have been successfully scanned 
	 * by a registered barcodeScanner
	 */
	
	public ArrayList<Barcode> getscannedBarcodes(){
		
		return scannedBarcodes;
	}
	
	/**
	 * Returns the ArrayList of registered BarcodeScanner's that have been enabled
	 */
	public ArrayList<BarcodeScanner> getenabledBarcodeScanners(){
		
		
		return enabledBarcodeScanners;
	}
	
	/**
	 * Returns the ArrayList of registered BarcodeScanner's that have been disabled
	 */
	public ArrayList<BarcodeScanner> getdisabledBarcodeScanners(){
		
		
		return disabledBarcodeScanners;
	}

	
	/**
	 * Announces that the indicated device has been enabled.
	 * 
	 * @param device
	 *                 The device that has been enabled.
	 */
	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
		/**
		 * Increments the notifiedEnabled attribute when a registered BarcodeScanner has 
		 * BarcodeScanner.enable() called. Note that this can be called more than once
		 * by a registered barcodeScanner
		 */
		
		// Add to enabledBarcodeScanners if not already present
		if (!enabledBarcodeScanners.contains(device)) {
			// HACK: have to explicitly type cast to match type signature
			enabledBarcodeScanners.add((BarcodeScanner) device);
		}
		
		// Remove from disabledBarcodeScanners if present
		if (disabledBarcodeScanners.contains(device)) {
			disabledBarcodeScanners.remove(device);
		}
		
		
	}

	/**
	 * Announces that the indicated device has been disabled.
	 * 
	 * @param device
	 *                 The device that has been enabled.
	 */
	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
		/**
		 * Increments the notifiedDisabled attribute when a registered BarcodeScanner has 
		 * BarcodeScanner.disable() called
		 */
		
		// Add to disabledBarcodeScanners if not already present
		if (!disabledBarcodeScanners.contains(device)) {
			// HACK: have to explicitly type cast to match type signature
			disabledBarcodeScanners.add((BarcodeScanner) device);
		}
		
		// Remove from enabledBarcodeScanners if present
		if (enabledBarcodeScanners.contains(device)) {
			enabledBarcodeScanners.remove(device);
		}
		
	}

	/**
	 * An event announcing that the indicated barcode has been successfully scanned.
	 * 
	 * @param barcodeScanner
	 *            The device on which the event occurred.
	 * @param barcode
	 *            The barcode that was read by the scanner.
	 */
	@Override
	public void barcodeScanned(BarcodeScanner barcodeScanner, Barcode barcode) {
		/**
		 * Appends the provided Barcode to the scannedBarcodes attribute
		 */
		
		if (checkoutMachine.waitingToPlaceItem) {
			return;
		}
		
		if (checkoutMachine.currently_scanning) { // Only register the scan if the checkoutMachine is currently scanning
			scannedBarcodes.add(barcode);
			if(ProductDatabases.BARCODED_PRODUCT_DATABASE.containsKey(barcode)) {
				if(this.checkoutMachine.cart.containsKey(ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode))) {
					this.checkoutMachine.cart.put(ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode), this.checkoutMachine.cart.get(ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode)) + 1);
				}
				else {
					this.checkoutMachine.cart.put(ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode), 1.0);
				}
				
				if(ProductDatabases.INVENTORY.containsKey(ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode))) {
					ProductDatabases.INVENTORY.replace(ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode), ProductDatabases.INVENTORY.get(ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode)) -1);
				}
			
			checkoutMachine.waitingToPlaceItem = true;
			}
		}
	}
		
		
	/**
	 * This method resets the transaction
	 * It removes all the items in the cart
	 */
	public void resetList() {
		/**
		 * Clears the scannedBarcodes attribute, and the cart 
		 */
		scannedBarcodes.clear();
		this.checkoutMachine.cart.clear();
	}
	
	/**
	 * Method to get the total price of items that were scanned (Note that this is not the total price of the transaction, merely the total for the items that were scanned)
	 * 
	 * @returns the total price of the items that were scanned, as a double
	 * 
	 * @throws SimulationException 
	 * 	if an item in the list is not in the BARCODED_PRODUCT_DATABASE
	 * 
	 * 
	 * TODO: maybe delete this idk if it will be useful since the cart hashmap is being used
	 * 		I'll keep it for now but idk if this will be all that useful.
	 */
	public double getScannedTotal() {
		double total = 0;
		
		for(int  i = 0; i < scannedBarcodes.size(); i++) {
			
			if(ProductDatabases.BARCODED_PRODUCT_DATABASE.containsKey(scannedBarcodes.get(i))) {
				
				total += ProductDatabases.BARCODED_PRODUCT_DATABASE.get(scannedBarcodes.get(i)).getPrice().doubleValue();
				
			}
			
			else {
				throw new SimulationException("One of the Item barcodes is not in the database");
			}
		}
		return total;
	}
	
	
}
