///////////////////////////////////////////////////////////////////////////////
// For information as to what this class does, see the Javadoc, below.       //
// Copyright (C) 2005 by Peter Spirtes, Richard Scheines, Joseph Ramsey,     //
// and Clark Glymour.                                                        //
//                                                                           //
// This program is free software; you can redistribute it and/or modify      //
// it under the terms of the GNU General Public License as published by      //
// the Free Software Foundation; either version 2 of the License, or         //
// (at your option) any later version.                                       //
//                                                                           //
// This program is distributed in the hope that it will be useful,           //
// but WITHOUT ANY WARRANTY; without even the implied warranty of            //
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the             //
// GNU General Public License for more details.                              //
//                                                                           //
// You should have received a copy of the GNU General Public License         //
// along with this program; if not, write to the Free Software               //
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA //
///////////////////////////////////////////////////////////////////////////////

package edu.cmu.tetrad.util;

import java.io.IOException;
import java.io.ObjectInputStream;


/**
 * A normal distribution that allows its parameters to be set and allows
 * random sampling. The parameters are 0 = mean, 1 = standard deviation.
 *
 * @author Joseph Ramsey jdramsey@andrew.cmu.edu
 * @deprecated 7/19/08 Use edu.cmu.tetrad.util.dist.Normal instead.
 */
@SuppressWarnings({"UnusedDeclaration"})
public class NormalDistribution {
    static final long serialVersionUID = 23L;

    /**
     * The mean of the distribution.
     * @deprecated
     * @serial
     */
    @SuppressWarnings({"UnusedDeclaration"})
    private double mean;

    /**
     * The standard devision of the distribution.
     * @deprecated
     * @serial
     */
    @SuppressWarnings({"UnusedDeclaration"})
    private double sd;

    //=========================CONSTRUCTORS===========================//

    private NormalDistribution() {
    }

    /**
     * Generates a simple exemplar of this class to test serialization.
     *
     * @see edu.cmu.TestSerialization
     * @see edu.cmu.tetradapp.util.TetradSerializableUtils
     */
    @SuppressWarnings({"UnusedDeclaration"})
    public static Object serializableInstance() {
        return new Object();
    }

    //=========================PUBLIC METHODS=========================//

    public String getName() {
        throw new UnsupportedOperationException();
    }

    public void setParameter(int index, double value) {
        throw new UnsupportedOperationException();
    }

    public double getParameter(int index) {
        throw new UnsupportedOperationException();
    }

    public String getParameterName(int index) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the number of parameters = 2.
     */
    public int getNumParameters() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the next random sample from the distribution.
     */
    public double nextRandom() {
        throw new UnsupportedOperationException();
    }

    public String toString() {
        throw new UnsupportedOperationException();
    }

    //========================PRIVATE METHODS===========================//

    /**
     * Adds semantic checks to the default deserialization method. This method
     * must have the standard signature for a readObject method, and the body of
     * the method must begin with "s.defaultReadObject();". Other than that, any
     * semantic checks can be specified and do not need to stay the same from
     * version to version. A readObject method of this form may be added to any
     * class, even if Tetrad sessions were previously saved out using a version
     * of the class that didn't include it. (That's what the
     * "s.defaultReadObject();" is for. See J. Bloch, Effective Java, for help.
     *
     * @param s What it says.
     * @throws java.io.IOException    If the stream cannot be read.
     * @throws ClassNotFoundException if a the class of an object in the input cannot
     *                                be found.
     */
    private void readObject(ObjectInputStream s)
            throws IOException, ClassNotFoundException {
        s.defaultReadObject();
    }
}