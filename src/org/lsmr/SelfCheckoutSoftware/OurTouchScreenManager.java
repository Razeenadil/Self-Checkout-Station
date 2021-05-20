package org.lsmr.SelfCheckoutSoftware;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JRootPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.PLUCodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.external.CardIssuer;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.products.Product;


/**
 * Manages the GUI and touch screen display.
 * Assumes a screen size of 1280x720
 * @author Braden
 *
 */
public class OurTouchScreenManager {
	
	
	private SelfCheckoutMachine machine;
	private String mode = "";
	private JRootPane pane;
	private Graphics g;
	private boolean machineOn = true;
	
	// A bunch of variables used to track elements I might want to reference again
	private JPasswordField passwordField = null;
	private JButton button1 = null;
	private JButton button2 = null;
	private JButton button3 = null;
	private JButton button4 = null;
	private JButton button5 = null;
	private JButton button6 = null;
	private JButton button7 = null;
	private JTextArea textArea1 = null;
	private JTextField textInput1 = null;
	private JLabel label1 = null;
	private String displayText;
	private int comboMode = 1;
	private int cardMode = 1;
	private boolean waitingToPlaceItem = false;
	
	/** 
	 * Makes a manager. 
	 * @param machine  The machine whose touch screen is being managed.
	 */
	public OurTouchScreenManager(SelfCheckoutMachine machine) {
		this.machine = machine;
		machine.checkoutStation.screen.getFrame().setExtendedState(JFrame.NORMAL);
		machine.checkoutStation.screen.getFrame().setSize(1280,720);
		machine.checkoutStation.screen.setVisible(true);
		
		machine.checkoutStation.screen.getFrame().setVisible(true);
		pane = (JRootPane)machine.checkoutStation.screen.getFrame().getComponent(0);
		pane.setBackground(Color.BLACK);
		g = machine.checkoutStation.screen.getFrame().getGraphics();
		screen("Main");
	}
	
	
	public static void main(String[] args) {
		new SelfCheckoutMachine();
	}
	
	
	/**
	 * Empties the machine's printer. For testing purposes.
	 * @param machine
	 */
	@SuppressWarnings("unused")
	private static void emptyPrinter(SelfCheckoutMachine machine) {
		while (!machine.RPListener.getIsOutOfInk() && !machine.RPListener.getIsOutOfPaper()) {
			machine.checkoutStation.printer.print('\n');
		}
		System.out.println("Done");
		machine.checkoutStation.printer.cutPaper();
		machine.checkoutStation.printer.removeReceipt();
	}
	
	
	/**
	 * Show the appropriate screen and update the internal mode
	 * @param mode The mode to display
	 */
	public void screen(String mode) {
		this.mode = mode;
		clearScreen();
		if (mode.equals("Main")) {
			showMain();
		} else if (mode.equals("Attendant")) {
			showAttendant();
		} else if (mode.equals("MainSignIn")) {
			showSignIn(true);
		} else if (mode.equals("Lock")) {
			showSignIn(false);
		} else if (mode.equals("ClientItems")) {
			showItemMain();
		} else if (mode.equals("FindItemAdmin")) {
			showFindItemAdmin();
		} else if (mode.equals("ManualOverride")) {
			showSignIn(true);
		} else if (mode.equals("ManualOverridePop")) {
			showSignIn(true);
		} else if (mode.equals("LearnAboutItem")) {
			showFindItemAdmin();
		} else if (mode.equals("PaySelect")) {
			showPaySelect();
		} else if (mode.equals("Cash")) {
			showPayCash();
		} else if (mode.equals("Card")) {
			showPayCard();
		}
		pane.paintAll(g);
	}
	
	/**
	 * Clear the screen
	 */
	private void clearScreen() {
		pane.removeAll();
	}
	
	/**
	 * Creates a listener, instructing it to inform this of the given message on left-click
	 * @param c - The component to track
	 * @param text - The message to return via informEvent
	 */
	private void mouseL(Component c, String text) {
		c.addMouseListener(new OurMouseListener(this, text));
	}
	
	/**
	 * Creates a listener, instructing it to inform this of the given message on 'enter'
	 * @param c - The component to track
	 * @param text - The message to return via informEvent
	 */
	private void keyL(Component c, String text) {
		c.addKeyListener(new OurKeyListener(this, text));
	}
	
	/**
	 * Show the main screen
	 */
	private void showMain() {
		JButton user = new JButton("I am an end user");
		JButton admin = new JButton("I am an attendant");
		JButton off = new JButton("Turn off");
		JButton exit = new JButton("Exit simulation");
		
		
		user.setBounds(1280/2 - 100, 720/2 - 50 - 100, 100*2, 50*2);
		admin.setBounds(1280/2 - 100, 720/2 - 50 + 100, 100*2, 50*2);
		off.setBounds(20, 670, 150, 20);
		exit.setBounds(1280 - 170, 670, 150, 20);
		
		mouseL(user,"Client");
		mouseL(admin,"Attendant");
		mouseL(off,"Off");
		mouseL(exit,"Exit");
		
		// Show nothing if machine is off
		if (!machineOn) {
			user.setEnabled(false);
			admin.setEnabled(false);
			off.setText("Turn on");
		} else {
			// ink is empty
			if (machine.RPListener.getIsOutOfInk()) {
				JLabel text = new JLabel("Ink is empty!");
				text.setForeground(Color.RED);
				text.setBounds(1280/2 - 100, 720/2 - 50 + 200, 100*2, 20);
				user.setEnabled(false);
				pane.add(text);
			}
			// ink is empty
			if (machine.RPListener.getIsOutOfPaper()) {
				JLabel text2 = new JLabel("Paper is empty!");
				text2.setForeground(Color.RED);
				text2.setBounds(1280/2 - 100, 720/2 - 50 + 200 + 20, 100*2, 20);
				user.setEnabled(false);
				pane.add(text2);
			}
		}
		
		pane.add(user);
		pane.add(admin);
		pane.add(off);
		pane.add(exit);
		displayText = "List of Items:";	// Reset when main shown.
	}
	
	/**
	 * Show the sign-in screen
	 */
	private void showSignIn(boolean canGoBack) {
		JButton back = new JButton("Go back");
		JLabel req = new JLabel("Password:");
		JPasswordField pass = new JPasswordField();
		JButton cont = new JButton("Next page");
		JLabel hint = new JLabel("(Try '1234')");
		

		back.setBounds(20, 670, 100, 20);
		req.setBounds(1280/2 - 50, 720/2 - 45, 100, 20);
		req.setForeground(Color.WHITE);
		req.setFont(req.getFont().deriveFont((float) 20));
		if (!canGoBack) {
			req.setText("Blocked.");
			req.setForeground(Color.RED);
		}
		pass.setBounds(1280/2 - 50, 720/2 - 15, 100, 20);
		cont.setBounds(1280/2 - 50, 720/2 + 15, 100, 20);
		if (!canGoBack) {cont.setText("Unblock");}
		hint.setBounds(1280/2 - 50, 720/2 + 40, 100, 20);
		hint.setForeground(Color.BLUE);
		hint.setFont(hint.getFont().deriveFont((float) 16));
		
		
		mouseL(back, "Back");
		mouseL(cont, "Next");
		keyL(pass, "Next");
		
		
		pane.add(pass);
		if (canGoBack) {pane.add(back);}
		pane.add(cont);
		pane.add(hint);
		pane.add(req);
		
		
		// Saved for posterity's sake
		passwordField = pass;
	}
	
	/**
	 * Show the customer item-select screen
	 */
	private void showItemMain() {
		JButton back = new JButton("Go back");
		JButton type = new JButton("Filler text...");
		JButton place = new JButton("Place on scale");
		JButton override = new JButton("Override");
		JButton search = new JButton("Item Search");
		JButton popItem = new JButton("Remove item (Password required)");
		JButton toCheckout = new JButton("Proceed to payment");
		JTextField textEntry = new JTextField();
		JButton cont = new JButton("Add item");
		JTextArea hint = new JTextArea(displayText);
		JLabel req = new JLabel("Add items:");
		
		button1 = type;
		
		back.setBounds(20, 670, 100, 20);
		req.setBounds(200, 200 - 145, 160, 20);
		req.setForeground(Color.WHITE);
		req.setFont(req.getFont().deriveFont((float) 20));
		type.setBounds(200, 200 - 115, 400, 20);
		comboMode = -1;
		bCycle();
		search.setBounds(610, 200 - 115, 390, 20);
		popItem.setBounds(610, 200 + 15 - 100, 390, 20);
		toCheckout.setBounds(610, 200 + 45 - 100, 390, 20);
		textEntry.setBounds(200, 200 - 115 + 30, 400, 20);
		cont.setBounds(200, 200 + 15-100 + 30, 100, 20);
		place.setBounds(200 + 110, 200 + 15-100 + 30, 180, 20);
		override.setBounds(200 + 300, 200 + 15-100 + 30, 100, 20);
		hint.setBounds(200, 200 + 45-100 + 30, 800, 400);
		hint.setForeground(Color.BLACK);
		hint.setBackground(Color.LIGHT_GRAY);
		hint.setOpaque(true);
		hint.setFont(hint.getFont().deriveFont((float) 16));
		
		mouseL(back, "Back");
		mouseL(type, "Cycle");
		mouseL(place, "PlaceItem");
		mouseL(override, "Override");
		mouseL(search, "Search");
		mouseL(cont, "Add");
		mouseL(popItem, "Remove");
		mouseL(toCheckout, "Pay");
		keyL(textEntry,"Add");
		
		
		pane.add(back);
		pane.add(req);
		pane.add(textEntry);
		pane.add(cont);
		pane.add(hint);
		pane.add(type);
		pane.add(place);
		pane.add(override);
		pane.add(search);
		pane.add(popItem);
		pane.add(toCheckout);
		
		
		textArea1 = hint;
		// button1 = type;
		button2 = cont;
		button3 = place;
		button4 = override;
		button5 = search;
		button6 = popItem;
		button7 = toCheckout;
		textInput1 = textEntry;
		
		if (waitingToPlaceItem) {
			waitToPlaceItem();
		} else {
			doneWaitingToPlaceItem();
		}
	}
	
	/**
	 * Cycle through possible item selection options.
	 * Needs showItemMain to be active
	 */
	private void bCycle() {
		comboMode += 1;
		if (comboMode > 3) {
			comboMode = 0;
		}
		if (comboMode == 0) {
			button1.setText("Mode: Enter PLU");
		} else if (comboMode == 1) {
			button1.setText("Mode: Scan Barcode (emulates hardware behavior)");
		} else if (comboMode == 2) {
			button1.setText("Mode: Enter total number of plastic bags");
		} else if (comboMode == 3) {
			button1.setText("Mode: Place your own bag on the scale");
		}
		try {
			button1.paint(button1.getGraphics());
		} catch (Exception e) {
			// ignore
		}
	}
	
	/**
	 * Add an item to the text log.
	 * needs showItemMain to be active
	 * @param s - Text to add
	 */
	private void addItemToLog(String s) {
		displayText = String.join("\n", displayText, s);
		textArea1.setText(displayText);
		textArea1.paint(textArea1.getGraphics());
	}
	
	/**
	 * Adds an item to the cart.
	 */
	private void addItem() {
		String itemDesc = textInput1.getText();
		if (comboMode == 0) {
			try {
				
				PLUCodedItem item = new PLUCodedItem(new PriceLookupCode(itemDesc), 1.00);
				machine.checkoutStation.scale.add(item);
				
				machine.PLUEnter(new PriceLookupCode(itemDesc));
				
				machine.checkoutStation.scale.remove(item);
				
				addItemToLog(String.join("", "Added item with PLU code: ", itemDesc));
				waitToPlaceItem();
			} catch (Exception e) {
				addItemToLog(String.join("", "Failed to identify item with PLU code: ", itemDesc));
			}
		} else if (comboMode == 1) {
			try {
				machine.checkoutStation.mainScanner.scan(new BarcodedItem(new Barcode(itemDesc), 5));
				if(!machine.waitingToPlaceItem) {
					throw new Exception();
				}
				addItemToLog(String.join("", "Added item with Barcode: ", itemDesc));
				waitToPlaceItem();
			} catch (Exception e) {
				addItemToLog(String.join("", "Failed to add item with Barcode: ", itemDesc));
			}
		} else if (comboMode == 2) {
			try {
				machine.enterNumberOfPlasticBags(Integer.parseInt(itemDesc));
				addItemToLog(String.join("", "Set number of plastic bags to: ", String.valueOf(Integer.parseInt(itemDesc))));
			} catch (Exception e) {
				addItemToLog(String.join("", "Error: This is not a number: ", itemDesc));
			}
		} else if (comboMode == 3) {
			try {
				addItemToLog("Added a bag");
				waitToPlaceItem(true);
			} catch (Exception e) {
				addItemToLog(String.join("", "Error: This is not a number: ", itemDesc));
			}
		}
	}
	
	/**
	 * Place an item on the scale
	 */
	private void placeItem() {
		if (comboMode == 3) {
			try {
				machine.scaleListener.nextItemIsBag();
			} catch (Exception e) {
				
			}
		}
		machine.checkoutStation.baggingArea.add(new BarcodedItem(new Barcode("0000"), 5));
		doneWaitingToPlaceItem();
	}
	
	/**
	 * Shorthand for waitToPlaceItem(false)
	 */
	private void waitToPlaceItem() {
		waitToPlaceItem(false);
	}
	
	/**
	 * Disable buttons, until item is placed
	 * @param isABag - Informs scale that next item is a bag
	 */
	private void waitToPlaceItem(boolean isABag) {
		waitingToPlaceItem = true;
		setState(button1,false);
		setState(button2,false);
		setState(textInput1,false);
		setState(button3,true);
		setState(button4,true);
		setState(button5,false);
		setState(button6,false);
		setState(button7,false);
		if (isABag) {
			machine.scaleListener.nextItemIsBag();
		}
	}
	
	/**
	 * Re-enable buttons, waiting for further input.
	 */
	private void doneWaitingToPlaceItem() {
		waitingToPlaceItem = false;
		setState(button1,true);
		setState(button2,true);
		setState(textInput1,true);
		setState(button3,false);
		setState(button4,false);
		setState(button5,true);
		setState(button6,true);
		setState(button7,true);
		if (machine.scaleListener.nextItemisBag) {
			machine.scaleListener.nextItemIsBag();
		}
	}
	
	/**
	 * Set the state (enabled/disabled) of a component
	 * @param c - The component
	 * @param state - True=enabled
	 */
	private void setState(Component c, boolean state) {
		c.setEnabled(state);
		c.paint(c.getGraphics());
	}
	
	/**
	 * Show the attendant screen
	 */
	private void showAttendant() {
		JButton back = new JButton("Go back");
		JLabel title = new JLabel("Attendant mode");
		JButton ink = new JButton("Refill ink");
		JButton paper = new JButton("Refill paper");
		JButton byeCoins = new JButton("Empty coin storage");
		JButton byeBanknotes = new JButton("Empty banknote storage");
		JButton hiCoins = new JButton("Refill coin dispenser");
		JButton hiBanknotes = new JButton("Refill banknote dispenser");
		JButton search = new JButton("Search for product");
		JButton lock = new JButton("Block station");
		
		
		title.setBounds(1280/2 - 115, 50, 230, 40);
		title.setFont(title.getFont().deriveFont(30f));
		title.setForeground(Color.WHITE);
		back.setBounds(20, 670, 150, 20);
		ink.setBounds(1280/2 - 50, 100, 100, 20);
		paper.setBounds(1280/2 - 50, 140, 100, 20);
		byeCoins.setBounds(1280/2 - 100, 180, 200, 20);
		byeBanknotes.setBounds(1280/2 - 100, 220, 200, 20);
		hiCoins.setBounds(1280/2 - 100, 260, 200, 20);
		hiBanknotes.setBounds(1280/2 - 100, 300, 200, 20);
		search.setBounds(1280/2 - 100, 340, 200, 20);
		lock.setBounds(20, 640, 150, 20);
		
		
		mouseL(back, "Back");
		mouseL(ink, "RefillInk");
		mouseL(paper, "RefillPaper");
		mouseL(byeCoins, "EmptyCoins");
		mouseL(byeBanknotes, "EmptyBanknotes");
		mouseL(hiCoins, "RefillCoins");
		mouseL(hiBanknotes, "RefillBanknotes");
		mouseL(search, "FindItem");
		mouseL(lock,"Lock");
		
		if (!machine.RPListener.getIsOutOfInk()) {
			ink.setText("Refill ink (only possible when ink is empty)");
			ink.setEnabled(false);
			ink.setBounds(1280/2 - 160, 100, 320, 20);
		}
		
		if (!machine.RPListener.getIsOutOfPaper()) {
			paper.setText("Refill paper (only possible when paper is empty)");
			paper.setEnabled(false);
			paper.setBounds(1280/2 - 160, 140, 320, 20);
		}
		
		
		pane.add(title);
		pane.add(back);
		pane.add(ink);
		pane.add(paper);
		pane.add(byeCoins);
		pane.add(byeBanknotes);
		pane.add(hiCoins);
		pane.add(hiBanknotes);
		pane.add(search);
		pane.add(lock);

		// Save these buttons
		button1 = byeCoins;
		button2 = byeBanknotes;
		button3 = hiCoins;
		button4 = hiBanknotes;
	}
	
	/**
	 * Show the cash payment screen
	 * (Wouldn't exist in a real station. Here so that the GUI can do anything)
	 */
	private void showPayCash() {
		JButton back = new JButton("Go back");
		JLabel req = new JLabel("Enter cash");
		JTextField pass = new JTextField();
		JButton cont = new JButton("Deposit");
		JLabel hint = new JLabel("(Try '1234')");
		
		label1 = hint;

		back.setBounds(20, 670, 100, 20);
		req.setBounds(1280/2 - 50, 720/2 - 45, 200, 20);
		req.setForeground(Color.WHITE);
		req.setFont(req.getFont().deriveFont((float) 20));
		pass.setBounds(1280/2 - 50, 720/2 - 15, 100, 20);
		cont.setBounds(1280/2 - 50, 720/2 + 15, 100, 20);
		hint.setBounds(1280/2 - 50, 720/2 + 40, 600, 20);
		hint.setFont(hint.getFont().deriveFont((float) 16));
		readBalance();
		
		mouseL(back, "Back");
		mouseL(cont, "Deposit");
		keyL(pass, "Deposit");
		
		
		pane.add(pass);
		pane.add(back);
		pane.add(cont);
		pane.add(hint);
		pane.add(req);
		
		// Saved for posterity's sake
		textInput1 = pass;
		button1 = back;
		button2 = cont;
	}
	
	/**
	 * Deposits cash, reading the value from the GUI.
	 * Needs 'showPayCash' to be active
	 */
	private void depositCash() {
		setState(button1, false);
		setState(button2, false);
		setState(textInput1, false);
		try {
			float amount = Float.parseFloat(textInput1.getText());
			machine.setBalance(machine.getBalance() - amount);
			readBalance();
			textInput1.setText("");
		} catch (Exception e) {
		}
		setState(button1, true);
		if (machine.balance >= 0.01) {
			setState(button2, true);
			setState(textInput1, true);
		}
	}
	
	/**
	 * Reads the balance, and updates it visually
	 */
	private void readBalance() {
		if (machine.balance > 0.01) {
			label1.setForeground(Color.RED);
		} else {
			label1.setForeground(Color.GREEN);
			button1.setForeground(Color.GREEN);
		}
		// The following line is a hacky way of rounding to the cent.
		machine.balance = (float)(int)(machine.balance * 100) / 100;
		String rep = String.valueOf(machine.balance);
		if (rep.lastIndexOf(".") + 2 < rep.length()) {
			rep = rep.substring(0, rep.lastIndexOf(".") + 3);
		}
		label1.setText(String.join("", "Balance: ", rep));
	}
	
	/**
	 * Shows the card payment screen.
	 */
	private void showPayCard() {
		JButton back = new JButton("Go back");
		JLabel req = new JLabel("Enter card");
		JButton cycle = new JButton("Filler");
		JTextField pass = new JTextField();
		JButton cont = new JButton("Deposit");
		JLabel hint = new JLabel("(Try '1234')");
		
		
		label1 = hint;
		button3 = cycle;

		back.setBounds(20, 670, 100, 20);
		req.setBounds(1280/2 - 50, 720/2 - 45, 200, 20);
		req.setForeground(Color.WHITE);
		req.setFont(req.getFont().deriveFont((float) 20));
		cycle.setBounds(1280/2 - 100, 720/2 - 15, 200, 20);
		pass.setBounds(1280/2 - 100, 720/2 + 15, 200, 20);
		cont.setBounds(1280/2 - 100, 720/2 + 45, 200, 20);
		hint.setBounds(1280/2 - 100, 720/2 + 65, 600, 20);
		hint.setFont(hint.getFont().deriveFont((float) 16));
		readBalance();
		
		
		mouseL(back, "Back");
		mouseL(cont, "Deposit");
		mouseL(cycle, "Cycle");
		keyL(pass, "Deposit");

		cardMode = -1;
		cycleCard();
		
		pane.add(pass);
		pane.add(back);
		pane.add(cont);
		pane.add(hint);
		pane.add(req);
		pane.add(cycle);
		
		// Saved for posterity's sake
		textInput1 = pass;
		button1 = back;
		button2 = cont;
	}
	
	/**
	 * Cycle through card payment options
	 */
	private void cycleCard() {
		cardMode += 1;
		if (cardMode > 3) {
			cardMode = 0;
		}
		if (cardMode == 0) {
			setCard("Membership");
		} else if (cardMode == 1) {
			setCard("Credit");
		} else if (cardMode == 2) {
			setCard("Debit");
		} else if (cardMode == 3) {
			setCard("Gift");
		}
	}
	
	/**
	 * Set which card name is active for the button
	 * @param s - The button contents
	 */
	private void setCard(String s) {
		try {
			button3.setText(s);
			button3.paint(button3.getGraphics());
		} catch (Exception e) {}
	}
	
	/**
	 * Show the item find interface.
	 */
	private void showFindItemAdmin() {
		JButton back = new JButton("Go back");
		JTextField textEntry = new JTextField();
		JButton cont = new JButton("Search");
		JTextArea hint = new JTextArea("(Results appear here)");
		JLabel req = new JLabel("Search:");
		
		back.setBounds(20, 670, 100, 20);
		
		req.setBounds(1280/2 - 40, 200 - 45, 80, 20);
		req.setForeground(Color.WHITE);
		req.setFont(req.getFont().deriveFont((float) 20));
		textEntry.setBounds(1280/2 - 200, 200 - 15, 400, 20);
		cont.setBounds(1280/2 - 50, 200 + 15, 100, 20);
		hint.setBounds(1280/2 - 400, 200 + 40, 800, 400);
		hint.setForeground(Color.BLACK);
		hint.setBackground(Color.LIGHT_GRAY);
		hint.setOpaque(true);
		hint.setFont(hint.getFont().deriveFont((float) 16));
		
		mouseL(back, "Back");
		mouseL(cont, "Search");
		keyL(textEntry,"Search");
		
		
		pane.add(back);
		pane.add(req);
		pane.add(textEntry);
		pane.add(cont);
		pane.add(hint);
		
		textArea1 = hint;
		button1 = cont;
		textInput1 = textEntry;
	}
	
	/**
	 * Show the screen for selecting payment method.
	 * If there is no balance, then show a 'proceed' button
	 */
	private void showPaySelect() {
		JLabel title = new JLabel("Select payment method");
		JButton cash = new JButton("Pay with Cash");
		JButton card = new JButton("Use card (or enter membership card info)");
		JButton hereAtTheEndOfAllThings = new JButton("Get change and and take items");
		JButton back = new JButton("Go back");
		
		cash.setBounds(1280/2 - 150 - 200, 720/2 - 50, 300, 50*2);
		card.setBounds(1280/2 - 150 + 200, 720/2 - 50, 300, 50*2);
		back.setBounds(20, 670, 100, 20);
		title.setBounds(1280/2 - 180, 720/2 - 170, 360, 40);
		title.setFont(title.getFont().deriveFont(30f));
		title.setForeground(Color.WHITE);
		if (machine.balance <= 0.01) {
			cash.setEnabled(false);
			card.setEnabled(false);
			hereAtTheEndOfAllThings.setBounds(1280/2 - 150, 720/2 - 50 + 150, 300, 50*2);
			mouseL(hereAtTheEndOfAllThings, "Finish");
			pane.add(hereAtTheEndOfAllThings);
		}
		
		
		mouseL(cash, "Cash");
		mouseL(card, "Card");
		mouseL(back, "Back");
		
		
		pane.add(cash);
		pane.add(card);
		pane.add(back);
		pane.add(title);
	}
	
	/**
	 * Simulates tapping a card.
	 */
	private void depositCard() {
		try {
			if (cardMode == 0) {
				machine.enterMembershipNumber(textInput1.getText());
			} else if (cardMode == 1) {
				Card c = new Card("Credit", textInput1.getText(), "GenericUser", "123", "1234", true, true);
				CardIssuer cIssuer = new CardIssuer("Credit");
				Calendar c1 = Calendar.getInstance();
				c1.add(Calendar.YEAR, 5);
				cIssuer.addCardData("123", "GenericUser", c1, "123", new BigDecimal(10000000));
				machine.payWithCredit(c, cIssuer, "tap", "1234", null);
				machine.removeCard();
				machine.balance = 0;
				
			} else if (cardMode == 2) {
				Card c = new Card("Debit", textInput1.getText(), "GenericUser", "123", "1234", true, true);
				machine.payWithDebit(c, "tap", "1234", null);
				machine.removeCard();
				machine.balance = 0;
				
			} else if (cardMode == 3) {
				if (acceptGiftCard(textInput1.getText())) {
					machine.setBalance(0);
				}
			}
		} catch (Exception e) {
			return;
		}
		screen("PaySelect");
	}
	
	/**
	 * Returns if to accept a given gift card
	 * @param s - the card number
	 * @return - Always returns true, because there isn't a database
	 */
	private boolean acceptGiftCard(String s) {
		return true;
	}
	
	/**
	 * Receives a string, saying that a button was pressed.
	 * Uses mouse and keyboard listeners to notify it.
	 * @param eventString - The string describing the event
	 */
	public void informEvent(String eventString) {
		if (mode.equals("Main")) {
			if (eventString.equals("Client")) {
				screen("ClientItems");
			} else if (eventString.equals("Attendant")) {
				screen("MainSignIn");
			} else if (eventString.equals("Off")) {
				machineToggle();
				screen("Main");
			} else if (eventString.equals("Exit")) {
				System.exit(0);
			}
		} else if (mode.equals("MainSignIn")) {
			if (eventString.equals("Back")) {
				screen("Main");
			} else if (eventString.equals("Next")) {
				if (checkSecure()) {
					screen("Attendant");
				}
			}
		} else if (mode.equals("Attendant")) {
			if (eventString.equals("Back")) {
				screen("Main");
			} else if (eventString.equals("RefillInk")) {
				machine.refillInk();
				screen("Attendant");
			} else if (eventString.equals("RefillPaper")) {
				machine.refillPaper();
				screen("Attendant");
			} else if (eventString.equals("EmptyCoins")) {
				emptyCoins();
			} else if (eventString.equals("EmptyBanknotes")) {
				emptyBanknotes();
			} else if (eventString.equals("RefillCoins")) {
				refillCoins();
			} else if (eventString.equals("RefillBanknotes")) {
				refillBanknotes();
			} else if (eventString.equals("FindItem")) {
				screen("FindItemAdmin");
			} else if (eventString.equals("Lock")) {
				blockStation();
				screen("Lock");
			}
		} else if (mode.equals("Lock")) {
			if (checkSecure()) {
				unblockStation();
				screen("Main");
			}
		} else if (mode.equals("FindItemAdmin") || mode.equals("LearnAboutItem")) {
			if (eventString.equals("Back")) {
				if (mode.equals("LearnAboutItem")) {
					screen("ClientItems");
				} else {
					screen("Attendant");
				}
			} else if (eventString.equals("Search")) {
				performSearch();
			}
		} else if (mode.equals("ClientItems")) {
			if (eventString.equals("Back")) {
				screen("Main");
			} else if (eventString.equals("Cycle")) {
				bCycle();
			} else if (eventString.equals("Add")) {
				addItem();
			} else if (eventString.equals("PlaceItem")) {
				placeItem();
			} else if (eventString.equals("Override")) {
				screen("ManualOverride");
			} else if (eventString.equals("Remove")) {
				screen("ManualOverridePop");
			} else if (eventString.equals("Search")) {
				screen("LearnAboutItem");
			} else if (eventString.equals("Pay")) {
				try {machine.finalizeItems();	} catch (Exception e) {}
				screen("PaySelect");
			}
		} else if (mode.equals("ManualOverride")) {
			if (eventString.equals("Back")) {
				screen("ClientItems");
			} else if (eventString.equals("Next")) {
				if (checkSecure()) {
					waitingToPlaceItem = false;
					screen("ClientItems");
				}
			}
		} else if (mode.equals("ManualOverridePop")) {
			if (eventString.equals("Back")) {
				screen("ClientItems");
			} else if (eventString.equals("Next")) {
				if (checkSecure()) {
					removeItemFromCart();
					screen("ClientItems");
				}
			}
		} else if (mode.equals("PaySelect")) {
			if (eventString.equals("Cash")) {
				screen("Cash");
			} else if (eventString.equals("Card")) {
				screen("Card");
			} else if (eventString.equals("Back")) {
				machine.currently_scanning = true;
				screen("ClientItems");
			} else if (eventString.equals("Finish")) {
				// Dispense change and such.
				try {
					BanknoteSlotGrabber bsg = new BanknoteSlotGrabber();
					machine.checkoutStation.banknoteOutput.register(bsg); //emulates user grabbing dangling banknote
					machine.dispenseChange(-machine.balance, machine.checkoutStation, SelfCheckoutMachine.ACCEPTEDCURRENCY, machine.bSlotListener);
					machine.checkoutStation.banknoteOutput.deregister(bsg);
				} catch (Exception e) {}
				machine.numberOfPlasticBags = 0;
				machine.resetMachine();
				machine.totalPrice = 0;
				
				screen("Main");
			}
		} else if (mode.equals("Cash")) {
			if (eventString.equals("Back")) {
				screen("PaySelect");
			} else if (eventString.equals("Deposit")) {
				depositCash();
			}
		} else if (mode.equals("Card")) {
			if (eventString.equals("Back")) {
				screen("PaySelect");
			} else if (eventString.equals("Deposit")) {
				depositCard();
				screen("PaySelect");
		} else if (eventString.equals("Cycle")) {
				cycleCard();
			}
		}
	}
	
	/**
	 * Pop an item from the stack/cart
	 */
	private void removeItemFromCart() {
		Set<Entry<Product, Double>> set = machine.cart.entrySet();
		Iterator<Entry<Product, Double>> iter = set.iterator();
		Entry<Product, Double> entry = null;
		while (iter.hasNext()) {
			entry = iter.next();
		}
		if (entry != null) {
			try {
				machine.attendantRemovesProduct(entry.getKey());
			} catch (Exception e) {
				System.out.println("Failed to remove item");
			}
		}
		try {
			displayText = displayText.substring(0, displayText.lastIndexOf("\n"));
		} catch (Exception e) {
			
		}
	}
	
	/**
	 * Do a search, using the keyword given in a text input field
	 */
	private void performSearch() {
		button1.setEnabled(false);
		button1.paint(button1.getGraphics());
		
		String sText = textInput1.getText();
		String output = "";
		String toAppend;
		ArrayList<Object> result;
		try {
			
			result = machine.attendantLooksUpProduct(new Barcode(sText));
			
			toAppend = (String)result.get(0);
			toAppend = String.join("",toAppend, "\t");
			toAppend = String.join("",toAppend, ((BigDecimal)result.get(1)).toString());
			toAppend = String.join("",toAppend, "\t");
			toAppend = String.join("",toAppend, ((Integer)result.get(2)).toString());
			
			output = String.join("",output, toAppend);
		} catch (Exception e) {
			output = String.join("",output, "No barcodes found!");
		}
		try {
			result = machine.attendantLooksUpProduct(new PriceLookupCode(sText));
			
			toAppend = String.join("","\n", (String)result.get(0));
			toAppend = String.join("",toAppend, "\t");
			toAppend = String.join("",toAppend, ((BigDecimal)result.get(1)).toString());
			toAppend = String.join("",toAppend, "\t");
			toAppend = String.join("",toAppend, ((Integer)result.get(2)).toString());
			
			output = String.join("",output, toAppend);
		} catch (Exception e) {
			output = String.join("",output, "\nNo PLU codes found!");
		}
		
		System.out.print(output);
		
		textArea1.setText(output);
		button1.setEnabled(true);
		button1.paint(button1.getGraphics());
	}
	
	/**
	 * Emulate the refilling of coins.
	 */
	private void refillCoins() {
		JButton button = button3;
		button.setText("Loading...");
		button.setEnabled(false);
		button.paint(button.getGraphics());
		refillCoinsSample();
		waitTime(5);
		button.setText("Done!");
		button.paint(button.getGraphics());
		waitTime(5);
		button.setText("Refill coin dispenser");
		button.setEnabled(true);
		button.paint(button.getGraphics());
	}
	
	/**
	 * Emulate the refilling of banknotes.
	 */
	private void refillBanknotes() {
		JButton button = button4;
		button.setText("Loading...");
		button.setEnabled(false);
		button.paint(button.getGraphics());
		refillBanknotesSample();
		waitTime(5);
		button.setText("Done!");
		button.paint(button.getGraphics());
		waitTime(5);
		button.setText("Refill banknote dispenser");
		button.setEnabled(true);
		button.paint(button.getGraphics());
	}
	
	/**
	 * Emulate the refilling of coins.
	 */
	private void refillCoinsSample() {
		//Loads hardware dispensers
		try {
			final Currency CAD = Currency.getInstance("CAD");
			final Coin NICKEL = new Coin(new BigDecimal(0.05),CAD);
			final Coin DIME = new Coin(new BigDecimal(0.10),CAD);
			final Coin QUARTER = new Coin(new BigDecimal(0.25),CAD);
			final Coin DOLLAR = new Coin(new BigDecimal(1.00),CAD);
			final Coin TWONIE = new Coin(new BigDecimal(2.00),CAD);
			
			SelfCheckoutStation hardware = machine.checkoutStation;
			
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
			System.out.println("Failed to load dispensers");
		}
		
	}
	
	/**
	 * Emulate the refilling of banknotes.
	 */
	private void refillBanknotesSample() {
		// Loads hardware dispensers
		try {
			final Currency CAD = Currency.getInstance("CAD");
			final Banknote HUNDO = new Banknote(100,CAD);
			final Banknote FIFTY = new Banknote(50,CAD);
			final Banknote TWENTY = new Banknote(20,CAD);
			final Banknote TEN = new Banknote(10,CAD);
			final Banknote FIVE = new Banknote(5,CAD);
			
			SelfCheckoutStation hardware = machine.checkoutStation;
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
		} catch(Exception e) {
		}
		
	}

	/**
	 * Emulate removing coins
	 */
	private void emptyCoins() {
		JButton button = button1;
		button.setText("Loading...");
		button.setEnabled(false);
		button.paint(button.getGraphics());
		machine.attendantEmptyCoinStorage();
		waitTime(5);
		button.setText("Done!");
		button.paint(button.getGraphics());
		waitTime(5);
		button.setText("Empty coin storage");
		button.setEnabled(true);
		button.paint(button.getGraphics());
	}
	
	/**
	 * Emulate removing banknotes
	 */
	private void emptyBanknotes() {
		JButton button = button2;
		button.setText("Loading...");
		button.setEnabled(false);
		button.paint(button.getGraphics());
		machine.attendantEmptyCoinStorage();
		waitTime(5);
		button.setText("Done!");
		button.paint(button.getGraphics());
		waitTime(5);
		button.setText("Empty banknote storage");
		button.setEnabled(true);
		button.paint(button.getGraphics());
	}
	
	/**
	 * Wait time in tenths of a second
	 * @param d Tenths of a second to wait.
	 */
	private void waitTime(int d) {
		try {
			Thread.sleep(d * 100);
		} catch (Exception e) {
		}
	}
	
	/**
	 * Toggle if the machine is enabled
	 */
	private void machineToggle() {
		if (machineOn) {
			machine.attendantShutsDownStation();
		} else {
			machine.attendantStartsStation();
		}
		machineOn = !machineOn;
	}

	/**
	 * Do the things needed to block the station
	 */
	private void blockStation() {
		try {
			machine.attendantBlocksStation();
		} catch (Exception e) {
		}
	}
	
	/**
	 * Do the things needed to unblock the station
	 */
	private void unblockStation() {
		try {
			machine.attendantUnblocksStation();
		} catch (Exception e) {
		}
	}
	
	/**
	 * Check if the admin login password is correct
	 * @return a boolean - true if correct. Try 1234
	 */
	private boolean checkSecure() {
		char[] password = passwordField.getPassword();
		// Check that the password is valid
		if (password.length == 4 && password[0] == '1' && password[1] == '2' && password[2] == '3' && password[3] == '4') {
			return true;
		} else {
			// Clear the password field
			passwordField.setText("");
			return false;
		}
	}
	
	
	
}
