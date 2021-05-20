package org.lsmr.SelfCheckoutSoftwareTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.SelfCheckoutSoftware.BanknoteDispenserListenerImplement;
import org.lsmr.SelfCheckoutSoftware.CoinDispenserListenerImplement;
import org.lsmr.SelfCheckoutSoftware.OurBanknoteSlotListener;
import org.lsmr.SelfCheckoutSoftware.SelfCheckoutMachine;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;


/** This is a testing suite for the deliver change use case
 * @author Austin Shawaga UCID 30086103
 *
 */
public class testChangeDispensing {
	
	//Values used to initialize hardware
	private final Currency CAD = Currency.getInstance("CAD");
	private final int MAX_WEIGHT = 1000;
	private final int SENS = 2;
	
	//banknotes
	private final Banknote HUNDO = new Banknote(100,CAD);
	private final Banknote FIFTY = new Banknote(50,CAD);
	private final Banknote TWENTY = new Banknote(20,CAD);
	private final Banknote TEN = new Banknote(10,CAD);
	private final Banknote FIVE = new Banknote(5,CAD);
	
	//coins
	private final Coin NICKEL = new Coin(new BigDecimal(0.05),CAD);
	private final Coin DIME = new Coin(new BigDecimal(0.10),CAD);
	private final Coin QUARTER = new Coin(new BigDecimal(0.25),CAD);
	private final Coin DOLLAR = new Coin(new BigDecimal(1.00),CAD);
	private final Coin TWONIE = new Coin(new BigDecimal(2.00),CAD);
	
	//banknote listeners for each denomination 100-5
	private BanknoteDispenserListenerImplement bdl_hundo;
	private BanknoteDispenserListenerImplement bdl_fifty;
	private BanknoteDispenserListenerImplement bdl_twenty;
	private BanknoteDispenserListenerImplement bdl_ten;
	private BanknoteDispenserListenerImplement bdl_five;
	
	//coin listeners for each denomination 0.05-2.00
	private CoinDispenserListenerImplement cdl_nickel;
	private CoinDispenserListenerImplement cdl_dime;
	private CoinDispenserListenerImplement cdl_quarter;
	private CoinDispenserListenerImplement cdl_dollar;
	private CoinDispenserListenerImplement cdl_twonie;
	
	private OurBanknoteSlotListener bsl; //Banknote slot listener to implement banknote delivery
	private BanknoteSlotGrabber bsg; //Listener of the banknote slot used to emulate a customer grabbing the banknote
	
	private SelfCheckoutMachine sm;
	
	/**
	 * Sets up the tests by creating each listener
	 */
	@Before
	public void setUp() throws Exception {
		
		//Banknote Dispenser Listeners
		bdl_hundo = new BanknoteDispenserListenerImplement();
		bdl_fifty = new BanknoteDispenserListenerImplement();
		bdl_twenty = new BanknoteDispenserListenerImplement();
		bdl_ten = new BanknoteDispenserListenerImplement();
		bdl_five = new BanknoteDispenserListenerImplement();
		
		//Coin Dispenser Listeners
		cdl_nickel = new CoinDispenserListenerImplement();
		cdl_dime = new CoinDispenserListenerImplement();
		cdl_quarter = new CoinDispenserListenerImplement();
		cdl_dollar = new CoinDispenserListenerImplement();
		cdl_twonie = new CoinDispenserListenerImplement();
		
		bsl = new OurBanknoteSlotListener(); //Slot listener to trigger next emit of banknotes
		bsg = new BanknoteSlotGrabber(); //Simple listener which emulates a user grabbing a banknote
		
		sm = new SelfCheckoutMachine();
		
	}

	

	/**
	 * This tests what happens when $10.00 should be administered as change.
	 * Loads the hardware with ample amounts of change, expects only one $10 banknote to be returned
	 */
	@Test
	public void testSimple10Change() {
		
		BigDecimal[] coinDenom = {new BigDecimal(0.05)};
		int[] banknoteDenom = {5,10,20};
		
		
		SelfCheckoutStation hardware = new SelfCheckoutStation(CAD,banknoteDenom,coinDenom,MAX_WEIGHT,SENS);
		
		bsl.setMachine(sm);
		
		bsl.setHardware(hardware);
		hardware.banknoteOutput.register(bsl);
		hardware.banknoteOutput.register(bsg);
		
		
		//Registers a listener for each denomination
		hardware.banknoteDispensers.get(20).register(bdl_twenty);
		hardware.banknoteDispensers.get(10).register(bdl_ten);
		hardware.banknoteDispensers.get(5).register(bdl_five);
		hardware.coinDispensers.get(new BigDecimal(0.05)).register(cdl_nickel);
		
		
		//Loads each dispenser
		try {
		hardware.banknoteDispensers.get(20).load(TWENTY);
		hardware.banknoteDispensers.get(20).load(TWENTY);
		
		hardware.banknoteDispensers.get(10).load(TEN);
		hardware.banknoteDispensers.get(10).load(TEN);
		
		hardware.banknoteDispensers.get(5).load(FIVE);
		hardware.banknoteDispensers.get(5).load(FIVE);
		
		hardware.coinDispensers.get(new BigDecimal(0.05)).load(NICKEL);
		
		}catch(Exception e) {
			fail("Error loading Dispensers");
		}
		
		
		sm.dispenseChange(10.00,hardware, CAD,bsl); //Attempts to deposit change
		
		

		//Expects only 1 $10 banknote to be distributed
		assertEquals(0,bdl_twenty.getAmountEmitted());
		assertEquals(1,bdl_ten.getAmountEmitted());
		assertEquals(0,bdl_five.getAmountEmitted());
		assertEquals(0,cdl_nickel.getAmountEmitted());
	}
	
	/**
	 * This tests what happens when 0.25 should be distributed with ample change.
	 * Expects only one $0.25 coin to be returned.
	 */
	@Test
	public void SimpleQuarterChange(){
		BigDecimal[] coinDenom = {new BigDecimal(0.05),new BigDecimal(0.10), new BigDecimal(0.25),new BigDecimal(1.00),new BigDecimal(2.00)};
		int[] banknoteDenom = {5};
		SelfCheckoutStation hardware = new SelfCheckoutStation(CAD,banknoteDenom,coinDenom,MAX_WEIGHT,SENS);
		
		bsl.setMachine(sm);
		
		bsl.setHardware(hardware);
		hardware.banknoteOutput.register(bsl);
		hardware.banknoteOutput.register(bsg);
		
		//Registers listeners for each denomination
		hardware.coinDispensers.get(new BigDecimal(0.05)).register(cdl_nickel);
		hardware.coinDispensers.get(new BigDecimal(0.10)).register(cdl_dime);
		hardware.coinDispensers.get(new BigDecimal(0.25)).register(cdl_quarter);
		hardware.coinDispensers.get(new BigDecimal(1.00)).register(cdl_dollar);
		hardware.coinDispensers.get(new BigDecimal(2.00)).register(cdl_twonie);
		
		hardware.banknoteDispensers.get(5).register(bdl_five);
		
		//Loads dispensers
		try {
			hardware.banknoteDispensers.get(5).load(FIVE);
			hardware.banknoteDispensers.get(5).load(FIVE);
			
			hardware.coinDispensers.get(new BigDecimal(0.05)).load(NICKEL);
			hardware.coinDispensers.get(new BigDecimal(0.05)).load(NICKEL);
			
			hardware.coinDispensers.get(new BigDecimal(0.10)).load(DIME);
			hardware.coinDispensers.get(new BigDecimal(0.10)).load(DIME);
			
			hardware.coinDispensers.get(new BigDecimal(0.25)).load(QUARTER);
			hardware.coinDispensers.get(new BigDecimal(0.25)).load(QUARTER);
			
			hardware.coinDispensers.get(new BigDecimal(1.00)).load(DOLLAR);
			hardware.coinDispensers.get(new BigDecimal(1.00)).load(DOLLAR);
			
			hardware.coinDispensers.get(new BigDecimal(2.00)).load(TWONIE);
			hardware.coinDispensers.get(new BigDecimal(1.00)).load(TWONIE);
			
			}catch(Exception e) {
				fail("Error loading Dispensers");
			}
		
		sm.dispenseChange(0.25,hardware, CAD,bsl); //Attempts to deposit change
		
		//Expects only one $0.25 coin to be given to customer
		assertEquals(0,cdl_twonie.getAmountEmitted());
		assertEquals(0,cdl_dollar.getAmountEmitted());
		assertEquals(1,cdl_quarter.getAmountEmitted());
		assertEquals(0,cdl_dime.getAmountEmitted());
		assertEquals(0,cdl_nickel.getAmountEmitted());
		assertEquals(0,bdl_five.getAmountEmitted());
		
		
	}
	
	
	/**
	 * This function tests $2.80 in change which should include multiple coins
	 */
	@Test
	public void testMultipleCoinChange() {
		BigDecimal[] coinDenom = {new BigDecimal(0.05),new BigDecimal(0.10), new BigDecimal(0.25),new BigDecimal(1.00),new BigDecimal(2.00)};
		int[] banknoteDenom = {5};
		SelfCheckoutStation hardware = new SelfCheckoutStation(CAD,banknoteDenom,coinDenom,MAX_WEIGHT,SENS);
		
		bsl.setMachine(sm);
		
		bsl.setHardware(hardware);
		hardware.banknoteOutput.register(bsl);
		hardware.banknoteOutput.register(bsg);
		
		//Registers listeners with each dispenser
		hardware.coinDispensers.get(new BigDecimal(0.05)).register(cdl_nickel);
		hardware.coinDispensers.get(new BigDecimal(0.10)).register(cdl_dime);
		hardware.coinDispensers.get(new BigDecimal(0.25)).register(cdl_quarter);
		hardware.coinDispensers.get(new BigDecimal(1.00)).register(cdl_dollar);
		hardware.coinDispensers.get(new BigDecimal(2.00)).register(cdl_twonie);
		
		hardware.banknoteDispensers.get(5).register(bdl_five);
		
		//Loads the hardware dispensers
		try {
			hardware.banknoteDispensers.get(5).load(FIVE);
			hardware.banknoteDispensers.get(5).load(FIVE);
			
			hardware.coinDispensers.get(new BigDecimal(0.05)).load(NICKEL);
			hardware.coinDispensers.get(new BigDecimal(0.05)).load(NICKEL);
			
			hardware.coinDispensers.get(new BigDecimal(0.10)).load(DIME);
			hardware.coinDispensers.get(new BigDecimal(0.10)).load(DIME);
			
			hardware.coinDispensers.get(new BigDecimal(0.25)).load(QUARTER);
			hardware.coinDispensers.get(new BigDecimal(0.25)).load(QUARTER);
			hardware.coinDispensers.get(new BigDecimal(0.25)).load(QUARTER);
			hardware.coinDispensers.get(new BigDecimal(0.25)).load(QUARTER);
			
			hardware.coinDispensers.get(new BigDecimal(1.00)).load(DOLLAR);
			hardware.coinDispensers.get(new BigDecimal(1.00)).load(DOLLAR);
			
			hardware.coinDispensers.get(new BigDecimal(2.00)).load(TWONIE);
			hardware.coinDispensers.get(new BigDecimal(1.00)).load(TWONIE);
			
			}catch(Exception e) {
				fail("Error loading Dispensers");
			}
		
		
		sm.dispenseChange(2.80,hardware, CAD,bsl); //Attempts to deliver change
		

		
		//Expects 1 $2.00 coin, 3 $0.25 coins, and 1 $0.05 coin
		assertEquals(1,cdl_twonie.getAmountEmitted());
		assertEquals(0,cdl_dollar.getAmountEmitted());
		assertEquals(3,cdl_quarter.getAmountEmitted());
		assertEquals(0,cdl_dime.getAmountEmitted());
		assertEquals(1,cdl_nickel.getAmountEmitted());
		assertEquals(0,bdl_five.getAmountEmitted());
		
	}
	
	
	/**
	 * Tests what happens when a non multiple of 0.05 is given as input, expects the value is rounded accordingly.
	 * 0.83 is given and 0.85 worth of change is expected to be returned
	 */
	@Test
	public void testMultipleCoinChangeRounded() {
		BigDecimal[] coinDenom = {new BigDecimal(0.05),new BigDecimal(0.10), new BigDecimal(0.25),new BigDecimal(1.00),new BigDecimal(2.00)};
		int[] banknoteDenom = {5};
		SelfCheckoutStation hardware = new SelfCheckoutStation(CAD,banknoteDenom,coinDenom,MAX_WEIGHT,SENS);
		
		bsl.setMachine(sm);
		
		bsl.setHardware(hardware);
		hardware.banknoteOutput.register(bsl);
		hardware.banknoteOutput.register(bsg);
		
		//Registers listeners for each denomination
		hardware.coinDispensers.get(new BigDecimal(0.05)).register(cdl_nickel);
		hardware.coinDispensers.get(new BigDecimal(0.10)).register(cdl_dime);
		hardware.coinDispensers.get(new BigDecimal(0.25)).register(cdl_quarter);
		hardware.coinDispensers.get(new BigDecimal(1.00)).register(cdl_dollar);
		hardware.coinDispensers.get(new BigDecimal(2.00)).register(cdl_twonie);
		
		hardware.banknoteDispensers.get(5).register(bdl_five);
		
		//Loads hardware dispensers
		try {
			hardware.banknoteDispensers.get(5).load(FIVE);
			hardware.banknoteDispensers.get(5).load(FIVE);
			
			hardware.coinDispensers.get(new BigDecimal(0.05)).load(NICKEL);
			hardware.coinDispensers.get(new BigDecimal(0.05)).load(NICKEL);
			
			hardware.coinDispensers.get(new BigDecimal(0.10)).load(DIME);
			hardware.coinDispensers.get(new BigDecimal(0.10)).load(DIME);
			
			hardware.coinDispensers.get(new BigDecimal(0.25)).load(QUARTER);
			hardware.coinDispensers.get(new BigDecimal(0.25)).load(QUARTER);
			hardware.coinDispensers.get(new BigDecimal(0.25)).load(QUARTER);
			hardware.coinDispensers.get(new BigDecimal(0.25)).load(QUARTER);
			
			hardware.coinDispensers.get(new BigDecimal(1.00)).load(DOLLAR);
			hardware.coinDispensers.get(new BigDecimal(1.00)).load(DOLLAR);
			
			hardware.coinDispensers.get(new BigDecimal(2.00)).load(TWONIE);
			hardware.coinDispensers.get(new BigDecimal(1.00)).load(TWONIE);
			
			}catch(Exception e) {
				fail("Error loading Dispensers");
			}
		
		sm.dispenseChange(0.83,hardware, CAD,bsl); //Attempts to provide change
		
		//testing 0.83 should round to 0.85: Expects 3 $0.25 coins and 1 $0.10 coin
		assertEquals(0,cdl_twonie.getAmountEmitted());
		assertEquals(0,cdl_dollar.getAmountEmitted());
		assertEquals(3,cdl_quarter.getAmountEmitted());
		assertEquals(1,cdl_dime.getAmountEmitted());
		assertEquals(0,cdl_nickel.getAmountEmitted());
		assertEquals(0,bdl_five.getAmountEmitted());
		
	}
	
	/**
	 * A test which requires multiple banknotes to be given as change
	 */
	@Test
	public void testMultipleBanknoteChange() {
		
		BigDecimal[] coinDenom = {new BigDecimal(0.05)};
		int[] banknoteDenom = {5,10,20,50,100};
		SelfCheckoutStation hardware = new SelfCheckoutStation(CAD,banknoteDenom,coinDenom,MAX_WEIGHT,SENS);
		
		bsl.setMachine(sm);
		
		bsl.setHardware(hardware);
		hardware.banknoteOutput.register(bsl);
		hardware.banknoteOutput.register(bsg);
		
		//Registers listeners for each dispenser
		hardware.banknoteDispensers.get(100).register(bdl_hundo);
		hardware.banknoteDispensers.get(50).register(bdl_fifty);
		hardware.banknoteDispensers.get(20).register(bdl_twenty);
		hardware.banknoteDispensers.get(10).register(bdl_ten);
		hardware.banknoteDispensers.get(5).register(bdl_five);
		hardware.coinDispensers.get(new BigDecimal(0.05)).register(cdl_nickel);
		
		
		//Loads hardware dispensers
		try {
			
			hardware.banknoteDispensers.get(100).load(HUNDO);
			hardware.banknoteDispensers.get(100).load(HUNDO);
			hardware.banknoteDispensers.get(100).load(HUNDO);
			
			hardware.banknoteDispensers.get(50).load(FIFTY);
			hardware.banknoteDispensers.get(50).load(FIFTY);
			hardware.banknoteDispensers.get(50).load(FIFTY);
			
			
			hardware.banknoteDispensers.get(20).load(TWENTY);
			hardware.banknoteDispensers.get(20).load(TWENTY);
			hardware.banknoteDispensers.get(20).load(TWENTY);
			hardware.banknoteDispensers.get(20).load(TWENTY);
			
			hardware.banknoteDispensers.get(10).load(TEN);
			hardware.banknoteDispensers.get(10).load(TEN);
			hardware.banknoteDispensers.get(10).load(TEN);
			hardware.banknoteDispensers.get(10).load(TEN);
			
			hardware.banknoteDispensers.get(5).load(FIVE);
			hardware.banknoteDispensers.get(5).load(FIVE);
			hardware.banknoteDispensers.get(5).load(FIVE);
			hardware.banknoteDispensers.get(5).load(FIVE);
			
			hardware.coinDispensers.get(new BigDecimal(0.05)).load(NICKEL);
		
		}catch(Exception e) {
			fail("Error loading Dispensers");
		}
		
		sm.dispenseChange(275.00,hardware, CAD,bsl); //Attempts to provide change
		
		//Expects 2 $100, 1 $50, 1 $20, and 1 $5 banknotes to be returned
		assertEquals(2,bdl_hundo.getAmountEmitted());
		assertEquals(1,bdl_fifty.getAmountEmitted());
		assertEquals(1,bdl_twenty.getAmountEmitted());
		assertEquals(0,bdl_ten.getAmountEmitted());
		assertEquals(1,bdl_five.getAmountEmitted());
		assertEquals(0,cdl_nickel.getAmountEmitted());
	}
	
	/**
	 * This tests when a lot of different types of change is to be returned
	 * Gives the amount $218.65
	 */
	@Test
	public void testTonsOfDifferentBanknotesAndCoins() {
		BigDecimal[] coinDenom = {new BigDecimal(0.25),new BigDecimal(0.10), new BigDecimal(0.05),new BigDecimal(1.00),new BigDecimal(2.00)};
		int[] banknoteDenom = {20,10,5,50,100};
		SelfCheckoutStation hardware = new SelfCheckoutStation(CAD,banknoteDenom,coinDenom,MAX_WEIGHT,SENS);
		
		bsl.setMachine(sm);
		
		bsl.setHardware(hardware);
		hardware.banknoteOutput.register(bsl);
		hardware.banknoteOutput.register(bsg);
		
		//Registers dispenser listeners
		hardware.coinDispensers.get(new BigDecimal(0.05)).register(cdl_nickel);
		hardware.coinDispensers.get(new BigDecimal(0.10)).register(cdl_dime);
		hardware.coinDispensers.get(new BigDecimal(0.25)).register(cdl_quarter);
		hardware.coinDispensers.get(new BigDecimal(1.00)).register(cdl_dollar);
		hardware.coinDispensers.get(new BigDecimal(2.00)).register(cdl_twonie);
		
		hardware.banknoteDispensers.get(100).register(bdl_hundo);
		hardware.banknoteDispensers.get(50).register(bdl_fifty);
		hardware.banknoteDispensers.get(20).register(bdl_twenty);
		hardware.banknoteDispensers.get(10).register(bdl_ten);
		hardware.banknoteDispensers.get(5).register(bdl_five);
		
		
		//Loads hardware dispensers
		try {
			
			hardware.banknoteDispensers.get(100).load(HUNDO);
			hardware.banknoteDispensers.get(100).load(HUNDO);
			hardware.banknoteDispensers.get(100).load(HUNDO);
			
			hardware.banknoteDispensers.get(50).load(FIFTY);
			hardware.banknoteDispensers.get(50).load(FIFTY);
			hardware.banknoteDispensers.get(50).load(FIFTY);
			
			
			hardware.banknoteDispensers.get(20).load(TWENTY);
			hardware.banknoteDispensers.get(20).load(TWENTY);
			hardware.banknoteDispensers.get(20).load(TWENTY);
			hardware.banknoteDispensers.get(20).load(TWENTY);
			
			hardware.banknoteDispensers.get(10).load(TEN);
			hardware.banknoteDispensers.get(10).load(TEN);
			hardware.banknoteDispensers.get(10).load(TEN);
			hardware.banknoteDispensers.get(10).load(TEN);
			
			hardware.banknoteDispensers.get(5).load(FIVE);
			hardware.banknoteDispensers.get(5).load(FIVE);
			hardware.banknoteDispensers.get(5).load(FIVE);
			hardware.banknoteDispensers.get(5).load(FIVE);
			
			hardware.coinDispensers.get(new BigDecimal(0.05)).load(NICKEL);
			hardware.coinDispensers.get(new BigDecimal(0.05)).load(NICKEL);
			
			hardware.coinDispensers.get(new BigDecimal(0.10)).load(DIME);
			hardware.coinDispensers.get(new BigDecimal(0.10)).load(DIME);
			
			hardware.coinDispensers.get(new BigDecimal(0.25)).load(QUARTER);
			hardware.coinDispensers.get(new BigDecimal(0.25)).load(QUARTER);
			hardware.coinDispensers.get(new BigDecimal(0.25)).load(QUARTER);
			hardware.coinDispensers.get(new BigDecimal(0.25)).load(QUARTER);
			
			hardware.coinDispensers.get(new BigDecimal(1.00)).load(DOLLAR);
			hardware.coinDispensers.get(new BigDecimal(1.00)).load(DOLLAR);
			
			hardware.coinDispensers.get(new BigDecimal(2.00)).load(TWONIE);
			hardware.coinDispensers.get(new BigDecimal(1.00)).load(TWONIE);
			
			hardware.coinDispensers.get(new BigDecimal(0.05)).load(NICKEL);
		
		}catch(Exception e) {
			fail("Error loading Dispensers");
		}

		sm.dispenseChange(218.65,hardware, CAD,bsl); //Attempts to deposit change
		
		
		//Expects 2 $100, 1 $10, 1 $5, 1 $2.00, 1 $1.00, 2 $0.25, 1 $0.10,and 1 $0.05
		assertEquals(2,bdl_hundo.getAmountEmitted());
		assertEquals(0,bdl_fifty.getAmountEmitted());
		assertEquals(0,bdl_twenty.getAmountEmitted());
		assertEquals(1,bdl_ten.getAmountEmitted());
		assertEquals(1,bdl_five.getAmountEmitted());
		
		assertEquals(1,cdl_twonie.getAmountEmitted());
		assertEquals(1,cdl_dollar.getAmountEmitted());
		assertEquals(2,cdl_quarter.getAmountEmitted());
		assertEquals(1,cdl_dime.getAmountEmitted());
		assertEquals(1,cdl_nickel.getAmountEmitted());
		

	
		
	}
	
	/**
	 * Tests what happens when large denominations aren't all loaded fully,
	 * expects next largest denominations to be provided
	 */
	@Test
	public void testRunOutOfLargeDenom() {
		BigDecimal[] coinDenom = {new BigDecimal(0.25),new BigDecimal(0.10), new BigDecimal(0.05),new BigDecimal(1.00),new BigDecimal(2.00)};
		int[] banknoteDenom = {20,10,5,50,100};
		SelfCheckoutStation hardware = new SelfCheckoutStation(CAD,banknoteDenom,coinDenom,MAX_WEIGHT,SENS);
		
		bsl.setMachine(sm);
		
		bsl.setHardware(hardware);
		hardware.banknoteOutput.register(bsl);
		hardware.banknoteOutput.register(bsg);
		
		//Registers dispenser listeners
		hardware.coinDispensers.get(new BigDecimal(0.05)).register(cdl_nickel);
		hardware.coinDispensers.get(new BigDecimal(0.10)).register(cdl_dime);
		hardware.coinDispensers.get(new BigDecimal(0.25)).register(cdl_quarter);
		hardware.coinDispensers.get(new BigDecimal(1.00)).register(cdl_dollar);
		hardware.coinDispensers.get(new BigDecimal(2.00)).register(cdl_twonie);
		
		hardware.banknoteDispensers.get(100).register(bdl_hundo);
		hardware.banknoteDispensers.get(50).register(bdl_fifty);
		hardware.banknoteDispensers.get(20).register(bdl_twenty);
		hardware.banknoteDispensers.get(10).register(bdl_ten);
		hardware.banknoteDispensers.get(5).register(bdl_five);
		
		
		//Loads hardware dispensers
		try {
			
			hardware.banknoteDispensers.get(100).load(HUNDO);
			
			hardware.banknoteDispensers.get(50).load(FIFTY);
			hardware.banknoteDispensers.get(50).load(FIFTY);
			hardware.banknoteDispensers.get(50).load(FIFTY);
			
			
			hardware.banknoteDispensers.get(20).load(TWENTY);
			hardware.banknoteDispensers.get(20).load(TWENTY);
			hardware.banknoteDispensers.get(20).load(TWENTY);
			hardware.banknoteDispensers.get(20).load(TWENTY);
			
			hardware.banknoteDispensers.get(10).load(TEN);
			hardware.banknoteDispensers.get(10).load(TEN);
			hardware.banknoteDispensers.get(10).load(TEN);
			hardware.banknoteDispensers.get(10).load(TEN);
			
			hardware.banknoteDispensers.get(5).load(FIVE);
			hardware.banknoteDispensers.get(5).load(FIVE);
			hardware.banknoteDispensers.get(5).load(FIVE);
			hardware.banknoteDispensers.get(5).load(FIVE);
			
			hardware.coinDispensers.get(new BigDecimal(0.05)).load(NICKEL);
			hardware.coinDispensers.get(new BigDecimal(0.05)).load(NICKEL);
			
			hardware.coinDispensers.get(new BigDecimal(0.10)).load(DIME);
			hardware.coinDispensers.get(new BigDecimal(0.10)).load(DIME);
			
			hardware.coinDispensers.get(new BigDecimal(0.25)).load(QUARTER);
			
			hardware.coinDispensers.get(new BigDecimal(1.00)).load(DOLLAR);
			hardware.coinDispensers.get(new BigDecimal(1.00)).load(DOLLAR);
			hardware.coinDispensers.get(new BigDecimal(1.00)).load(DOLLAR);
			hardware.coinDispensers.get(new BigDecimal(1.00)).load(DOLLAR);
			
			
			
			hardware.coinDispensers.get(new BigDecimal(0.05)).load(NICKEL);
		
		}catch(Exception e) {
			fail("Error loading Dispensers");
		}

		
		sm.dispenseChange(318.50,hardware, CAD,bsl); //Attempts to deposit change
		
		
		//Expects 1 $100, 3 $50, 3 $20, 1 $5, 3 $1.00, 1 $0.25, 2 $0.10, 1 $0.05
		assertEquals(1,bdl_hundo.getAmountEmitted());
		assertEquals(3,bdl_fifty.getAmountEmitted());
		assertEquals(3,bdl_twenty.getAmountEmitted());
		assertEquals(0,bdl_ten.getAmountEmitted());
		assertEquals(1,bdl_five.getAmountEmitted());
		
		assertEquals(0,cdl_twonie.getAmountEmitted());
		assertEquals(3,cdl_dollar.getAmountEmitted());
		assertEquals(1,cdl_quarter.getAmountEmitted());
		assertEquals(2,cdl_dime.getAmountEmitted());
		assertEquals(1,cdl_nickel.getAmountEmitted());
		

	
		
	}
	
	/**
	 * Tests what happens when large denominations dispensers are disabled,
	 * expects next largest denominations to be provided
	 */
	@Test
	public void testRunOutOfLargeDenomDueToDisabled() {
		BigDecimal[] coinDenom = {new BigDecimal(0.25),new BigDecimal(0.10), new BigDecimal(0.05),new BigDecimal(1.00),new BigDecimal(2.00)};
		int[] banknoteDenom = {20,10,5,50,100};
		SelfCheckoutStation hardware = new SelfCheckoutStation(CAD,banknoteDenom,coinDenom,MAX_WEIGHT,SENS);
		
		bsl.setMachine(sm);
		
		bsl.setHardware(hardware);
		hardware.banknoteOutput.register(bsl);
		hardware.banknoteOutput.register(bsg);
		
		//Registers dispenser listeners
		hardware.coinDispensers.get(new BigDecimal(0.05)).register(cdl_nickel);
		hardware.coinDispensers.get(new BigDecimal(0.10)).register(cdl_dime);
		hardware.coinDispensers.get(new BigDecimal(0.25)).register(cdl_quarter);
		hardware.coinDispensers.get(new BigDecimal(1.00)).register(cdl_dollar);
		hardware.coinDispensers.get(new BigDecimal(2.00)).register(cdl_twonie);
		
		hardware.banknoteDispensers.get(100).register(bdl_hundo);
		hardware.banknoteDispensers.get(50).register(bdl_fifty);
		hardware.banknoteDispensers.get(20).register(bdl_twenty);
		hardware.banknoteDispensers.get(10).register(bdl_ten);
		hardware.banknoteDispensers.get(5).register(bdl_five);
		
		
		//Loads hardware dispensers
		try {
			
			hardware.banknoteDispensers.get(100).load(HUNDO);
			hardware.banknoteDispensers.get(100).load(HUNDO);
			
			hardware.banknoteDispensers.get(50).load(FIFTY);
			hardware.banknoteDispensers.get(50).load(FIFTY);
			hardware.banknoteDispensers.get(50).load(FIFTY);
			hardware.banknoteDispensers.get(50).load(FIFTY);
			hardware.banknoteDispensers.get(50).load(FIFTY);
			
			
			hardware.banknoteDispensers.get(20).load(TWENTY);
			hardware.banknoteDispensers.get(20).load(TWENTY);
			hardware.banknoteDispensers.get(20).load(TWENTY);
			hardware.banknoteDispensers.get(20).load(TWENTY);
			
			hardware.banknoteDispensers.get(10).load(TEN);
			hardware.banknoteDispensers.get(10).load(TEN);
			hardware.banknoteDispensers.get(10).load(TEN);
			hardware.banknoteDispensers.get(10).load(TEN);
			
			hardware.banknoteDispensers.get(5).load(FIVE);
			hardware.banknoteDispensers.get(5).load(FIVE);
			hardware.banknoteDispensers.get(5).load(FIVE);
			hardware.banknoteDispensers.get(5).load(FIVE);
			
			hardware.coinDispensers.get(new BigDecimal(0.05)).load(NICKEL);
			hardware.coinDispensers.get(new BigDecimal(0.05)).load(NICKEL);
			
			hardware.coinDispensers.get(new BigDecimal(0.10)).load(DIME);
			hardware.coinDispensers.get(new BigDecimal(0.10)).load(DIME);
			
			hardware.coinDispensers.get(new BigDecimal(0.25)).load(QUARTER);
			
			hardware.coinDispensers.get(new BigDecimal(1.00)).load(DOLLAR);
			hardware.coinDispensers.get(new BigDecimal(1.00)).load(DOLLAR);
			hardware.coinDispensers.get(new BigDecimal(1.00)).load(DOLLAR);
			hardware.coinDispensers.get(new BigDecimal(1.00)).load(DOLLAR);
			
			
			
			hardware.coinDispensers.get(new BigDecimal(0.05)).load(NICKEL);
		
		}catch(Exception e) {
			fail("Error loading Dispensers");
		}

		hardware.banknoteDispensers.get(100).disable(); //Disables $100 dispenser
		
		sm.dispenseChange(318.50,hardware, CAD,bsl); //Attempts to deposit change
		
		
		//Expects 5 $50, 3 $20, 1 $5, 3 $1.00, 1 $0.25, 2 $0.10, 1 $0.05
		assertEquals(0,bdl_hundo.getAmountEmitted());
		assertEquals(5,bdl_fifty.getAmountEmitted());
		assertEquals(3,bdl_twenty.getAmountEmitted());
		assertEquals(0,bdl_ten.getAmountEmitted());
		assertEquals(1,bdl_five.getAmountEmitted());
		
		assertEquals(0,cdl_twonie.getAmountEmitted());
		assertEquals(3,cdl_dollar.getAmountEmitted());
		assertEquals(1,cdl_quarter.getAmountEmitted());
		assertEquals(2,cdl_dime.getAmountEmitted());
		assertEquals(1,cdl_nickel.getAmountEmitted());
		

	
		
	}
	
	/**
	 * Tests that change can be given for one of each denomination
	 */
	@Test
	public void testOneOfEach() {
		BigDecimal[] coinDenom = {new BigDecimal(0.25),new BigDecimal(0.10), new BigDecimal(0.05),new BigDecimal(1.00),new BigDecimal(2.00)};
		int[] banknoteDenom = {20,10,5,50,100};
		SelfCheckoutStation hardware = new SelfCheckoutStation(CAD,banknoteDenom,coinDenom,MAX_WEIGHT,SENS);
		
		bsl.setMachine(sm);
		
		bsl.setHardware(hardware);
		hardware.banknoteOutput.register(bsl);
		hardware.banknoteOutput.register(bsg);
		
		hardware.coinDispensers.get(new BigDecimal(0.05)).register(cdl_nickel);
		hardware.coinDispensers.get(new BigDecimal(0.10)).register(cdl_dime);
		hardware.coinDispensers.get(new BigDecimal(0.25)).register(cdl_quarter);
		hardware.coinDispensers.get(new BigDecimal(1.00)).register(cdl_dollar);
		hardware.coinDispensers.get(new BigDecimal(2.00)).register(cdl_twonie);
		
		hardware.banknoteDispensers.get(100).register(bdl_hundo);
		hardware.banknoteDispensers.get(50).register(bdl_fifty);
		hardware.banknoteDispensers.get(20).register(bdl_twenty);
		hardware.banknoteDispensers.get(10).register(bdl_ten);
		hardware.banknoteDispensers.get(5).register(bdl_five);
		
		//Loads hardware dispensers
		try {
			
			hardware.banknoteDispensers.get(100).load(HUNDO);
			
			hardware.banknoteDispensers.get(50).load(FIFTY);
			
			
			hardware.banknoteDispensers.get(20).load(TWENTY);
			
			hardware.banknoteDispensers.get(10).load(TEN);
			
			hardware.banknoteDispensers.get(5).load(FIVE);
			
			hardware.coinDispensers.get(new BigDecimal(0.05)).load(NICKEL);
			
			hardware.coinDispensers.get(new BigDecimal(0.10)).load(DIME);
			
			hardware.coinDispensers.get(new BigDecimal(0.25)).load(QUARTER);
			
			hardware.coinDispensers.get(new BigDecimal(1.00)).load(DOLLAR);
			
			hardware.coinDispensers.get(new BigDecimal(2.00)).load(TWONIE);
			
			
			
			hardware.coinDispensers.get(new BigDecimal(0.05)).load(NICKEL);
		
		}catch(Exception e) {
			fail("Error loading Dispensers");
		}

		
		sm.dispenseChange(188.40,hardware, CAD,bsl); //Attempts to deposit change
		
		

		//Expects one of each denomination to be returned
		assertEquals(1,bdl_hundo.getAmountEmitted());
		assertEquals(1,bdl_fifty.getAmountEmitted());
		assertEquals(1,bdl_twenty.getAmountEmitted());
		assertEquals(1,bdl_ten.getAmountEmitted());
		assertEquals(1,bdl_five.getAmountEmitted());
		
		assertEquals(1,cdl_twonie.getAmountEmitted());
		assertEquals(1,cdl_dollar.getAmountEmitted());
		assertEquals(1,cdl_quarter.getAmountEmitted());
		assertEquals(1,cdl_dime.getAmountEmitted());
		assertEquals(1,cdl_nickel.getAmountEmitted());
		

	
		
	}
	
	
	/**
	 * Test what happens when there is not enough denominations to satisfy a change request.
	 * Expects SimulationError to be thrown
	 */
	@Test
	public void testUnableToProvideChange() {
		BigDecimal[] coinDenom = {new BigDecimal(0.25),new BigDecimal(0.10), new BigDecimal(0.05),new BigDecimal(1.00),new BigDecimal(2.00)};
		int[] banknoteDenom = {20,10,5,50,100};
		SelfCheckoutStation hardware = new SelfCheckoutStation(CAD,banknoteDenom,coinDenom,MAX_WEIGHT,SENS);
		
		bsl.setMachine(sm);
		
		bsl.setHardware(hardware);
		hardware.banknoteOutput.register(bsl);
		hardware.banknoteOutput.register(bsg);
		
		hardware.coinDispensers.get(new BigDecimal(0.05)).register(cdl_nickel);
		hardware.coinDispensers.get(new BigDecimal(0.10)).register(cdl_dime);
		hardware.coinDispensers.get(new BigDecimal(0.25)).register(cdl_quarter);
		hardware.coinDispensers.get(new BigDecimal(1.00)).register(cdl_dollar);
		hardware.coinDispensers.get(new BigDecimal(2.00)).register(cdl_twonie);
		
		hardware.banknoteDispensers.get(100).register(bdl_hundo);
		hardware.banknoteDispensers.get(50).register(bdl_fifty);
		hardware.banknoteDispensers.get(20).register(bdl_twenty);
		hardware.banknoteDispensers.get(10).register(bdl_ten);
		hardware.banknoteDispensers.get(5).register(bdl_five);
		
		//Loads hardware dispensers
		try {
			
			hardware.banknoteDispensers.get(100).load(HUNDO);
			
			
			hardware.coinDispensers.get(new BigDecimal(0.05)).load(NICKEL);
			
			hardware.coinDispensers.get(new BigDecimal(0.10)).load(DIME);
			
			hardware.coinDispensers.get(new BigDecimal(0.25)).load(QUARTER);
			
			hardware.coinDispensers.get(new BigDecimal(1.00)).load(DOLLAR);
			
			hardware.coinDispensers.get(new BigDecimal(2.00)).load(TWONIE);
			
		
		}catch(Exception e) {
			fail("Error loading Dispensers");
		}

		//Expects exception to be thrown
		assertThrows("Testing dispenseChange with not enough loaded: expecting SimulationException",SimulationException.class, () -> {
			sm.dispenseChange(188.40,hardware, CAD,bsl);
		});

	
		
	}
	
	
	/**
	 * Tests what happens when a null value for SelfCheckoutStation is given: Expects SimulationException to be thrown
	 */
	@Test
	public void testNullHardware() {
		assertThrows("Testing dispenseChange with null hardware: expecting SimulationException",SimulationException.class, () -> {
			sm.dispenseChange(188,null, CAD,bsl);
		});
	}
	
	/**
	 * Tests what happens when a null value for BanknoteSlotListener is given: Expects SimulationException to be thrown
	 */
	@Test
	public void testNullListener() {
		BigDecimal[] coinDenom = {new BigDecimal(0.25),new BigDecimal(0.10), new BigDecimal(0.05),new BigDecimal(1.00),new BigDecimal(2.00)};
		int[] banknoteDenom = {20,10,5,50,100};
		SelfCheckoutStation hardware = new SelfCheckoutStation(CAD,banknoteDenom,coinDenom,MAX_WEIGHT,SENS);
		
		
		assertThrows("Testing dispenseChange with null hardware: expecting SimulationException",SimulationException.class, () -> {
			sm.dispenseChange(188,hardware, CAD,null);
		});
	}
	
	
	/**
	 * Tests what happens when 0.00 is given as the amount of change required, expects nothing to be given to the customer
	 */
	@Test
	public void testZeroAmount() {
		BigDecimal[] coinDenom = {new BigDecimal(0.25),new BigDecimal(0.10), new BigDecimal(0.05),new BigDecimal(1.00),new BigDecimal(2.00)};
		int[] banknoteDenom = {20,10,5,50,100};
		SelfCheckoutStation hardware = new SelfCheckoutStation(CAD,banknoteDenom,coinDenom,MAX_WEIGHT,SENS);
		
		bsl.setMachine(sm);
		
		bsl.setHardware(hardware);
		hardware.banknoteOutput.register(bsl);
		hardware.banknoteOutput.register(bsg);
		
		hardware.coinDispensers.get(new BigDecimal(0.05)).register(cdl_nickel);
		hardware.coinDispensers.get(new BigDecimal(0.10)).register(cdl_dime);
		hardware.coinDispensers.get(new BigDecimal(0.25)).register(cdl_quarter);
		hardware.coinDispensers.get(new BigDecimal(1.00)).register(cdl_dollar);
		hardware.coinDispensers.get(new BigDecimal(2.00)).register(cdl_twonie);
		
		hardware.banknoteDispensers.get(100).register(bdl_hundo);
		hardware.banknoteDispensers.get(50).register(bdl_fifty);
		hardware.banknoteDispensers.get(20).register(bdl_twenty);
		hardware.banknoteDispensers.get(10).register(bdl_ten);
		hardware.banknoteDispensers.get(5).register(bdl_five);
		
		//Loads hardware dispensers
		try {
			
			hardware.banknoteDispensers.get(100).load(HUNDO);
			
			hardware.banknoteDispensers.get(50).load(FIFTY);
			
			
			hardware.banknoteDispensers.get(20).load(TWENTY);
			
			hardware.banknoteDispensers.get(10).load(TEN);
			
			hardware.banknoteDispensers.get(5).load(FIVE);
			
			hardware.coinDispensers.get(new BigDecimal(0.05)).load(NICKEL);
			
			hardware.coinDispensers.get(new BigDecimal(0.10)).load(DIME);
			
			hardware.coinDispensers.get(new BigDecimal(0.25)).load(QUARTER);
			
			hardware.coinDispensers.get(new BigDecimal(1.00)).load(DOLLAR);
			
			hardware.coinDispensers.get(new BigDecimal(2.00)).load(TWONIE);
			
			
			
			hardware.coinDispensers.get(new BigDecimal(0.05)).load(NICKEL);
		
		}catch(Exception e) {
			fail("Error loading Dispensers");
		}

		
		sm.dispenseChange(0.00,hardware, CAD,bsl); //Attempts to deposit 0 change
		
		

		//Expects nothing to be emitted
		assertEquals(0,bdl_hundo.getAmountEmitted());
		assertEquals(0,bdl_fifty.getAmountEmitted());
		assertEquals(0,bdl_twenty.getAmountEmitted());
		assertEquals(0,bdl_ten.getAmountEmitted());
		assertEquals(0,bdl_five.getAmountEmitted());
		
		assertEquals(0,cdl_twonie.getAmountEmitted());
		assertEquals(0,cdl_dollar.getAmountEmitted());
		assertEquals(0,cdl_quarter.getAmountEmitted());
		assertEquals(0,cdl_dime.getAmountEmitted());
		assertEquals(0,cdl_nickel.getAmountEmitted());
		

	
		
	}
	

}
