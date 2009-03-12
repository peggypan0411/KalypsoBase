/*--------------- Kalypso-Header --------------------------------------------------------------------

 This file is part of kalypso.
 Copyright (C) 2004, 2005 by:

 Technical University Hamburg-Harburg (TUHH)
 Institute of River and coastal engineering
 Denickestr. 22
 21073 Hamburg, Germany
 http://www.tuhh.de/wb

 and
 
 Bjoernsen Consulting Engineers (BCE)
 Maria Trost 3
 56070 Koblenz, Germany
 http://www.bjoernsen.de

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

 E-Mail:
 belger@bjoernsen.de
 schlienger@bjoernsen.de
 v.doemming@tuhh.de
  
---------------------------------------------------------------------------------------------------*/
package org.kalypso.ogc.gml.widgets;

import java.awt.Graphics;
import java.awt.Point;

import org.deegree.model.geometry.GM_Envelope;
import org.kalypso.ogc.gml.command.ChangeExtentCommand;
import org.kalypso.util.command.ICommand;

/**
 * This class performs a zoomin event. It will be performed by setting the map
 * boundaries to the rectangle selected by the client or centering the map onto
 * the point the user had mouse-clicked to.
 * 
 * @author <a href="mailto:k.lupp@web.de">Katharina Lupp </a>
 */
public class ZoomInByRectWidget extends AbstractWidget {

    private Point endPoint = null;

    private Point startPoint = null;

    public void dragged(Point p) {
        //System.out.println("dragged");
        if (startPoint == null)
            startPoint = p;
        {
            endPoint = p;
        }
        //System.out.println("draggedPoint: " + startPoint + " " + endPoint);
    }

    public void leftPressed(Point p) {
        this.startPoint = p;
        this.endPoint = null;
        //System.out.println("leftPressed " + this.startPoint + " " + this.endPoint);
    }

    public void leftReleased(Point p) {
        this.endPoint = p;
        //System.out.println("leftReleased " + this.startPoint + " " + this.endPoint);
        
        perform();
        
    }

    /*
     * paints the dragged rectangle defined by the start and end point of the
     * drag box
     */
    public void paint(Graphics g) {
       if (startPoint != null && endPoint != null) {        
            final int x  = (int)Math.round(startPoint.getX());
            final int y  = (int)Math.round(startPoint.getY());
			
			double dx = Math.abs( endPoint.getX() - startPoint.getX() );
			double dy = Math.abs( endPoint.getY() - startPoint.getY() );
			
			g.drawRect(x, y, (int) dx, (int) dy);

        }
    }

    /*
     * performs the zoomin action.
     * 
     * @see org.kalypso.ogc.widgets.AbstractWidget#performIntern()
     */
    protected final ICommand performIntern() {

        if (startPoint != null && endPoint != null) {
            double x = startPoint.getX();
            double y = startPoint.getY();
            double x2 = endPoint.getX();
            double y2 = endPoint.getY();
                        
            double wi = (x2 - x);
            double he = (y2 - y);

            double centerx = (x2 + x) / 2.0;
            double centery = (y2 + y) / 2.0;

            double xmin = centerx - (wi / 2.0);
            double xmax = centerx + (wi / 2.0);
            double ymax = centery + (he / 2.0);
            double ymin = centery - (he / 2.0);
            double dx = Math.abs( x2 - x );
    		double dy = Math.abs( y2 - y );
    		
    		//performs zoomin by point
            if (dx < 5 || dy < 5){
                dx = 5;
                dy = 5;
                int facX = (int)Math.round(dx * 75);
    			int facY = (int)Math.round(dy * 75);
    			xmin = x - ( facX / 2.0 );
    			xmax = x + ( facX / 2.0 );
    			ymin = y - ( facY / 2.0 );
    			ymax = y + ( facY / 2.0 );
    	    }
            GM_Envelope zoomBox = getBox(xmin, ymin, xmax, ymax);//GeometryFactory.createGM_Envelope(

            startPoint = null;
            endPoint = null;

            return new ChangeExtentCommand(getMapPanel(), zoomBox);
        }

        return null;
    }

}