/*----------------    FILE HEADER KALYPSO ------------------------------------------
 *
 *  This file is part of kalypso.
 *  Copyright (C) 2004 by:
 * 
 *  Technical University Hamburg-Harburg (TUHH)
 *  Institute of River and coastal engineering
 *  Denickestraße 22
 *  21073 Hamburg, Germany
 *  http://www.tuhh.de/wb
 * 
 *  and
 *  
 *  Bjoernsen Consulting Engineers (BCE)
 *  Maria Trost 3
 *  56070 Koblenz, Germany
 *  http://www.bjoernsen.de
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 *  Contact:
 * 
 *  E-Mail:
 *  belger@bjoernsen.de
 *  schlienger@bjoernsen.de
 *  v.doemming@tuhh.de
 *   
 *  ---------------------------------------------------------------------------*/
package org.kalypso.chart.ext.test.data;

import java.util.ArrayList;
import java.util.List;

import de.openali.odysseus.chart.framework.model.data.IDataContainer;
import de.openali.odysseus.chart.framework.model.data.IDataRange;
import de.openali.odysseus.chart.framework.model.data.impl.ComparableDataRange;

/**
 * @author burtscher1
 * 
 */
public class EditableTestDataContainer implements IDataContainer
{

  private final List<Object> m_targetData;

  private final List<Object> m_domainData;

  private final ComparableDataRange<Object> m_targetRange;

  private final ComparableDataRange<Object> m_domainRange;

  public EditableTestDataContainer( int size, double targetRange )
  {
    m_targetData = new ArrayList<Object>();
    m_domainData = new ArrayList<Object>();

    for( int i = 0; i < size; i++ )
    {
      m_domainData.add( new Double( i ) );
      m_targetData.add( Math.random() * targetRange );
    }

    m_targetRange = new ComparableDataRange<Object>( m_targetData.toArray( new Object[] {} ) );
    m_domainRange = new ComparableDataRange<Object>( m_domainData.toArray( new Object[] {} ) );

  }

  /**
   * @see org.kalypso.chart.framework.model.data.IDataContainer#close()
   */
  public void close( )
  {

  }

  /**
   * @see org.kalypso.chart.framework.model.data.IDataContainer#getDomainRange()
   */
  public IDataRange<Object> getDomainRange( )
  {
    return m_domainRange;
  }

  /**
   * @see org.kalypso.chart.framework.model.data.IDataContainer#getTargetRange()
   */
  public IDataRange<Object> getTargetRange( )
  {
    return m_targetRange;
  }

  /**
   * @see org.kalypso.chart.framework.model.data.IDataContainer#isOpen()
   */
  public boolean isOpen( )
  {
    return true;
  }

  /**
   * @see org.kalypso.chart.framework.model.data.IDataContainer#open()
   */
  public void open( )
  {
    // nothing to do

  }

  public List<Object> getTargetValues( )
  {
    return m_targetData;
  }

  public List<Object> getDomainValues( )
  {
    return m_domainData;
  }

}
