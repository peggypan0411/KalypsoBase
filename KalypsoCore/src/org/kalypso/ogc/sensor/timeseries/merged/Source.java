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
package org.kalypso.ogc.sensor.timeseries.merged;

import java.util.Date;

import org.kalypso.contribs.java.util.DateUtilities;
import org.kalypso.ogc.sensor.DateRange;

public class Source
{
  /** Timesries link to the source observation. */
  private String m_link;

  /** Property of the source timeseries. Overrules 'link' if set */
  private String m_property;

  private Date m_from;

  private Date m_to;

  private String m_filter;

  public Source( )
  {
  }

  public Source( final String link, final String property, final DateRange daterange, final String filter )
  {
    m_link = link;
    m_property = property;

    if( daterange != null )
    {
      m_from = daterange.getFrom();
      m_to = daterange.getTo();
    }

    m_filter = filter;
  }

  public final String getProperty( )
  {
    return m_property;
  }

  public final void setProperty( final String prop )
  {
    m_property = prop;
  }

  public final String getLink( )
  {
    return m_link;
  }

  public final void setLink( final String prop )
  {
    m_link = prop;
  }

  public final Date getFrom( )
  {
    return m_from;
  }

  public final void setFrom( final String lfrom )
  {
    m_from = DateUtilities.parseDateTime( lfrom );
  }

  public final Date getTo( )
  {
    return m_to;
  }

  public final void setTo( final String lto )
  {
    m_to = DateUtilities.parseDateTime( lto );
  }

  public final String getFilter( )
  {
    return m_filter;
  }

  public final void setFilter( final String filt )
  {
    m_filter = filt;
  }

  public final DateRange getDateRange( )
  {
    return DateRange.createDateRangeOrNull( m_from, m_to );
  }
}