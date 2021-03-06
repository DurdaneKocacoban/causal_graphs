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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests basic functionality of the tetrad.util.Point class.
 *
 * @author Joseph Ramsey
 */
public class TestPoint extends TestCase {
    public TestPoint(String name) {
        super(name);
    }

    public void testPoint() {
        PointXy p = new PointXy(25, 50);
        PointXy q = new PointXy(35, 55);
        PointXy r = new PointXy(25, 50);

        assertTrue(!p.equals(q));
        assertEquals(p, r);

        PointXy s = new PointXy(q);

        assertEquals(q, s);
    }

    public static Test suite() {
        return new TestSuite(TestPoint.class);
    }
}


