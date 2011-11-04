/** This file is part of kalypso/deegree.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * history:
 * 
 * Files in this package are originally taken from deegree and modified here
 * to fit in kalypso. As goals of kalypso differ from that one in deegree
 * interface-compatibility to deegree is wanted but not retained always. 
 * 
 * If you intend to use this software in other ways than in kalypso 
 * (e.g. OGC-web services), you should consider the latest version of deegree,
 * see http://www.deegree.org .
 *
 * all modifications are licensed as deegree, 
 * original copyright:
 *
 * Copyright (C) 2001 by:
 * EXSE, Department of Geography, University of Bonn
 * http://www.giub.uni-bonn.de/exse/
 * lat/lon GmbH
 * http://www.lat-lon.de
 */

package org.kalypsodeegree_impl.io.shpapi;

import org.kalypsodeegree.model.geometry.ByteUtils;
import org.kalypsodeegree.model.geometry.GM_Curve;
import org.kalypsodeegree.model.geometry.GM_Envelope;
import org.kalypsodeegree.model.geometry.GM_LineString;

/**
 * Class representig a two dimensional ESRI PolyLine <BR>
 * <B>Last changes <B>: <BR>
 * 12.01.2000 ap: constructor re-declared <BR>
 * 25.01.2000 ap: public variables numRings and numPoints declared <BR>
 * 21.03.2000 ap: parameter list of the second constructor modified <BR>
 * 14.08.2000 ap: constructor SHPPolyLine(GM_Point[][] gm_points) added <BR>
 * 14.08.2000 ap: method writeSHPPolyline(..) added <BR>
 * 14.08.2000 ap: method size() added <BR>
 * 16.08.2000 ap: constructor SHPPolyLine(GM_Point[][] gm_points) modified <BR>
 * <!---------------------------------------------------------------------------->
 * 
 * @version 16.08.2000
 * @author Andreas Poth
 */

public class SHPPolyLine implements ISHPParts
{
  private final int numParts;

  private final int numPoints;

  private final ISHPPoint[][] points;

  private final SHPEnvelope m_envelope;

  /**
   * constructor: gets a stream <BR>
   */
  public SHPPolyLine( final byte[] recBuf )
  {
    m_envelope = ShapeUtils.readBox( recBuf, 4 );

    numParts = ByteUtils.readLEInt( recBuf, 36 );
    numPoints = ByteUtils.readLEInt( recBuf, 40 );

    final int pointsStart = ShapeConst.PARTS_START + numParts * 4;

    points = new SHPPoint[numParts][];
    for( int j = 0; j < numParts; j++ )
    {
      // get number of first point of current part out of ESRI shape Record:
      final int firstPointNo = ByteUtils.readLEInt( recBuf, ShapeConst.PARTS_START + j * 4 );

      // calculate offset of part in bytes, count from the beginning of
      // recordbuffer
      final int offset = pointsStart + firstPointNo * 16;

      // get number of first point of next part ...
      int nextFirstPointNo = 0;
      if( j < numParts - 1 )
      {
        // ... usually out of ESRI shape Record
        nextFirstPointNo = ByteUtils.readLEInt( recBuf, ShapeConst.PARTS_START + (j + 1) * 4 );
      }
      // ... for the last part as total number of points
      else if( j == numParts - 1 )
      {
        nextFirstPointNo = numPoints;
      }

      // calculate number of points per part due to distance and
      // calculate some checksum for the total number of points to be worked
      final int lnumPoints = nextFirstPointNo - firstPointNo;

      // allocate memory for the j-th part
      points[j] = new SHPPoint[lnumPoints];

      // create the points of the j-th part from the buffer
      for( int i = 0; i < lnumPoints; i++ )
      {
        points[j][i] = new SHPPoint( recBuf, offset + i * 16 );
      }
    }
  }

  /**
   * constructor: recieves a matrix of GM_Points <BR>
   */
  public SHPPolyLine( final GM_Curve[] curves )
  {
    final GM_Envelope curve0env = curves[0].getEnvelope();
    double xmin = curve0env.getMin().getX();
    double xmax = curve0env.getMax().getX();
    double ymin = curve0env.getMin().getY();
    double ymax = curve0env.getMax().getY();

    numParts = curves.length;

    int numberPoints = 0;

    points = new SHPPoint[numParts][];

    try
    {
      // create SHPPoints from the GM_Points array
      for( int i = 0; i < numParts; i++ )
      {
        final GM_LineString ls = curves[i].getAsLineString();

        numberPoints += ls.getNumberOfPoints();

        points[i] = new SHPPoint[ls.getNumberOfPoints()];

        for( int j = 0; j < ls.getNumberOfPoints(); j++ )
        {
          points[i][j] = new SHPPoint( ls.getPositionAt( j ) );
          if( points[i][j].getX() > xmax )
          {
            xmax = points[i][j].getX();
          }
          else if( points[i][j].getX() < xmin )
          {
            xmin = points[i][j].getX();
          }
          if( points[i][j].getY() > ymax )
          {
            ymax = points[i][j].getY();
          }
          else if( points[i][j].getY() < ymin )
          {
            ymin = points[i][j].getY();
          }
        }

      }
    }
    catch( final Exception e )
    {
      System.out.println( "SHPPolyLine:: " + e );
    }

    numPoints = numberPoints;

    m_envelope = new SHPEnvelope( xmin, xmax, ymax, ymin );
  }

  /**
   * method: writeSHPPolyLine(byte[] bytearray, int start) <BR>
   */
  @Override
  public byte[] writeShape( )
  {
    int offset = ShapeConst.SHAPE_FILE_RECORD_HEADER_LENGTH;
    final byte[] bytearray = new byte[offset + size()];

    double xmin = points[0][0].getX();
    double xmax = points[0][0].getX();
    double ymin = points[0][0].getY();
    double ymax = points[0][0].getY();

    // write shape type identifier ( 3 = polyline )
    ByteUtils.writeLEInt( bytearray, offset, ShapeConst.SHAPE_TYPE_POLYLINE );

    offset += 4;
    // save offset of the bounding box
    final int tmp1 = offset;

    // increment offset with size of the bounding box
    offset += 4 * 8;

    // write numparts
    ByteUtils.writeLEInt( bytearray, offset, numParts );
    offset += 4;
    // write numpoints
    ByteUtils.writeLEInt( bytearray, offset, numPoints );
    offset += 4;

    // save offset of the list of offsets for each polyline
    int tmp2 = offset;

    // increment offset with numParts
    offset += 4 * numParts;

    int count = 0;
    for( final ISHPPoint[] point : points )
    {
      // stores the index of the i'th part
      ByteUtils.writeLEInt( bytearray, tmp2, count );
      tmp2 += 4;

      // write the points of the i'th part and calculate bounding box
      for( final ISHPPoint element : point )
      {
        count++;

        // calculate bounding box
        if( element.getX() > xmax )
        {
          xmax = element.getX();
        }
        else if( element.getX() < xmin )
        {
          xmin = element.getX();
        }

        if( element.getY() > ymax )
        {
          ymax = element.getY();
        }
        else if( element.getY() < ymin )
        {
          ymin = element.getY();
        }

        // write x-coordinate
        ByteUtils.writeLEDouble( bytearray, offset, element.getX() );
        offset += 8;

        // write y-coordinate
        ByteUtils.writeLEDouble( bytearray, offset, element.getY() );
        offset += 8;
      }
    }

    // jump back to the offset of the bounding box
    offset = tmp1;

    // write bounding box to the byte array
    ByteUtils.writeLEDouble( bytearray, offset, xmin );
    offset += 8;
    ByteUtils.writeLEDouble( bytearray, offset, ymin );
    offset += 8;
    ByteUtils.writeLEDouble( bytearray, offset, xmax );
    offset += 8;
    ByteUtils.writeLEDouble( bytearray, offset, ymax );

    return bytearray;
  }

  /**
   * returns the polyline shape size in bytes <BR>
   */
  @Override
  public int size( )
  {
    return 44 + numParts * 4 + numPoints * 16;
  }

  @Override
  public SHPEnvelope getEnvelope( )
  {
    return m_envelope;
  }

  @Override
  public int getNumParts( )
  {
    return numParts;
  }

  @Override
  public int getNumPoints( )
  {
    return numPoints;
  }

  @Override
  public ISHPPoint[][] getPoints( )
  {
    return points;
  }
} // end of class PolyLine
