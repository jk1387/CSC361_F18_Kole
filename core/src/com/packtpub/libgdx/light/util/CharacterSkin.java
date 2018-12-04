package com.packtpub.libgdx.light.util;

import com.badlogic.gdx.graphics.Color;

/**
 * Helps set the character skin for the orb.
 * @author Jacob Kole
 */
public enum CharacterSkin {

	//instance variablesS
	WHITE("White", 1.0f, 1.0f, 1.0f), GREEN("Green", 0f, 0.9f, 0.25f), GOLD("Gold", 1.0f, 0.8f, 0f);
	private String name;
	private Color color = new Color();

	/**
	 * Constructor for the character skin
	 * @param name name of the character skin
	 * @param r red
	 * @param g green
	 * @param b blue
	 */
	private CharacterSkin(String name, float r, float g, float b) {
		this.name = name;
		color.set(r, g, b, 1.0f);
	}

	/**
	 * Getter for the name 
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * Getter for color
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}
}