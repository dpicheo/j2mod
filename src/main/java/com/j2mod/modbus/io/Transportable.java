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
package com.j2mod.modbus.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Interface defining a transportable class.
 *
 * @author Dieter Wimberger
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 */
public interface Transportable {

    /**
     * Returns the number of bytes that will
     * be written by {@link #writeTo(DataOutput)}.
     *
     * @return the number of bytes that will be written as <tt>int</tt>.
     */
    int getOutputLength();

    /**
     * Writes this <tt>Transportable</tt> to the
     * given <tt>DataOutput</tt>.
     *
     * @param dout the <tt>DataOutput</tt> to write to.
     *
     * @throws java.io.IOException if an I/O error occurs.
     */
    void writeTo(DataOutput dout) throws IOException;

    /**
     * Reads this <tt>Transportable</tt> from the given
     * <tt>DataInput</tt>.
     *
     * @param din the <tt>DataInput</tt> to read from.
     *
     * @throws java.io.IOException if an I/O error occurs or the data
     *                             is invalid.
     */
    void readFrom(DataInput din) throws IOException;

}