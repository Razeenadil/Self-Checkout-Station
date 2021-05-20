package org.lsmr.SelfCheckoutSoftwareTest;

import static org.junit.Assert.*;

import java.util.Currency;

import org.junit.Before;

import org.junit.Test;
import org.lsmr.SelfCheckoutSoftware.OurReceiptPrinterListener;
import org.lsmr.SelfCheckoutSoftware.*;



public class OurReceiptPrinterListenerTest {

	static SelfCheckoutMachine testmac;
	static Currency cur;
	static OurReceiptPrinterListener x ;
	
	
	//ReceiptPrinter testprinter = new ReceiptPrinter();
	
	
	@Before
	public void setUp() throws Exception {
		testmac = new SelfCheckoutMachine();
		x = new OurReceiptPrinterListener();
		cur = Currency.getInstance("CAD");
		testmac.checkoutStation.printer.register(x);
		
	}

	@Test
	public void PaperStartupTest() {
		// Printer shouldn't have any paper before it is set up
		assertEquals(true,x.getIsOutOfPaper());
	}
	
	@Test
	public void InkStartUpTest() {
		// Printer shouldn't have any ink before it is set up
		assertEquals(true,x.getIsOutOfInk());
	}

		
	
	@Test
	public void AddingPapertest() {
		
		testmac.checkoutStation.printer.addPaper(1);
		
		// if we add paper then we should be able to get out of the boolean state of no paper
		assertEquals(false,x.getIsOutOfPaper()); 	
	}
	
	@Test
	public void AddingInktes() {
		
		testmac.checkoutStation.printer.addInk(1);
		
		// if we add paper then we should be able to get out of the boolean state of no paper
		assertEquals(false,x.getIsOutOfInk()); 	
		
	}
	
	@Test
	public void outOfInkandPaperTest() {
		x.outOfInk(testmac.checkoutStation.printer);
		x.outOfPaper(testmac.checkoutStation.printer);
	}

	
}
