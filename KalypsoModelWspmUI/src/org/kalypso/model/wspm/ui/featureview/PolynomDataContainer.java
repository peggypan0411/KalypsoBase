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
package org.kalypso.model.wspm.ui.featureview;

import org.kalypsodeegree_impl.gml.binding.math.IPolynomial1D;
import org.kalypsodeegree_impl.gml.binding.math.PolynomialUtilities;

import de.openali.odysseus.chart.framework.model.data.DataRange;
import de.openali.odysseus.chart.framework.model.data.IDataRange;

/**
 * @author burtscher1
 */
class PolynomDataContainer
{
  private final IPolynomial1D[] m_polyArray;

  public PolynomDataContainer( final IPolynomial1D[] polyArray )
  {
    m_polyArray = polyArray;
  }

  public IDataRange<Double> getDomainRange( )
  {
    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;

    for( final IPolynomial1D poly : m_polyArray )
    {
      final double rangeMin = poly.getRangeMin();
      final double rangeMax = poly.getRangeMax();

      /* Real paranoic: make sure min always less than max */
      final double rangeNormMin = Math.min( rangeMin, rangeMax );
      final double rangeNormMax = Math.max( rangeMin, rangeMax );

      if( rangeNormMin < min )
      {
        min = rangeNormMin;
      }

      if( rangeNormMax > max )
      {
        max = rangeNormMax;
      }
    }

    return DataRange.createFromComparable(  min, max );
  }

  public IDataRange<Double> getTargetRange( )
  {
    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;

    final IDataRange<Double> domainRange = getDomainRange();
    final double domainMin = domainRange.getMin();
    final double domainMax = domainRange.getMax();

    // TODO: handle case where domainMin == domainMax

    final int tickCount = m_polyArray.length * 10;
    final double tick = (domainMax - domainMin) / tickCount;

    for( double pos = domainMin; pos < domainMax; pos += tick )
    {
      final IPolynomial1D poly = PolynomialUtilities.getPoly( m_polyArray, pos );
      if( poly == null )
      {
        continue;
      }

      final double value = poly.computeResult( pos );

      if( value < min )
      {
        min = value;
      }

      if( value > max )
      {
        max = value;
      }
    }
    // 10% Toleranz
    final double range = Math.abs( max - min );

    if( range != 0 )
    {
      min = min ;//- 0.1 * range;
      max = max ;//+ 0.1 * range;
    }
    else
    {
      // Falls die Range 0 ist (min==max), werden die Grenzen um 10% des Werts verteilt
      if( min != 0 )
      {
        min = min - 0.1 * min;
        max = max + 0.1 * max;
      }
      // Falls die Range und der Wert 0 sind, werden +-0.1 als fiktive Grenzen verwendet
      else
      {
        min = -0.1;
        max = +0.1;
      }
    }

    return DataRange.createFromComparable(min,  max );
  }

  public IPolynomial1D[] getPolyArray( )
  {
    return m_polyArray;
  }
}