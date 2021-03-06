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

import java.util.ArrayList;
import java.util.List;

import org.kalypso.ogc.gml.IKalypsoTheme;
import org.kalypso.ogc.gml.mapmodel.IKalypsoThemePredicate;
import org.kalypso.ogc.gml.mapmodel.IKalypsoThemeVisitor;

/**
 * TODO: give this visitor a better name; 'KalypsoThemeVisitor' indicates that this is some kind of standard
 * implementation (which it is not)
 * <p>
 * This visitor collects all IKalypsoThemes with the property specified with a predicate
 *
 * @author Thomas Jung
 */
public class KalypsoThemeVisitor implements IKalypsoThemeVisitor
{
  private final List<IKalypsoTheme> m_results = new ArrayList<>();

  private final IKalypsoThemePredicate m_predicate;

  private final boolean m_recurseIntoFailedPredicate;

  public KalypsoThemeVisitor( final IKalypsoThemePredicate predicate )
  {
    this( predicate, true );
  }

  public KalypsoThemeVisitor( final IKalypsoThemePredicate predicate, final boolean recurseIntoFailedPredicate )
  {
    m_predicate = predicate;
    m_recurseIntoFailedPredicate = recurseIntoFailedPredicate;
  }

  @Override
  public boolean visit( final IKalypsoTheme theme )
  {
    if( m_predicate.decide( theme ) )
    {
      m_results.add( theme );
      return true;
    }

    return m_recurseIntoFailedPredicate;
  }

  public IKalypsoTheme[] getFoundThemes( )
  {
    return m_results.toArray( new IKalypsoTheme[m_results.size()] );
  }
}
