package org.lsmr.SelfCheckoutSoftwareTest;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.SelfCheckoutSoftware.BanknoteDispenserListenerImplement;
import org.lsmr.SelfCheckoutSoftware.SelfCheckoutMachine;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.ChipFailureException;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.MagneticStripeFailureException;
import org.lsmr.selfcheckout.PLUCodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.BanknoteDispenser;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.external.CardIssuer;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

public class SelfCheckoutMachineTest {
	/**
	 * This set of tests is for any aspects of the SelfCheckoutMachine not
	 * covered by other test suites
	 */
	SelfCheckoutMachine cMachine;
	
	// PLU Code Attributes
	Map<PriceLookupCode, PLUCodedProduct> pluProductDb;
	PriceLookupCode bannanaPLU;
	PriceLookupCode applePLU;
	PLUCodedProduct bannanaPLUProduct;
	PLUCodedProduct applePLUProduct;
	PLUCodedItem  bannanaPLUItem;
	PLUCodedItem  applePLUItem;
	
	
	// Barcode attributes
	Barcode bC;
	BarcodedProduct bp1;
	BarcodedItem bi1;
	Map<Barcode, BarcodedProduct> bcProductDb;
	
	@Before
	public void setUp() {
		cMachine = new SelfCheckoutMachine();
		
		// Setup PLU test attributes
		pluProductDb = ProductDatabases.PLU_PRODUCT_DATABASE;
		pluProductDb.clear(); // Ensure only newly entered items are in the pluProductDb
		bannanaPLU = new PriceLookupCode("4011");
		applePLU = new PriceLookupCode("4128");
		bannanaPLUProduct = new PLUCodedProduct(bannanaPLU, "Bannana", new BigDecimal(2.50));
		applePLUProduct = new PLUCodedProduct(applePLU, "Apple", new BigDecimal(1.25));
		
		bannanaPLUItem = new PLUCodedItem(bannanaPLU, 2.00);
		applePLUItem = new PLUCodedItem(applePLU, 1.00);
		
		// Setup Barcode test attributes
		bC = new Barcode("15");
		bi1 = new BarcodedItem(bC, 15.00);
		bp1 = new BarcodedProduct(bC, "Description", new BigDecimal(5.00));
		bcProductDb = ProductDatabases.BARCODED_PRODUCT_DATABASE;
		bcProductDb.clear(); // Clear the productDb so it's only the new products in the db
		bcProductDb.put(bC, bp1);
		
		
	}

	@Test(expected=SimulationException.class)
	public final void enterPLUCodeNotInDB() throws SimulationException, OverloadException {
		cMachine.PLUEnter(bannanaPLU);
	}
	
	@Test
	public final void enterOneValidPLUCode() {
		pluProductDb.put(bannanaPLU, bannanaPLUProduct);
		try {
			cMachine.PLUEnter(bannanaPLU);
		} catch (SimulationException e) {

			System.out.println("Sim exception");
		} catch (OverloadException e) {

			System.out.println("Overload exception");
		}
	}

	@Test
	public final void enterTwoValidPLUCodes() throws SimulationException, OverloadException {
		pluProductDb.put(bannanaPLU, bannanaPLUProduct);
		pluProductDb.put(applePLU, applePLUProduct);
		
		cMachine.checkoutStation.scale.add(bannanaPLUItem);
		cMachine.PLUEnter(bannanaPLU);
		cMachine.checkoutStation.scale.remove(bannanaPLUItem);
		cMachine.checkoutStation.scale.add(applePLUItem);
		cMachine.PLUEnter(applePLU);
		cMachine.checkoutStation.scale.remove(applePLUItem);
	}
	
	
	@Test
	public final void enterSamePLUCodes() throws SimulationException, OverloadException {
		pluProductDb.put(bannanaPLU, bannanaPLUProduct);
		cMachine.checkoutStation.scale.add(bannanaPLUItem);
		cMachine.PLUEnter(bannanaPLU);
		cMachine.checkoutStation.scale.remove(bannanaPLUItem);
		cMachine.checkoutStation.scale.add(bannanaPLUItem);
		cMachine.PLUEnter(bannanaPLU);
		cMachine.checkoutStation.scale.remove(bannanaPLUItem);
		cMachine.checkoutStation.scale.add(bannanaPLUItem);
		cMachine.PLUEnter(bannanaPLU);
		cMachine.checkoutStation.scale.remove(bannanaPLUItem);
	}
	
	@Test
	public final void customerEntersBags() {
		cMachine.currently_scanning = false;
		cMachine.waitingToPlaceItem = false;
		cMachine.enterNumberOfPlasticBags(10);
		assertEquals(cMachine.numberOfPlasticBags, 10);
	}
	
	@Test(expected=SimulationException.class)
	public final void customerEntersBagsInvalid() {
		cMachine.enterNumberOfPlasticBags(-10);
	}
	
	@Test
	public final void customerEntersBagsWhileStillScanning() {
		cMachine.currently_scanning = true;
		cMachine.waitingToPlaceItem = true;
		
		// Should not work since cMachine.currently_scanning and cMachine.waitingToPlaceItem are true
		cMachine.enterNumberOfPlasticBags(10);
		assertFalse(cMachine.numberOfPlasticBags == 10);
		
		// Should not work since cMachine.waitingToPlaceItem is true
		cMachine.currently_scanning = false;
		cMachine.enterNumberOfPlasticBags(10);
		assertFalse(cMachine.numberOfPlasticBags == 10);
		cMachine.currently_scanning = true; // reset back to true
		
		// Should not work since cMachine.currently_scanning is true
		cMachine.waitingToPlaceItem = false;
		cMachine.enterNumberOfPlasticBags(10);
		assertFalse(cMachine.numberOfPlasticBags == 10);
		cMachine.waitingToPlaceItem = true;
	}
	
	@Test
	public final void doNotBagAScannedItemErrors() {
		try {
		cMachine.doNotBagAScannedItem(null, null);
		fail("You cannot opt out of bagging null items");
		}
		catch(SimulationException e) {
			// This should happen
		}
		
		Barcode bC2 = new Barcode("23");
		BarcodedItem bi2 = new BarcodedItem(bC2, 15.00);
		
		try {
			cMachine.doNotBagAScannedItem(bi2, cMachine.checkoutStation);
			fail("You cannot opt out of bagging non-existant items");
		}catch(SimulationException e) {
			// This should happen
		}
		
	}
	
	
	@Test
	public final void doNotBagAScannedItemSingle() {
		// Since scanning is probabalistic I just do it a few times
		cMachine.scanItem(bi1);
		cMachine.scanItem(bi1);
		cMachine.scanItem(bi1);
		
		cMachine.doNotBagAScannedItem(bi1, cMachine.checkoutStation);
	}
	
	@Test
	public final void doNotBagAScannedItemMultiple() {
		// Create second item
		Barcode bC2 = new Barcode("23");
		BarcodedItem bi2 = new BarcodedItem(bC2, 15.00);
		BarcodedProduct bp2 = new BarcodedProduct(bC2, "Second barcoded product", new BigDecimal(10.5));
		bcProductDb.put(bC2, bp2);
		
		// Scan both existing items
		cMachine.scanItem(bi1);
		cMachine.scanItem(bi1);
		cMachine.scanItem(bi1);
		cMachine.doNotBagAScannedItem(bi1, cMachine.checkoutStation);
		cMachine.scanItem(bi2);
		cMachine.scanItem(bi2);
		cMachine.scanItem(bi2);
		cMachine.doNotBagAScannedItem(bi2, cMachine.checkoutStation);
	}
	
	@Test
	public final void emptyBagScaleAfterPayment() throws OverloadException {
		assertTrue(cMachine.bagScaleEmptyAfterPaid(true, cMachine.checkoutStation));
	}
	
	@Test
	public final void emptyBagScaleAfterPaymentBeforePayment() throws OverloadException {
		assertFalse(cMachine.bagScaleEmptyAfterPaid(false, cMachine.checkoutStation));
	}
	
	@Test
	public final void bagScaleNotEmptyAfterPayment() throws OverloadException {
		cMachine.checkoutStation.baggingArea.add(bi1);
		assertFalse(cMachine.bagScaleEmptyAfterPaid(true, cMachine.checkoutStation));
	}
	
	@Test
	public final void ProductSearchValid() {
		pluProductDb.put(bannanaPLU, bannanaPLUProduct);
		pluProductDb.put(applePLU, applePLUProduct);
		
		assertTrue(cMachine.ProductSearch("B").get(0) == bannanaPLUProduct);
		assertTrue(cMachine.ProductSearch("Ba").get(0) == bannanaPLUProduct);
		assertTrue(cMachine.ProductSearch("Bannana").get(0) == bannanaPLUProduct);
		
		// A valid item is returned, but not the correct one
		assertFalse(cMachine.ProductSearch("A").get(0) == bannanaPLUProduct);
		
		
		// Multiple possible return values
		PriceLookupCode b2PLU = new PriceLookupCode("4201");
		PLUCodedProduct b2PLUProduct = new PLUCodedProduct(b2PLU, "Ban", new BigDecimal(3.50));
		pluProductDb.put(b2PLU, b2PLUProduct);
		assertTrue(cMachine.ProductSearch("Ba").size() == 2);
		assertTrue(cMachine.ProductSearch("Ba").get(0) == bannanaPLUProduct);
	}
	
	@Test(expected=SimulationException.class)
	public final void ProductSearchInvalid() {
		pluProductDb.put(bannanaPLU, bannanaPLUProduct);
		pluProductDb.put(applePLU, applePLUProduct);
		assertTrue(cMachine.ProductSearch("Q").size() == 0); // Should be no items
		
		// String longer than longest in set
		assertTrue(cMachine.ProductSearch("Bannanas").size() == 0);
		
		// Empty string
		assertTrue(cMachine.ProductSearch("").size() == 2);

		cMachine.ProductSearch(null); // Should error out
	}
	
	
	
	@Test
	public final void scanWhenBlocked() {
		cMachine.currently_scanning = false;
		cMachine.scanItem(bi1);
		assertEquals(cMachine.scanListener.getscannedBarcodes().size(), 0);
		
	}
	

	@Test
	public final void balanceTests() {
		cMachine.balance = 10.00;
		assertTrue(cMachine.getBalance() == 10.00);
		
	}
	
	@Test
	public final void payments() throws Exception {
	
		Banknote bn = new Banknote(20, cMachine.ACCEPTEDCURRENCY);
		cMachine.payWithBanknote(bn);
		
		Coin cn = new Coin(new BigDecimal(2.00), cMachine.ACCEPTEDCURRENCY);
		cMachine.payWithCoin(cn);
		
		Card c = new Card("debit", "123456789", "John Doe", "313", "1234", true, true);
		
		// Debit checks NOTE: many of these fail on probability
		try {
		cMachine.payWithDebit(c, "tap", "1234", null);
		}  catch (Exception e) {
			e.printStackTrace();
		}
		cMachine.removeCard();
		
		try {
		cMachine.payWithDebit(c, "insert", "1234", null);
		} catch(Exception e) {
			e.printStackTrace();
		}
		cMachine.removeCard();
		try {
		cMachine.payWithDebit(c, "swipe", "1234", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		cMachine.removeCard();
		
		cMachine.payWithDebit(c, "yeet", "1234", null); // Should do nothing
		
		// Credit checks NOTE: many of these fail on probability
		Card c1 = new Card("credit", "123456789", "John Doe", "313", "1234", true, true);
		CardIssuer ciSelected = new CardIssuer("mastercard");
		try {
		cMachine.payWithCredit(c1, ciSelected, "tap", "1234", null);
		}  catch (Exception e) {
			e.printStackTrace();
		} 
		cMachine.removeCard();
		try {
		cMachine.payWithCredit(c1, ciSelected, "insert", "1234", null);
		} catch (SimulationException e) {
			cMachine.removeCard();
		try {
			cMachine.payWithCredit(c1, ciSelected, "insert", "1234", null);
		} catch(Exception e2) {
			e2.printStackTrace();
		}
		}
		cMachine.removeCard();
		try {
		cMachine.payWithCredit(c1, ciSelected, "swipe", "1234", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		cMachine.removeCard();
		cMachine.payWithCredit(c1, ciSelected, "yeet", "1234", null); // Should do nothing
		
		// Membership checks NOTE: many of these fail on probability
		Card ms = new Card("Membership", "123456789", "John Doe", "313", "1234", true, true);
		cMachine.swipeMembershipCard(ms);
		cMachine.removeCard();
	}
	
	@Test(expected=SimulationException.class)
	public final void bankNoteEmissionFailure() {
		Banknote b = new Banknote(5, Currency.getInstance(Locale.CANADA));
		BanknoteDispenserListenerImplement bdli = new BanknoteDispenserListenerImplement();
		for(BanknoteDispenser dispenser : cMachine.checkoutStation.banknoteDispensers.values()) {
			bdli.banknoteAdded(dispenser, b);
			bdli.banknotesFull(dispenser);
			bdli.isEmitted();
			dispenser.disable();
		}
		cMachine.emitBanknote(cMachine.checkoutStation, -4);
	}
	
	@Test
	public final void transactionTotals() throws SimulationException, OverloadException {
		
		
		
		
		
		// Add items
		pluProductDb.put(bannanaPLU, bannanaPLUProduct);
		cMachine.checkoutStation.scale.add(bannanaPLUItem);
		cMachine.PLUEnter(bannanaPLU);
		cMachine.checkoutStation.scale.remove(bannanaPLUItem);
		pluProductDb.put(bannanaPLU, bannanaPLUProduct);
		cMachine.checkoutStation.scale.add(bannanaPLUItem);
		cMachine.PLUEnter(bannanaPLU);
		cMachine.checkoutStation.scale.remove(bannanaPLUItem);
		cMachine.scanItem(bi1);

		assertTrue(cMachine.getTransactionTotal() == 5.01);
		

	}
	
	@Test
	public final void multiScanandInventory() {
		ProductDatabases.INVENTORY.put(bp1, 5);
		cMachine.checkoutStation.scale.add(bi1);
		cMachine.scanItem(bi1);
		cMachine.checkoutStation.scale.remove(bi1);
		cMachine.checkoutStation.scale.add(bi1);
		cMachine.scanItem(bi1);
		cMachine.checkoutStation.scale.remove(bi1);
		cMachine.checkoutStation.scale.add(bi1);
		cMachine.scanItem(bi1);
		cMachine.checkoutStation.scale.remove(bi1);
		cMachine.checkoutStation.scale.add(bi1);
		cMachine.scanItem(bi1);
		cMachine.checkoutStation.scale.remove(bi1);
		assertTrue(cMachine.scanListener.getScannedTotal() == 15.00);
		
	}
	
	/**
	 * Tests the action of an attendant approving a weight discrepancy. Since the machine should
	 * be ready to scan the next item after a discrepancy is approved, the test will fail
	 * if this is not the case.
	 * 
	 * @author Jacob Stanich
	 */
	@Test
	public final void approveWeightDiscrepancy()
	{
		cMachine.waitingToPlaceItem = true;
		cMachine.attendantApprovesWeight();
		assertTrue("Machine should be ready to scan after customer approves weight discrepancy", !cMachine.waitingToPlaceItem);
	}
	
	/**
	 * Tests the action of an attendant refilling the station's receipt printer paper. If the
	 * machine indicates that it is still out of paper, the test will fail.
	 * 
	 * @author Jacob Stanich
	 */
	@Test
	public final void refillPrinterPaper()
	{
		cMachine.refillPaper();
		cMachine.refillPaper();
		assertTrue("Machine should not indicate that it is out of paper after being refilled", !cMachine.RPListener.getIsOutOfPaper());
	}
	
	/**
	 * Tests the action of an attendant refilling the station's receipt printer ink. If the
	 * machine indicates that it is still out of ink, the test will fail.
	 * 
	 * @author Jacob Stanich
	 */
	@Test
	public final void refillPrinterInk()
	{
		cMachine.refillInk();
		cMachine.refillInk();
		assertTrue("Machine should not indicate that it is out of ink after being refilled", !cMachine.RPListener.getIsOutOfInk());
	}
	
	/**
	 * Tests the action of an attendant blocking a station. If the station indicates that
	 * it is still ready to scan items after it has been blocked, the test will fail.
	 * 
	 * @author Jacob Stanich
	 */
	@Test
	public final void attendantBlocksStationTest() throws Exception
	{
		cMachine.currently_scanning = true;
		cMachine.waitingToPlaceItem = true;
		cMachine.attendantBlocksStation();
		assertTrue("Station should not be ready to scan after being blocked", !cMachine.currently_scanning && !cMachine.waitingToPlaceItem);
	}
	
	/**
	 * Tests the action of an attendant unblocking a station. If the station indicates that
	 * it is not ready to scan items after it has been unblocked, the test will fail.
	 * 
	 * @author Jacob Stanich
	 */
	@Test
	public final void attendantUnblocksStationTest() throws Exception
	{
		cMachine.currently_scanning = false;
		cMachine.waitingToPlaceItem = false;
		cMachine.attendantUnblocksStation();
		assertTrue("Station should be ready to scan after being unblocked", cMachine.currently_scanning && cMachine.waitingToPlaceItem);
	}
	
	/**
	 * Tests the action of an attendant unloading the coin storage device. If there
	 * are still coins in the storage unit afterwards, the test will fail.
	 * 
	 * @author Jacob Stanich
	 */
	@Test
	public final void attendantUnloadsCoinStorageTest()
	{
		cMachine.attendantEmptyCoinStorage();
		assertTrue("Storage unit should not be holding any coins after being emptied",
				cMachine.cStorageUnitListener.getStoredCoinCount() == 0);
	}
	
	/**
	 * Tests the action of an attendant unloading the banknote storage device. If there
	 * are still banknotes in the storage unit afterwards, the test will fail.
	 * 
	 * @author Jacob Stanich
	 */
	@Test
	public final void attendantUnloadsBanknoteStorageTest()
	{
		cMachine.attendantEmptyBanknoteStorage();
		assertTrue("Storage unit should not be holding any coins after being emptied",
				cMachine.bStorageUnitListener.getStoredBanknoteCount() == 0);
	}
	
	/**
	 * Tests the action of an attendant shutting down a station. If the station is still
	 * ready to scan items after being shut down, the test will fail.
	 * 
	 * @author Jacob Stanich
	 */
	@Test
	public final void attendantShutsDownStationTest()
	{
		cMachine.currently_scanning = true;
		cMachine.attendantShutsDownStation();
		assertTrue("Station should not be ready to scan after being shut down", !cMachine.currently_scanning);
	}
	
	/**
	 * Tests the action of an attendant starting a station. If the station is not
	 * ready to scan items after being started, the test will fail.
	 * 
	 * @author Jacob Stanich
	 */
	@Test
	public final void attendantStartsStationTest()
	{
		cMachine.currently_scanning = false;
		cMachine.attendantStartsStation();
		assertTrue("Station should be ready to scan after being started", cMachine.currently_scanning);
	}
	
	/**
	 * Tests the action of an attendant attempting to log in with a correct password.
	 * Since the password is correct, the test will fail if the attendant is not granted access.
	 * 
	 * @author Jacob Stanich
	 */
	@Test
	public final void attendantLogsInWithValidPassword()
	{
		boolean[] loginValues = cMachine.attendantLogsIn("CoopCalgary");
		assertTrue("Attendant be granted access when logging in with a correct password",
				loginValues[0] && loginValues[1]);
	}
	
	/**
	 * Tests the action of an attendant attempting to log in with an incorrect password.
	 * Since the password is incorrect, the test will fail if the attendant is granted access.
	 * 
	 * @author Jacob Stanich
	 */
	@Test
	public final void attendantLogsInWithInvalidPassword()
	{
		boolean[] loginValues = cMachine.attendantLogsIn("incorrectPass");
		assertTrue("Attendant should not be granted access when logging in with an incorrect password",
				!loginValues[0] && !loginValues[1]);
	}
	
	/**
	 * Tests the action of an attendant attempting to log out while they are currently logged in.
	 * If the machine indicates that the attendant is still logged in afterwards, the test will fail.
	 * 
	 * @author Jacob Stanich
	 */
	@Test
	public final void attendantLogsOutWhileLoggedIn()
	{
		cMachine.loggedIn = true;
		cMachine.accessGranted = true;
		
		assertTrue("Attendant should not be logged in after logging out",
				cMachine.attendantLogsOut(true) && !cMachine.loggedIn && !cMachine.accessGranted);
	}
	
	/**
	 * Tests the action of an attendant attempting to log out while they are note currently logged in.
	 * If the machine indicates that the attendant is logged in afterwards, the test will fail.
	 * (While this is not a likely situation to actually happen, it is important for the machine
	 * to ensure that the attendant is not marked as "logged in" after they log out)
	 * 
	 * @author Jacob Stanich
	 */
	@Test
	public final void attendantLogsOutWhileNotLoggedIn()
	{
		cMachine.loggedIn = false;
		cMachine.accessGranted = false;
		
		assertTrue("Attendant should not be logged in after logging out",
				!cMachine.attendantLogsOut(true) && !cMachine.loggedIn && !cMachine.accessGranted);
	}
	
	/**
	 * Tests the function for an attendant logging out when the function is given "false"
	 * as a parameter. The end result should be that the variables that indicates
	 * whether or not an attendant is logged in should not be changed.
	 * 
	 * @author Jacob Stanich
	 */
	@Test
	public final void attendantLogsOutWithFalseParameter()
	{
		cMachine.loggedIn = true;
		cMachine.accessGranted = true;
		
		assertTrue("Login variables should not be changed when the function parameter is false",
				!cMachine.attendantLogsOut(false) && cMachine.loggedIn && cMachine.accessGranted);
	}
	
	/**
	 * Tests the action of an attendant removing a product that is not in the cart.
	 * This test expects a SimulationException to be thrown.
	 * 
	 * @author Jacob Stanich
	 */
	@Test(expected=SimulationException.class)
	public final void attendantRemovesProductNotInCart()
	{
		cMachine.cart.clear();
		cMachine.attendantRemovesProduct(bp1);
		fail("SimulationException should have been thrown");
	}
	
	/**
	 * Tests the action of an attendant removing a barcoded product from
	 * the cart when there is only one of that item in the cart. If there
	 * are any of that item left in the cart afterwards, the test will fail.
	 * 
	 * @author Jacob Stanich
	 */
	@Test
	public final void attendantRemovesBarcodedProductWithOneInCart()
	{
		cMachine.cart.clear();
		cMachine.cart.put(bp1, 1.0);
		cMachine.attendantRemovesProduct(bp1);
		assertTrue("Product should not still be in the cart after being removed", !cMachine.cart.containsKey(bp1));
	}
	
	/**
	 * Tests the action of an attendant removing a barcoded product from the cart
	 * when there are two of that item currently in the cart. Afterwards, there
	 * should be exactly one of that item left in the cart.
	 * 
	 * @author Jacob Stanich
	 */
	@Test
	public final void attendantRemovesBarcodedProductWithTwoInCart()
	{
		cMachine.cart.clear();
		cMachine.cart.put(bp1, 2.0);
		cMachine.attendantRemovesProduct(bp1);
		assertTrue("There should be exactly one of the product left in the cart", cMachine.cart.get(bp1) == 1.0);
	}
	
	/**
	 * Tests the action of removing a null product from the cart. This test expects
	 * a SimulationException to be thrown.
	 * 
	 * @author Jacob Stanich
	 */
	@Test(expected=SimulationException.class)
	public final void attendantRemovesNullProduct()
	{
		cMachine.cart.clear();
		cMachine.cart.put(bp1, 1.0);
		cMachine.attendantRemovesProduct(null);
		fail("SimulationException should have been thrown");
	}
	
	/**
	 * Tests the action of an attendant removing a PLU coded product from
	 * the cart. If that item is still in the cart afterwards, the test will fail.
	 * 
	 * @author Jacob Stanich
	 */
	@Test
	public final void attendantRemovesPLUCodedProduct()
	{
		cMachine.cart.put(applePLUProduct, 5.0);
		cMachine.attendantRemovesProduct(applePLUProduct);
		assertTrue("Product should not still be in the cart after being removed", !cMachine.cart.containsKey(applePLUProduct));
	}
	
	/**
	 * Tests the action of an attendant looking up a barcoded product that exists in the product
	 * database. If the function does not return the correct information related to the product, the test will fail.
	 * 
	 * @author Jacob Stanich
	 */
	@Test
	public final void attendantLooksUpBarcodedProductInDatabase()
	{
		ArrayList<Object> array = cMachine.attendantLooksUpProduct(bC);
		assertTrue("Product lookup did not return the correct information",
				array.get(0) == bp1.getDescription() && array.get(1) == bp1.getPrice() && array.get(2) == ProductDatabases.INVENTORY.get(bp1));
	}
	
	/**
	 * Tests the action of an attendant looking up a barcoded product that does not exist in the product
	 * database. This test expects a SimulationException to be thrown.
	 * 
	 * @author Jacob Stanich
	 */
	@Test(expected=SimulationException.class)
	public final void attendantLooksUpBarcodedProductNotInDatabase()
	{
		Barcode bc2 = new Barcode("16");
		cMachine.attendantLooksUpProduct(bc2);
		fail("SimulationException should have been thrown");
	}
	
	/**
	 * Tests the action of an attendant looking up a PLU coded product that exists in the product
	 * database. If the function does not return the correct information related to the product, the test will fail.
	 * 
	 * @author Jacob Stanich
	 */
	@Test
	public final void attendantLooksUpPLUCodedProductInDatabase()
	{
		pluProductDb.clear();
		pluProductDb.put(applePLU, applePLUProduct);
		ArrayList<Object> array = cMachine.attendantLooksUpProduct(applePLU);
		assertTrue("test", array.get(0) == applePLUProduct.getDescription() && array.get(1) == applePLUProduct.getPrice() && array.get(2) == ProductDatabases.INVENTORY.get(applePLUProduct));
	}
	
	/**
	 * Tests the action of an attendant looking up a PLU coded product that does not exist in the product
	 * database. This test expects a SimulationException to be thrown.
	 * 
	 * @author Jacob Stanich
	 */
	@Test(expected=SimulationException.class)
	public final void attendantLooksUpPLUCodedProductNotInDatabase()
	{
		PriceLookupCode pluCode = new PriceLookupCode("4010");
		cMachine.attendantLooksUpProduct(pluCode);
		fail("test");
	}
	
	/**
	 * Tests the action of an attendant loading a valid coin into a coin dispenser.
	 * Since the coin is valid and there are not enough coins being loaded to 
	 * overload the machine, an exception should not be thrown.
	 * 
	 * @author Jacob Stanich
	 */
	@Test
	public final void attendantLoadsValidCoinTest() throws SimulationException, OverloadException
	{
		cMachine.checkoutStation.coinDispensers.get(new BigDecimal("1.00")).unload();
		Coin c = new Coin(new BigDecimal("1.00"), Currency.getInstance(Locale.CANADA));
		cMachine.attendantRefillCoinDispenser(cMachine.checkoutStation.coinDispensers.get(new BigDecimal("1.00")), c, 1);
	}
	
	/**
	 * Tests the action of an attendant loading enough coins into a coin dispenser to overload it.
	 * This test expects an OverloadException to be thrown.
	 * 
	 * @author Jacob Stanich
	 */
	@Test(expected=OverloadException.class)
	public final void attendantLoadsCoinsOverloadTest() throws SimulationException, OverloadException
	{
		Coin c = new Coin(new BigDecimal("1.00"), Currency.getInstance(Locale.CANADA));
		cMachine.attendantRefillCoinDispenser(cMachine.checkoutStation.coinDispensers.get(new BigDecimal("1.00")), c, 1200);
		fail("OverloadException should have been thrown");
	}
	
	/**
	 * Tests the action of an attendant loading a null coin into a coin dispenser.
	 * This test expects a SimulationException to be thrown.
	 * 
	 * @author Jacob Stanich
	 */
	@Test(expected=SimulationException.class)
	public final void attendantLoadsNullCoinTest() throws SimulationException, OverloadException
	{
		cMachine.checkoutStation.coinDispensers.get(new BigDecimal("1.00")).unload();
		cMachine.attendantRefillCoinDispenser(cMachine.checkoutStation.coinDispensers.get(new BigDecimal("1.00")), null, 1);
		fail("SimulationException should have been thrown");
	}
	
	/**
	 * Tests the action of an attendant loading a valid banknote into a banknote dispenser.
	 * Since the banknote is valid and there are not enough banknotes being loaded to 
	 * overload the machine, an exception should not be thrown.
	 * 
	 * @author Jacob Stanich
	 */
	@Test
	public final void attendantLoadsValidBanknoteTest() throws SimulationException, OverloadException
	{
		cMachine.checkoutStation.banknoteDispensers.get(5).unload();
		Banknote b = new Banknote(5, Currency.getInstance(Locale.CANADA));
		cMachine.attendantRefillBanknoteDispenser(cMachine.checkoutStation.banknoteDispensers.get(5), b, 1);
	}
	
	/**
	 * Tests the action of an attendant loading enough banknotes into a banknote dispenser to overload it.
	 * This test expects an OverloadException to be thrown.
	 * 
	 * @author Jacob Stanich
	 */
	@Test(expected=OverloadException.class)
	public final void attendantLoadsBanknotesOverloadTest() throws SimulationException, OverloadException
	{
		Banknote b = new Banknote(5, Currency.getInstance(Locale.CANADA));
		cMachine.attendantRefillBanknoteDispenser(cMachine.checkoutStation.banknoteDispensers.get(5), b, 1100);
		fail("OverloadException should have been thrown");
	}
	
	/**
	 * Tests the action of an attendant loading a null banknote into a banknote dispenser.
	 * This test expects a SimulationException to be thrown.
	 * 
	 * @author Jacob Stanich
	 */
	@Test(expected=SimulationException.class)
	public final void attendantLoadsNullBanknoteTest() throws SimulationException, OverloadException
	{
		cMachine.checkoutStation.banknoteDispensers.get(5).unload();
		cMachine.attendantRefillBanknoteDispenser(cMachine.checkoutStation.banknoteDispensers.get(5), null, 1);
		fail("SimulationException should have been thrown");
	}
	
	/**
	 * Tests the action of an attendant loading a banknote into a null dispenser.
	 * This test expects a SimulationException to be thrown.
	 * 
	 * @author Jacob Stanich
	 */
	@Test(expected=SimulationException.class)
	public final void attendantLoadsBanknoteIntoInvalidDispenserTest() throws SimulationException, OverloadException
	{
		cMachine.checkoutStation.banknoteDispensers.get(5).unload();
		Banknote b = new Banknote(5, Currency.getInstance(Locale.CANADA));
		cMachine.attendantRefillBanknoteDispenser(null, b, 1);
		fail("SimulationException should have been thrown");
	}
	
	
	
}
