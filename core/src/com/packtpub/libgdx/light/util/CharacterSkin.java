/*
 * Author: Christian Crouthamel
 * Description: Enum class for character skins
 */

package com.packtpub.libgdx.light.util;

import com.badlogic.gdx.graphics.Color;

public enum CharacterSkin {

	//instance variablesS
	WHITE("White", 1.0f, 1.0f, 1.0f), GRAY("Gray", 0.7f, 0.7f, 0.7f), BROWN("Brown", 0.7f, 0.5f, 0.3f);
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