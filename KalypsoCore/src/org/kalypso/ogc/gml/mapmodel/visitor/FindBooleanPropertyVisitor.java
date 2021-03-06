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
package org.kalypso.ogc.gml.mapmodel.visitor;

import java.util.LinkedHashSet;
import java.util.Set;

import org.kalypso.ogc.gml.IKalypsoTheme;
import org.kalypso.ogc.gml.mapmodel.IKalypsoThemeVisitor;

/**
 * find themes boolean property = xyz
 *
 * @author Dirk Kuch
 */
public class FindBooleanPropertyVisitor implements IKalypsoThemeVisitor
{
  Set<IKalypsoTheme> m_themes = new LinkedHashSet<>();

  private final String m_property;

  public FindBooleanPropertyVisitor( final String property )
  {
    m_property = property;
  }

  /**
   * @see org.kalypso.ogc.gml.mapmodel.IKalypsoThemeVisitor#visit(org.kalypso.ogc.gml.IKalypsoTheme)
   */
  @Override
  public boolean visit( final IKalypsoTheme theme )
  {
    final String property = theme.getProperty( m_property, "false" ); //$NON-NLS-1$
    if( Boolean.valueOf( property ) )
    {
      m_themes.add( theme );
    }

    return true;
  }

  public IKalypsoTheme[] getThemes( )
  {
    return m_themes.toArray( new IKalypsoTheme[] {} );
  }

}
