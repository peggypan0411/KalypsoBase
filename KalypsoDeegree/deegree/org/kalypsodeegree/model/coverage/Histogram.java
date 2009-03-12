/*----------------    FILE HEADER  ------------------------------------------
 
This file is part of deegree (Java Framework for Geospatial Solutions).
Copyright (C) 2001 by:
EXSE, Department of Geography, University of Bonn
http://www.giub.uni-bonn.de/exse/
lat/lon Fitzke/Fretter/Poth GbR
http://www.lat-lon.de
 
This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.
 
This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.
 
You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 
Contact:
 
Andreas Poth
lat/lon Fitzke/Fretter/Poth GbR
Meckenheimer Allee 176
53115 Bonn
Germany
E-Mail: poth@lat-lon.de
 
Jens Fitzke
Department of Geography
University of Bonn
Meckenheimer Allee 166
53115 Bonn
Germany
E-Mail: jens.fitzke@uni-bonn.de
 
 
 ---------------------------------------------------------------------------*/
package org.deegree.model.coverage;


/**
 * Histogram records image statistics (mean and median value, etc.) and lists 
 * the counts and percentages of pixels in each of several brightness �bins.�
 * defines the access to the fields of a grids histogram
 *
 * <p>-----------------------------------------------------------------------</p>
 *
 * @author Andreas Poth
 * @version $Revision$ $Date$
 * <p>
 */
public interface Histogram {

    /**
     * returns the minimum value of grids values
     */
    double getMin();

    /**
     * returns the maximum value of grids values
     */
    double getMax();

    /**
     * returns the standard deviation of the grids values
     */
    double getStDev();

    /**
     * returns the mean value of grids values
     */
    double getMean();

    /**
     * returns the median value of grids values
     */
    double getMedian();

    /**
     * returns the number of grid values
     */
    int getTotalCount();

    /**
     * returns the counts for each histogram class
     */
    Count[] getCounts();
}

