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
package org.kalypsodeegree_impl.graphics.displayelements;

import java.awt.Graphics;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.kalypsodeegree.graphics.displayelements.DisplayElement;
import org.kalypsodeegree.graphics.displayelements.DisplayElementDecorator;
import org.kalypsodeegree.graphics.transformation.GeoTransform;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.geometry.GM_Curve;
import org.kalypsodeegree.model.geometry.GM_Exception;
import org.kalypsodeegree.model.geometry.IPlainGridVisitable;
import org.kalypsodeegree.model.geometry.IPlainGridVisitor;

/**
 * FIXME: Does NOT belong in KalypsoDeegree plugin, this is specialized code for 1d2d.<br/>
 * Provide display mechanism for wind data models
 */
public class GridVisitableDisplayElement<P extends GM_Curve> implements DisplayElementDecorator
{
  public interface IGridVisitorFactory<P2 extends GM_Curve>
  {
    public IPlainGridVisitor<P2> createVisitor( final Graphics g, final GeoTransform projection );
  }

  private final IPlainGridVisitable<P> m_plainGridVisitable;

  private final Feature m_feature;

  private DisplayElement m_decorated;

  private final IGridVisitorFactory<P> m_visitorFactory;

  public GridVisitableDisplayElement( final Feature feature, final IPlainGridVisitable<P> gridVisitable, final IGridVisitorFactory<P> visitorFactory )
  {
    m_visitorFactory = visitorFactory;

    Assert.isNotNull( gridVisitable, "GridVisitableDisplayElement" ); //$NON-NLS-1$

    m_feature = feature;

    m_plainGridVisitable = gridVisitable;
  }

  @Override
  public Feature getFeature( )
  {
    return m_feature;
  }

  @Override
  public void paint( final Graphics g, final GeoTransform projection, final IProgressMonitor monitor ) throws CoreException
  {
    if( m_decorated != null )
    {
      m_decorated.paint( g, projection, monitor );
    }

    try
    {
      // TODO: give monitor to accept method and check for cancel!
      final IPlainGridVisitor<P> visitor = m_visitorFactory.createVisitor( g, projection );
      m_plainGridVisitable.acceptVisits( projection.getSourceRect(), visitor, monitor );
    }
    catch( final GM_Exception e )
    {
      e.printStackTrace();
    }
  }

  @Override
  public void setDecorated( final DisplayElement decorated )
  {
    m_decorated = decorated;
  }
}
