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

package edu.cmu.tetradapp.workbench;

import edu.cmu.tetrad.graph.Edge;

import java.awt.*;

/**
 * Created by IntelliJ IDEA. User: jdramsey Date: Oct 15, 2004 Time: 1:20:54 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IDisplayEdge {
    boolean isSelected();

    void setSelected(boolean selected);

    void launchAssociatedEditor();

    void toggleSelected();

    void updateTrackPoint(Point p);

    DisplayNode getNode1();

    DisplayNode getNode2();

    PointPair getConnectedPoints();

    void setConnectedPoints(PointPair connectedPoints);

    Point getRelativeMouseTrackPoint();

    Edge getModelEdge();

    double getOffset();

    void setOffset(double offset);

    PointPair getPointPair();

    Point getTrackPoint();

    DisplayNode getComp1();

    DisplayNode getComp2();

    Color getLineColor();

    void setLineColor(Color lineColor);

    Color getSelectedColor();

    void setSelectedColor(Color selectedColor);

    float getStrokeWidth();

    void setStrokeWidth(float strokeWidth);
}


