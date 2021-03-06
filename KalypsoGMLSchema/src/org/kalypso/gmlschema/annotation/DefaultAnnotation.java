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
package org.kalypso.gmlschema.annotation;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of {@link org.kalypso.gmlschema.adapter.IAnnotation}.
 *
 * @author Gernot Belger
 */
public class DefaultAnnotation implements IAnnotation
{
  private final String m_lang;

  private final String m_defaultValue;

  private final Map<String, String> m_values = new HashMap<>();

  public DefaultAnnotation( final String lang, final String defaultValue )
  {
    m_lang = lang;
    m_defaultValue = defaultValue;
  }

  /**
   * @see org.kalypso.gmlschema.adapter.IAnnotation#getLabel()
   */
  @Override
  public String getLabel( )
  {
    return getValue( ANNO_LABEL );
  }

  /**
   * @see org.kalypso.gmlschema.adapter.IAnnotation#getDescription()
   */
  @Override
  public String getDescription( )
  {
    return getValue( ANNO_DESCRIPTION );
  }

  /**
   * @see org.kalypso.gmlschema.adapter.IAnnotation#getTooltip()
   */
  @Override
  public String getTooltip( )
  {
    return getValue( ANNO_TOOLTIP );
  }

  /**
   * @see org.kalypso.gmlschema.adapter.IAnnotation#getLang()
   */
  public String getLang( )
  {
    return m_lang;
  }

  /**
   * Add a value to this annotation.
   */
  public void putValue( final String element, final String value )
  {
    /* Don't put null's into the hash */
    if( value == null )
      m_values.remove( element );
    else
      m_values.put( element, value );
  }

  /**
   * @see org.kalypso.gmlschema.adapter.IAnnotation#getValue(java.lang.String)
   */
  @Override
  public String getValue( final String element )
  {
    if( !m_values.containsKey( element ) )
      return m_defaultValue;

    return m_values.get( element );
  }

}
