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
package org.kalypso.ogc.sensor.properties;

import org.eclipse.core.expressions.PropertyTester;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.metadata.ITimeseriesConstants;
import org.kalypso.repository.IRepositoryItem;

/**
 * Property Tester for repository items
 * 
 * @author Gernot Belger
 */
public class RepositoryItemPropertyTester extends PropertyTester
{
  private static final String PROPERTY_HASWQTABLE = "repositoryItemHasWQTable"; //$NON-NLS-1$

  /**
   * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[], java.lang.Object)
   */
  @Override
  public boolean test( final Object receiver, final String property, final Object[] args, final Object expectedValue )
  {
    if( !(receiver instanceof IRepositoryItem) )
      return false;

    final IRepositoryItem item = (IRepositoryItem)receiver;

    if( PROPERTY_HASWQTABLE.equals( property ) )
      return testHasWQTable( item );

    return false;
  }

  private boolean testHasWQTable( final IRepositoryItem item )
  {
    final IObservation obs = (IObservation)item.getAdapter( IObservation.class );
    if( obs == null )
      return false;

    final String propTable = obs.getMetadataList().getProperty( ITimeseriesConstants.MD_WQ_TABLE );
    if( propTable == null )
      return false;

    return true;
  }

}
