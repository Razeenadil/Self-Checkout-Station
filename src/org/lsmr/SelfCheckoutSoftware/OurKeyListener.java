package org.lsmr.SelfCheckoutSoftware;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class OurKeyListener implements KeyListener {
	
	private OurTouchScreenManager manager;
	private String eventString;
	
	public OurKeyListener(OurTouchScreenManager manager, String event) {
		this.manager = manager;
		this.eventString = event;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getComponent().isEnabled()) {
			// Respond solely to the enter key.
			if (e.getKeyChar() == '\n') {
				manager.informEvent(eventString);
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
