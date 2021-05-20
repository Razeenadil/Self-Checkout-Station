package org.lsmr.SelfCheckoutSoftware;

import java.util.ArrayList;

import org.lsmr.selfcheckout.devices.*;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.ElectronicScaleListener;
import org.lsmr.selfcheckout.*;
import static java.lang.Math.abs;

public class OurScaleListener implements ElectronicScaleListener {

	
	//A list of the weight of the scanned items
	public ArrayList<Double> itemWeightList = new ArrayList<Double>();
	//A list of the weight of the scanned items placed inside the bag
	public ArrayList<Double> bagWeightList = new ArrayList<Double>();
	
	
	
	public boolean overloadFlag = false;
	public boolean enableFlag = true;
	public double lastWeight = 0;
	public boolean useBagFlag = false;
	public boolean nextItemisBag = false;
	public double bagWeight = 0;
	public double currentWeight;
	private SelfCheckoutMachine checkoutMachine;
	
	
	/**
	 * Constructor of OurScaleListener
	 * Initializes the global variables
	 * @param cMachine
	 * 		The selfcheckout machine
	 */
	public OurScaleListener(SelfCheckoutMachine cMachine) {
		checkoutMachine = cMachine;
		itemWeightList = new ArrayList<Double>();
		bagWeightList = new ArrayList<Double>();
		overloadFlag = false;
		enableFlag = true;
		lastWeight = 0;
		useBagFlag = false;
		nextItemisBag = false;
		bagWeight = 0;
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
		 * Enable usage of scale
		 */
		this.enableFlag = true;
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
		 * Disable usage of scale
		 */
		this.enableFlag = false;
	}

	
	/**
	 * Announces that the weight on the indicated scale has changed.
	 * 
	 * @param scale
	 *            The scale where the event occurred.
	 * @param weightInGrams
	 *            The new weight.
	 */
	@Override
	public void weightChanged(ElectronicScale scale, double weightInGrams) {
		/**
		 * When the weight of an Item is changed, add the new value of the weight to 
		 * itemWeightList or remove it from itemWeightList if the item has been removed
		 */
		
		//Checking if the customer is currently scanning- indicates that the customer is not done yet with the transaction
		if (checkoutMachine.currently_scanning) {
			//Comparing if the weight of the current item is greater than the weight of the last item
			if (weightInGrams > lastWeight) {
					//If the customer does not want to bag an item
					if (nextItemisBag == false) {
						//Get the difference between the weight of the current item and the weight of the last item
						//Then add that to the itemWeightList
					itemWeightList.add(weightInGrams-lastWeight);
				} else {
					//If the customer is using a plastic bag provided by the store
					//Get the difference between the weight of the current item and the weight of the last item
					bagWeight = weightInGrams-lastWeight;
					//Then add that to the BagWeightList
					bagWeightList.add(bagWeight);
					nextItemisBag = false;
				}
			}
			
			// If discrepancy is found between item weight and weight on scale, scanning is disabled
			if(Math.abs((weightInGrams - lastWeight) - checkoutMachine.expectedWeight) > scale.getSensitivity()) {
				checkoutMachine.waitingToPlaceItem = true;
				lastWeight = weightInGrams;
				//return;
			}
			
			// Remove an item from the list if weight is removed
			//Comparing if the weight of the last item is greater that the weight of the current item
			if (weightInGrams < lastWeight) {
				//If the customer does not want to bag the item
				if (nextItemisBag == false) {
					//Get the difference between the weight of the current item and the weight of the last item
					//Then add that to the itemWeightList
					itemWeightList.remove(lastWeight-weightInGrams);
				} else {
					//If the customer is using a plastic bag provided by the store
					//Get the difference between the weight of the current item and the weight of the last item
					remove(bagWeightList,lastWeight-weightInGrams);
					nextItemisBag = false;
				}
			}
			
			lastWeight = weightInGrams;
			checkoutMachine.waitingToPlaceItem = false;
		}
		
	}
	
	/**
	 * Announces that excessive weight has been placed on the indicated scale.
	 * 
	 * @param scale
	 *            The scale where the event occurred.
	 */
	@Override
	public void overload(ElectronicScale scale) {
		/**
		 * When the scale has too much weight on it, set overloadFlag to true
		 */
		

		overloadFlag = true;
			
	}
	
	/**
	 * Announces that the former excessive weight has been removed from the
	 * indicated scale, and it is again able to measure weight.
	 * 
	 * @param scale
	 *            The scale where the event occurred.
	 */
	@Override
	public void outOfOverload(ElectronicScale scale) {
		/**
		 * After the scale overloading has been fixed reset the overloadFlag to false
		 */
		
		overloadFlag = false;
		
	}
	
	/**
	 * This method sets the value of the variable nextItemBag to true - indicating that the next item will be placed in the bag
	 */
	public void nextItemIsBag() {
		this.nextItemisBag = true;
	}
	
	/**
	 * This method removes the given double value in the array list
	 * @param l
	 * 		Array list of double 
	 * @param d
	 * 		A double value
	 */
	private void remove(ArrayList<Double> l, double d) {
		for (int i = 0; i < l.size(); i++) {
			if (l.get(i).equals(d)) {
				l.remove(i);
			}
		}
	}
	
}

