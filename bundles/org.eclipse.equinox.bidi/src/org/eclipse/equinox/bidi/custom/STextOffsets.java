/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.equinox.bidi.custom;

public class STextOffsets {

	private static final byte L = Character.DIRECTIONALITY_LEFT_TO_RIGHT;
	private static final byte R = Character.DIRECTIONALITY_RIGHT_TO_LEFT;
	private static final byte AL = Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
	private static final byte AN = Character.DIRECTIONALITY_ARABIC_NUMBER;
	private static final byte EN = Character.DIRECTIONALITY_EUROPEAN_NUMBER;

	private static final byte[] STRONGS = {L, R};

	private static final int OFFSET_SIZE = 20;

	private int[] offsets = new int[OFFSET_SIZE];
	private int count; // number of used entries
	private int direction = -1; // STT direction
	private int prefixLength;

	/**
	 *  @return the stored prefix length
	 */
	public int getPrefixLength() {
		return prefixLength;
	}

	/**
	 * Store the prefix length
	 */
	public void setPrefixLength(int prefLen) {
		prefixLength = prefLen;
	}

	/**
	 * @return the number of used entries in the offsets array.
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Mark that all entries in the offsets array are unused.
	 */
	public void resetCount() {
		count = 0;
	}

	/**
	 * Get the value of a specified entry in the offsets array.
	 * @param  index is the index of the entry of interest.
	 * @return the value of the specified entry.
	 */
	public int getOffset(int index) {
		return offsets[index];
	}

	/**
	 * Insert an offset value in the offset array so that the array 
	 * stays in ascending order.
	 * @param  procData is a group of data accessible to processors.
	 * @param  offset is the value to insert.
	 */
	public void insertOffset(STextCharTypes charTypes, int offset) {
		int index = count - 1; // index of greatest member <= offset
		// look up after which member the new offset should be inserted
		while (index >= 0) {
			int wrkOffset = offsets[index];
			if (offset > wrkOffset)
				break;
			if (offset == wrkOffset)
				return; // avoid duplicates
			index--;
		}
		index++; // index now points at where to insert
		int length = count - index; // number of members to move up
		if (length > 0) // shift right all members greater than offset
			System.arraycopy(offsets, index, offsets, index + 1, length);
		offsets[index] = offset;
		count++; // number of used entries
		// if the offset is 0, adding a mark does not change anything
		if (offset < 1)
			return;
		if (charTypes == null)
			return;

		byte charType = charTypes.getBidiTypeAt(offset);
		// if the current char is a strong one or a digit, we change the
		//   charType of the previous char to account for the inserted mark.
		if (charType == L || charType == R || charType == AL || charType == EN || charType == AN)
			index = offset - 1;
		else
			// if the current char is a neutral, we change its own charType
			index = offset;

		if (direction < 0)
			direction = charTypes.getDirection();
		charTypes.setBidiTypeAt(index, STRONGS[direction]);
		return;
	}

	/**
	 * Make sure that there is at least 3 free entries in the offsets array.
	 */
	public void ensureRoom() {
		// make sure there are at least 3 empty slots in offsets
		if ((offsets.length - count) < 3) {
			int[] newOffsets = new int[offsets.length * 2];
			System.arraycopy(offsets, 0, newOffsets, 0, count);
			offsets = newOffsets;
		}
	}

	/**
	 * Get all and only the used offset entries.
	 * @return the current used entries of the offsets array.
	 */
	public int[] getArray() {
		if (count == offsets.length)
			return offsets;
		int[] array = new int[count];
		System.arraycopy(offsets, 0, array, 0, count);
		return array;
	}
}