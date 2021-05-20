package org.lsmr.SelfCheckoutSoftwareTest;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.SelfCheckoutSoftware.OurScaleListener;
import org.lsmr.SelfCheckoutSoftware.SelfCheckoutMachine;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.ElectronicScale;

public class OurScaleListenerTest {

	private ElectronicScale eScale;
	private OurScaleListener listener;
	public SelfCheckoutMachine cMachine;
	
	@Before
	public void setup() {
		cMachine = new SelfCheckoutMachine();
		eScale = new ElectronicScale(1000, 1);
		listener = new OurScaleListener(cMachine);
		eScale.register(listener);
	}
	
	@Test
	public void disableEnable() {
		try {
			eScale.disable();
		} catch (Exception e) {
			fail("Disabling threw an error");
		}
		assertFalse("Listener doesn't know that the scale is disabled",listener.enableFlag);
		try {
			eScale.enable();
		} catch (Exception e) {
			fail("Enabling threw an error");
		}
		assertTrue("Listener doesn't know that the scale is enabled",listener.enableFlag);
	}
	
	public boolean contains(ArrayList<Double> list, double val) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equals(val)) {
				return true;
			}
		}
		return false;
	}
	
	@Test
	public void addItem() {
		BarcodedItem bI = new BarcodedItem(new Barcode("1"), 50);
		eScale.add(bI);
		assertTrue("Item is not on list", contains(listener.itemWeightList, 50));
		eScale.remove(bI);
		assertTrue("Item remains on list", listener.itemWeightList.isEmpty());
	}
	
	@Test
	public void overloadAndBack() {
		BarcodedItem bI = new BarcodedItem(new Barcode("1"), 50);
		BarcodedItem bI2 = new BarcodedItem(new Barcode("1"), 1000);
		eScale.add(bI);
		assertFalse("Scale should not be overloaded!", listener.overloadFlag);
		eScale.add(bI2);
		assertTrue("Scale should be overloaded!", listener.overloadFlag);
		eScale.remove(bI);
		assertFalse("Scale should not be overloaded!", listener.overloadFlag);
		eScale.remove(bI2);
		assertFalse("Scale should not be overloaded!", listener.overloadFlag);
	}
	
	
	@Test
	public void addBag() {
		listener.nextItemIsBag();
		Bag b = new Bag(10);
		eScale.add(b);
		assertTrue("Bag treated as item", !contains(listener.itemWeightList,10));
		assertTrue("Bag not tracked.", contains(listener.bagWeightList,10));
		listener.nextItemIsBag();
		eScale.remove(b);
		assertTrue("Bag treated as item", !contains(listener.itemWeightList,10));
		assertTrue("Bag not tracked.", !contains(listener.bagWeightList,10));
	}
	
	@Test
	public void add2Bags() {
		Bag b = new Bag(10);
		Bag b2 = new Bag(20);
		listener.nextItemIsBag();
		eScale.add(b);
		assertTrue("Bag treated as item", !contains(listener.itemWeightList,10));
		assertTrue("Bag not tracked.", contains(listener.bagWeightList,10));
		assertTrue("Bag treated as item", !contains(listener.itemWeightList,20));
		assertTrue("Bag not tracked.", !contains(listener.bagWeightList,20));
		listener.nextItemIsBag();
		eScale.add(b2);
		assertTrue("Bag treated as item", !contains(listener.itemWeightList,10));
		assertTrue("Bag not tracked.", contains(listener.bagWeightList,10));
		assertTrue("Bag treated as item", !contains(listener.itemWeightList,20));
		assertTrue("Bag not tracked.", contains(listener.bagWeightList,20));
		listener.nextItemIsBag();
		eScale.remove(b2);
		assertTrue("Bag treated as item", !contains(listener.itemWeightList,10));
		assertTrue("Bag not tracked.", contains(listener.bagWeightList,10));
		assertTrue("Bag treated as item", !contains(listener.itemWeightList,20));
		assertTrue("Bag not tracked.", !contains(listener.bagWeightList,20));
		listener.nextItemIsBag();
		eScale.remove(b);
		assertTrue("Bag treated as item", !contains(listener.itemWeightList,10));
		assertTrue("Bag not tracked.", !contains(listener.bagWeightList,10));
		assertTrue("Bag treated as item", !contains(listener.itemWeightList,20));
		assertTrue("Bag not tracked.", !contains(listener.bagWeightList,20));
	}
	
	@Test
	public void notScanning() {
		// Used as regular item
		Bag i = new Bag(100);
		cMachine.currently_scanning = false;
		eScale.add(i);
		assertTrue("Should ignore!", !contains(listener.itemWeightList,100));
		eScale.remove(i);
		assertTrue("Should ignore!", !contains(listener.itemWeightList,100));
		cMachine.currently_scanning = true;
	}
	
	@Test
	public void weightDiscTest2() {
		
		BarcodedItem bI = new BarcodedItem(new Barcode("1"), 50);
		BarcodedItem bI2 = new BarcodedItem(new Barcode("1"), 20);
		cMachine.scanItem(bI);
		eScale.add(bI2);
		assertTrue("some sort of message",cMachine.waitingToPlaceItem);
	}
	
	

	@After
	public void cleanup() {
		eScale = null;
		listener = null;
	}
}
