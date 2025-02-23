/*
 * Copyright 2002-2016 jamod & j2mod development teams
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ghgande.j2mod.modbus.util;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class that implements a collection for
 * bits, storing them packed into bytes.
 * Per default the access operations will index from
 * the LSB (rightmost) bit.
 *
 * @author Dieter Wimberger
 * @author Steve O'Hara (4NG)
 * @version 2.0 (March 2016)
 */
public class BitVector {

    private static final Logger logger = LogManager.getLogger(BitVector.class);
    private static final int[] ODD_OFFSETS = {-1, -3, -5, -7};
    private static final int[] STRAIGHT_OFFSETS = {7, 5, 3, 1};
    //instance attributes
    private int size;
    private final byte[] data;
    private boolean msbAccess = false;

    /**
     * Constructs a new <tt>BitVector</tt> instance
     * with a given size.
     * <p>
     *
     * @param size the number of bits the <tt>BitVector</tt>
     *             should be able to hold.
     */
    public BitVector(int size) {
        //store bits
        this.size = size;

        //calculate size in bytes
        if ((size % 8) > 0) {
            size = (size / 8) + 1;
        }
        else {
            size = (size / 8);
        }
        data = new byte[size];
    }

    /**
     * Factory method for creating a <tt>BitVector</tt> instance
     * wrapping the given byte data.
     *
     * @param data a byte[] containing packed bits.
     * @param size Size to set the bit vector to
     *
     * @return the newly created <tt>BitVector</tt> instance.
     */
    public static BitVector createBitVector(byte[] data, int size) {
        BitVector bv = new BitVector(data.length * 8);
        bv.setBytes(data);
        bv.size = size;
        return bv;
    }

    /**
     * Factory method for creating a <tt>BitVector</tt> instance
     * wrapping the given byte data.
     *
     * @param data a byte[] containing packed bits.
     *
     * @return the newly created <tt>BitVector</tt> instance.
     */
    public static BitVector createBitVector(byte[] data) {
        BitVector bv = new BitVector(data.length * 8);
        bv.setBytes(data);
        return bv;
    }

    /**
     * Toggles the flag deciding whether the LSB
     * or the MSB of the byte corresponds to the
     * first bit (index=0).
     *
     * @param b true if LSB=0 up to MSB=7, false otherwise.
     */
    public void toggleAccess(boolean b) {
        msbAccess = !msbAccess;
    }

    /**
     * Tests if this <tt>BitVector</tt> has
     * the LSB (rightmost) as the first bit
     * (i.e. at index 0).
     *
     * @return true if LSB=0 up to MSB=7, false otherwise.
     */
    public boolean isLSBAccess() {
        return !msbAccess;
    }

    /**
     * Tests if this <tt>BitVector</tt> has
     * the MSB (leftmost) as the first bit
     * (i.e. at index 0).
     *
     * @return true if LSB=0 up to MSB=7, false otherwise.
     */
    public boolean isMSBAccess() {
        return msbAccess;
    }

    /**
     * Returns the <tt>byte[]</tt> which is used to store
     * the bits of this <tt>BitVector</tt>.
     * <p>
     *
     * @return the <tt>byte[]</tt> used to store the bits.
     */
    public final synchronized byte[] getBytes() {
        byte[] dest = new byte[data.length];
        System.arraycopy(data, 0, dest, 0, dest.length);
        return dest;
    }

    /**
     * Sets the <tt>byte[]</tt> which stores
     * the bits of this <tt>BitVector</tt>.
     * <p>
     *
     * @param data a <tt>byte[]</tt>.
     */
    public synchronized void setBytes(byte[] data) {
        System.arraycopy(data, 0, this.data, 0, data.length);
    }

    /**
     * Sets the <tt>byte[]</tt> which stores
     * the bits of this <tt>BitVector</tt>.
     * <p>
     *
     * @param data a <tt>byte[]</tt>.
     * @param size Size to set the bit vector to
     */
    public void setBytes(byte[] data, int size) {
        System.arraycopy(data, 0, this.data, 0, data.length);
        this.size = size;
    }

    /**
     * Returns the state of the bit at the given index of this
     * <tt>BitVector</tt>.
     * <p>
     *
     * @param index the index of the bit to be returned.
     *
     * @return true if the bit at the specified index is set,
     * false otherwise.
     *
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    public boolean getBit(int index) throws IndexOutOfBoundsException {
        index = translateIndex(index);
        logger.debug("Get bit #{}", index);
        return ((data[byteIndex(index)]
                & (0x01 << bitIndex(index))) != 0
        );
    }

    /**
     * Sets the state of the bit at the given index of
     * this <tt>BitVector</tt>.
     * <p>
     *
     * @param index the index of the bit to be set.
     * @param b     true if the bit should be set, false if it should be reset.
     *
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    public void setBit(int index, boolean b) throws IndexOutOfBoundsException {
        index = translateIndex(index);
        logger.debug("Set bit #{}", index);
        int value = ((b) ? 1 : 0);
        int byteNum = byteIndex(index);
        int bitNum = bitIndex(index);
        data[byteNum] = (byte)((data[byteNum] & ~(0x01 << bitNum))
                | ((value & 0x01) << bitNum)
        );
    }

    /**
     * Returns the number of bits in this <tt>BitVector</tt>
     * as <tt>int</tt>.
     * <p>
     *
     * @return the number of bits in this <tt>BitVector</tt>.
     */
    public int size() {
        return size;
    }

    /**
     * Forces the number of bits in this <tt>BitVector</tt>.
     *
     * @param size Size to set the bit vector to
     *
     * @throws IllegalArgumentException if the size exceeds
     *                                  the byte[] store size multiplied by 8.
     */
    public void forceSize(int size) {
        if (size > data.length * 8) {
            throw new IllegalArgumentException("Size exceeds byte[] store");
        }
        else {
            this.size = size;
        }
    }

    /**
     * Returns the number of bytes used to store the
     * collection of bits as <tt>int</tt>.
     * <p>
     *
     * @return the number of bits in this <tt>BitVector</tt>.
     */
    public int byteSize() {
        return data.length;
    }

    /**
     * Returns a <tt>String</tt> representing the
     * contents of the bit collection in a way that
     * can be printed to a screen or log.
     * <p>
     * Note that this representation will <em>ALLWAYS</em>
     * show the MSB to the left and the LSB to the right
     * in each byte.
     *
     * @return a <tt>String</tt> representing this <tt>BitVector</tt>.
     */
    public String toString() {
        StringBuilder sbuf = new StringBuilder();
        for (int i = 0; i < data.length; i++) {

            int numberOfBitsToPrint = Byte.SIZE;
            int remainingBits = size - (i * Byte.SIZE);
            if (remainingBits < Byte.SIZE) {
                numberOfBitsToPrint = remainingBits;
            }

            sbuf.append(String.format("%" + numberOfBitsToPrint + "s", Integer.toBinaryString(data[i] & 0xFF)).replace(' ', '0'));
            sbuf.append(" ");
        }
        return sbuf.toString();
    }

    /**
     * Returns the index of the byte in the the byte array
     * that contains the given bit.
     * <p>
     *
     * @param index the index of the bit.
     *
     * @return the index of the byte where the given bit is stored.
     *
     * @throws IndexOutOfBoundsException if index is
     *                                   out of bounds.
     */
    private int byteIndex(int index) throws IndexOutOfBoundsException {

        if (index < 0 || index >= data.length * 8) {
            throw new IndexOutOfBoundsException();
        }
        else {
            return index / 8;
        }
    }

    /**
     * Returns the index of the given bit in the byte
     * where it it stored.
     * <p>
     *
     * @param index the index of the bit.
     *
     * @return the bit index relative to the position in the byte
     * that stores the specified bit.
     *
     * @throws IndexOutOfBoundsException if index is
     *                                   out of bounds.
     */
    private int bitIndex(int index) throws IndexOutOfBoundsException {

        if (index < 0 || index >= data.length * 8) {
            throw new IndexOutOfBoundsException();
        }
        else {
            return index % 8;
        }
    }

    private int translateIndex(int idx) {
        if (msbAccess) {
            int mod4 = idx % 4;
            int div4 = idx / 4;

            if ((div4 % 2) != 0) {
                //odd
                return (idx + ODD_OFFSETS[mod4]);
            }
            else {
                //straight
                return (idx + STRAIGHT_OFFSETS[mod4]);
            }
        }
        else {
            return idx;
        }
    }
}