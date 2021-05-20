package org.lsmr.SelfCheckoutSoftwareTest;


import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.SelfCheckoutSoftware.SelfCheckoutMachine;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

public class OurReturnAddingItemTest {

	SelfCheckoutMachine cMachine= new SelfCheckoutMachine();
	Barcode bC;
	
	BarcodedItem bi1;
	@Test // After scanning item, It machine can still scanning
	public void StartReturnAddingitem() {
		Barcode bC = new Barcode("15");
		BarcodedItem bi1 = new BarcodedItem(bC, 15.00);
		
		Barcode bC2=new Barcode("16");
		BarcodedItem bi2 = new BarcodedItem(bC2,10.0);
		try {
			cMachine.scanItem(bi1);
			cMachine.returnToAddingItems();
			Assert.assertTrue(cMachine.currently_scanning);
			if(cMachine.currently_scanning) {
				cMachine.scanItem(bi2);
				cMachine.returnToAddingItems();
			}else if(!cMachine.currently_scanning){
				cMachine.finalizeItems();
			}
			cMachine.resetMachine();
		}catch(Exception e) {
		fail("Return Adding the item is invaild");	
		}
		
		
	}
	@Test //  When customer finishd scan. The customer waits for pay item
	public void finalizeItemsTest() {
		Barcode bC = new Barcode("15");
		BarcodedItem bi1 = new BarcodedItem(bC, 15.00);
		
		Barcode bC2=new Barcode("16");
		BarcodedItem bi2 = new BarcodedItem(bC2,10.0);
		try {
			cMachine.scanItem(bi1);
			cMachine.scanItem(bi2);
			cMachine.finalizeItems();
			
			}catch(Exception e) {
				fail("The settle accounts is invaild");
		}
	}
	
	
	
	
}
