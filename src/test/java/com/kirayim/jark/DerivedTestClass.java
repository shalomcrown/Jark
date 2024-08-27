
package com.kirayim.jark;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

public class DerivedTestClass extends TestClass {

	int derivedItem = 45;

	int derivedItemPrimitiveArray[] = new int[] {5,6,7,8};

	Integer derivedImmutableItemArray[] = new Integer[] {3,4,5,6};

	Color rainbow[] = new Color[] {
			Color.RED,
			Color.ORANGE,
			Color.YELLOW,
			Color.GREEN,
			Color.CYAN,
			Color.MAGENTA,
			Color.BLUE};


	// ===========================================================================


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		DerivedTestClass that = (DerivedTestClass) o;
		return derivedItem == that.derivedItem && Objects.deepEquals(derivedItemPrimitiveArray, that.derivedItemPrimitiveArray) && Objects.deepEquals(derivedImmutableItemArray, that.derivedImmutableItemArray) && Objects.deepEquals(rainbow, that.rainbow);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), derivedItem, Arrays.hashCode(derivedItemPrimitiveArray), Arrays.hashCode(derivedImmutableItemArray), Arrays.hashCode(rainbow));
	}

	@Override
	public String toString() {
		return "DerivedTestClass{" +
				"derivedItem=" + derivedItem +
				", derivedItemPrimitiveArray=" + Arrays.toString(derivedItemPrimitiveArray) +
				", derivedImmutableItemArray=" + Arrays.toString(derivedImmutableItemArray) +
				", rainbow=" + Arrays.toString(rainbow) +
				"} " + super.toString();
	}

	// ===========================================================================

	/**
	 * @return the derivedItem
	 */
	public int getDerivedItem() {
		return derivedItem;
	}

	/**
	 * @param derivedItem the derivedItem to set
	 */
	public void setDerivedItem(int derivedItem) {
		this.derivedItem = derivedItem;
	}

	public int[] getDerivedItemPrimitiveArray() {
		return derivedItemPrimitiveArray;
	}

	public void setDerivedItemPrimitiveArray(int[] derivedItemPrimitiveArray) {
		this.derivedItemPrimitiveArray = derivedItemPrimitiveArray;
	}

	public Integer[] getDerivedImmutableItemArray() {
		return derivedImmutableItemArray;
	}

	public void setDerivedImmutableItemArray(Integer[] derivedImmutableItemArray) {
		this.derivedImmutableItemArray = derivedImmutableItemArray;
	}

	public Color[] getRainbow() {
		return rainbow;
	}

	public void setRainbow(Color[] rainbow) {
		this.rainbow = rainbow;
	}
}
