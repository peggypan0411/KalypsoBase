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
package org.kalypso.ogc.sensor.tableview.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.kalypso.ogc.sensor.tableview.ITableViewRules;

/**
 * Holds a list of rules.
 * 
 * @author schlienger
 */
public class Rules implements ITableViewRules
{
  private final List m_rules = new ArrayList();

  private final Map m_map = new HashMap();

  public Rules()
  {
    // empty
  }
  
  /**
   * Constructor with given rules
   * @param rules
   */
  public Rules( final RenderingRule[] rules )
  {
    m_rules.addAll(  Arrays.asList( rules ) );
  }

  public void addRule( final RenderingRule rule )
  {
    m_rules.add( rule );
  }
  
  public void removeRule( final RenderingRule rule )
  {
    m_rules.remove( rule );
  }
  
  /**
   * Finds a rule that contains the mask
   * @param mask
   * @return list of rules that apply
   * @throws NoSuchElementException
   */
  public RenderingRule[] findRules( final int mask ) throws NoSuchElementException
  {
    RenderingRule[] r = (RenderingRule[])m_map.get( new Integer( mask ) );

    if( r != null )
      return r;

    List lrules = new ArrayList();
    
    for( Iterator it = m_rules.iterator(); it.hasNext(); )
    {
      RenderingRule rule = (RenderingRule)it.next();
      
      if( rule.contains( mask ) )
        lrules.add( rule );
    }

    r = (RenderingRule[])lrules.toArray( new RenderingRule[0]);
    m_map.put( new Integer( mask ), r );
    
    return r;
  }

  /**
   * @see org.kalypso.ogc.sensor.tableview.ITableViewRules#isEmpty()
   */
  public boolean isEmpty( )
  {
    return m_rules.size() == 0;
  }
}