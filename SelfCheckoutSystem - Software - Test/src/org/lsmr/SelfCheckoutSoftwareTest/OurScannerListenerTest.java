package org.lsmr.SelfCheckoutSoftwareTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.SelfCheckoutSoftware.OurScannerListener;
import org.lsmr.SelfCheckoutSoftware.SelfCheckoutMachine;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.devices.BarcodeScanner;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.BarcodedProduct;

public class OurScannerListenerTest {
	Barcode bC;
	Barcode bC2;
	BarcodeScanner bS;
	BarcodeScanner bS2;
	BarcodedProduct bp1;
	BarcodedProduct bp2;
	OurScannerListener listener;
	SelfCheckoutMachine cMachine;
	Map<Barcode, BarcodedProduct> productDb;

	
	
	@Before
	public void makeScanners() {
		cMachine = new SelfCheckoutMachine();
		bS = new BarcodeScanner();
		bS2 = new BarcodeScanner();
		listener = new OurScannerListener(cMachine);
		bS.register(listener);
		bS2.register(listener);
		
		bC = new Barcode("15");
		bC2 = new Barcode("20");

		bp1 = new BarcodedProduct(bC, "Description", new BigDecimal(5.00));
		bp2 = new BarcodedProduct(bC2, "Description", new BigDecimal(5.00));
		
		// Add BarcodedProducts and add them to the database
		productDb = ProductDatabases.BARCODED_PRODUCT_DATABASE;
		productDb.clear(); // Clear the productDb so it's only the new products in the db
		productDb.put(bC, bp1);
		productDb.put(bC2, bp2);
		
	}
	
	@Test
	public void enabledDisabled() {
		bS.enable();
		bS2.enable();
		
		bS.disable();
		assertTrue(listener.getdisabledBarcodeScanners().contains(bS));
		assertTrue(!listener.getenabledBarcodeScanners().contains(bS));
		assertTrue(!listener.getdisabledBarcodeScanners().contains(bS2));
		assertTrue(listener.getenabledBarcodeScanners().contains(bS2));
		bS2.disable();
		assertTrue(listener.getdisabledBarcodeScanners().contains(bS));
		assertTrue(!listener.getenabledBarcodeScanners().contains(bS));
		assertTrue(listener.getdisabledBarcodeScanners().contains(bS2));
		assertTrue(!listener.getenabledBarcodeScanners().contains(bS2));
		bS2.enable();
		assertTrue(listener.getdisabledBarcodeScanners().contains(bS));
		assertTrue(!listener.getenabledBarcodeScanners().contains(bS));
		assertTrue(!listener.getdisabledBarcodeScanners().contains(bS2));
		assertTrue(listener.getenabledBarcodeScanners().contains(bS2));
		bS.enable();
		assertTrue(!listener.getdisabledBarcodeScanners().contains(bS));
		assertTrue(listener.getenabledBarcodeScanners().contains(bS));
		assertTrue(!listener.getdisabledBarcodeScanners().contains(bS2));
		assertTrue(listener.getenabledBarcodeScanners().contains(bS2));
	}
	
	@Test
	public void doubleDisableEnable() {
		bS.disable();
		bS.disable();
		assertTrue(listener.getdisabledBarcodeScanners().contains(bS));
		assertTrue(!listener.getenabledBarcodeScanners().contains(bS));
		bS.enable();
		bS.enable();
		assertTrue(!listener.getdisabledBarcodeScanners().contains(bS));
		assertTrue(listener.getenabledBarcodeScanners().contains(bS));
	}
	
	@Test
	public void scanCode() {
		cMachine.waitingToPlaceItem = false;
		bS.scan(new BarcodedItem(bC,50));
		assertTrue("Code not added",listener.getscannedBarcodes().get(listener.getscannedBarcodes().size()-1).equals(bC));
	}
	
	@Test
	public void scanCodeTwice() {
		cMachine.waitingToPlaceItem = false;
		bS.scan(new BarcodedItem(bC,50));
		assertTrue("Code not added",listener.getscannedBarcodes().get(listener.getscannedBarcodes().size()-1).equals(bC));
		int oldSize = listener.getscannedBarcodes().size();
		cMachine.waitingToPlaceItem = false;
		bS.scan(new BarcodedItem(bC,50));
		assertTrue("Code not added twice",listener.getscannedBarcodes().get(listener.getscannedBarcodes().size()-1).equals(bC));
		assertEquals("Code cannot be added twice",oldSize+1,listener.getscannedBarcodes().size());
	}
	
	@Test
	public void scan2Codes() {
		cMachine.waitingToPlaceItem = false;
		bS.scan(new BarcodedItem(bC,50));
		bS.scan(new BarcodedItem(bC,50));
	}
	
	@Test
	public void resetTest() {
		bS.scan(new BarcodedItem(new Barcode("10"),10));
		listener.resetList();
		assertTrue(listener.getscannedBarcodes().size() == 0);
	}
	
	@Test
	public void scanThenPlaceItems() {
		listener.resetList();
		cMachine.waitingToPlaceItem = false;
		bS.scan(new BarcodedItem(bC,50));
		assertTrue("Code not stored",listener.getscannedBarcodes().get(listener.getscannedBarcodes().size()-1).equals(bC));
		bS.scan(new BarcodedItem(bC2,50));
		assertFalse("Should be waiting",listener.getscannedBarcodes().get(listener.getscannedBarcodes().size()-1).equals(bC2));
		cMachine.checkoutStation.baggingArea.add(new BarcodedItem(bC,50));
		bS.scan(new BarcodedItem(bC2,50));
		assertTrue("Should be waiting",listener.getscannedBarcodes().get(listener.getscannedBarcodes().size()-1).equals(bC2));
	}
	
	@Test
	public void selfCheckoutFlagFalse(){
		cMachine.currently_scanning = false;
		bS.scan(new BarcodedItem(bC,50));
		assertTrue(listener.getscannedBarcodes().size() == 0);
		
		cMachine.waitingToPlaceItem = false;
		bS.scan(new BarcodedItem(bC2,50));
		assertTrue(listener.getscannedBarcodes().size() == 0);
		
	}
	
	
	@Test(expected = SimulationException.class)
	public void getScannedTotalError() {
		Barcode bC3 = new Barcode("25");
		
		bS.scan(new BarcodedItem(bC3,50));
		listener.getScannedTotal();
	}
	
	public boolean contains(ArrayList<Integer> list, int val) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equals(val)) {
				return true;
			}
		}
		return false;
	}
	
	
	@After
	public void reset() {
		bS = null;
		bS2 = null;
		listener = null;
	}

}
