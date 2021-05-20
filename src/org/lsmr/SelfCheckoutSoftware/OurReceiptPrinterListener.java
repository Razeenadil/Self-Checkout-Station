package org.lsmr.SelfCheckoutSoftware;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.ReceiptPrinterListener;

/**
 * Used to alert attendant if recipt printer is out of paper or ink
 * @author Zach Brown 30070355
 *
 */
public class OurReceiptPrinterListener implements ReceiptPrinterListener {
	
	
	private boolean isOutOfPaper = true;
	private boolean isOutOfInk = true;

	
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
	 * Announces that the indicated printer is out of paper.
	 * 
	 * @param printer
	 *            The device from which the event emanated.
	 */
	@Override
	public void outOfPaper(ReceiptPrinter printer) {
		isOutOfPaper = true;
		
	}
	
	/**
	 * Announces that the indicated printer is out of ink.
	 * 
	 * @param printer
	 *            The device from which the event emanated.
	 */
	@Override
	public void outOfInk(ReceiptPrinter printer) {
		isOutOfInk = true;
		
	}

	/**
	 * Announces that paper has been added to the indicated printer.
	 * 
	 * @param printer
	 *            The device from which the event emanated.
	 */
	@Override
	public void paperAdded(ReceiptPrinter printer) {
		isOutOfPaper = false;
		
	}

	/**
	 * Announces that ink has been added to the indicated printer.
	 * 
	 * @param printer
	 *            The device from which the event emanated.
	 */
	@Override
	public void inkAdded(ReceiptPrinter printer) {
		isOutOfInk = false;
		
	}
	
	/**
	 * Getter method for paper in printer
	 * @return a boolean value to see if printer is out of paper
	 */
	public boolean getIsOutOfPaper() {
		return isOutOfPaper;
		
	}
	/**
	 * Getter method for ink in printer
	 * @return a boolean value to check if printer is out of ink
	 */
	public boolean getIsOutOfInk() {
		return isOutOfInk;
		
	}

}
