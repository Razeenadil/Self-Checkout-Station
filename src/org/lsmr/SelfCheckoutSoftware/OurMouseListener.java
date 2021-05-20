package org.lsmr.SelfCheckoutSoftware;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class OurMouseListener implements MouseListener {

	private OurTouchScreenManager manager;
	private String eventString;
	
	public OurMouseListener(OurTouchScreenManager manager, String event) {
		this.manager = manager;
		this.eventString = event;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getComponent().isEnabled()) {
			manager.informEvent(eventString);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
