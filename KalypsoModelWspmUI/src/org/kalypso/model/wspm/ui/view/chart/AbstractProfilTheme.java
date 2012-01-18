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
package org.kalypso.model.wspm.ui.view.chart;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.kalypso.model.wspm.core.profil.IProfil;
import org.kalypso.model.wspm.core.profil.changes.ProfilChangeHint;
import org.kalypso.model.wspm.ui.i18n.Messages;
import org.kalypso.model.wspm.ui.view.ILayerStyleProvider;
import org.kalypso.observation.result.IComponent;

import de.openali.odysseus.chart.framework.model.data.IDataRange;
import de.openali.odysseus.chart.framework.model.data.impl.DataRange;
import de.openali.odysseus.chart.framework.model.layer.EditInfo;
import de.openali.odysseus.chart.framework.model.layer.IChartLayer;
import de.openali.odysseus.chart.framework.model.layer.IEditableChartLayer;
import de.openali.odysseus.chart.framework.model.layer.ILegendEntry;
import de.openali.odysseus.chart.framework.model.layer.impl.LegendEntry;
import de.openali.odysseus.chart.framework.model.layer.manager.visitors.EditableChartLayerVisitor;
import de.openali.odysseus.chart.framework.model.mapper.ICoordinateMapper;

/**
 * @author kimwerner
 */
public abstract class AbstractProfilTheme extends AbstractProfilLayer// implements IChartLayer
{
  private final String m_id;

  private String m_title;

  private ILegendEntry[] m_combinedEntry;

  public AbstractProfilTheme( final IProfil profil, final String id, final String title, final IProfilChartLayer[] chartLayers, final ICoordinateMapper cm )
  {
    this( profil, id, title, chartLayers, cm, null );
  }

  public AbstractProfilTheme( final IProfil profil, final String id, final String title, final IProfilChartLayer[] chartLayers, final ICoordinateMapper cm, final ILayerStyleProvider styleProvider )
  {
    super( id, profil, "", styleProvider ); //$NON-NLS-1$

    /* *grml* AbstractProfileLayer overwrites getTitle() implementation */
    setTitle( title );
    m_title = title;

    m_id = id;

    setCoordinateMapper( cm );

    if( chartLayers != null )
    {
      for( final IChartLayer layer : chartLayers )
        layer.setCoordinateMapper( cm );

      getLayerManager().addLayer( chartLayers );
    }
  }

  @Override
  public EditInfo commitDrag( final Point point, final EditInfo dragStartData )
  {
    if( getTargetComponent() != null )
    {
      getProfil().getSelection().setActivePointProperty( getTargetComponent() );
    }
    final IProfilChartLayer layer = getActiveLayer();
    if( layer == null )
      return null;

    if( dragStartData.getPosition() == point )
    {
      layer.executeClick( dragStartData );
    }
    else
    {
      layer.executeDrop( point, dragStartData );
    }

    return null;
  }

  public IChartLayer[] getLegendNodes( )
  {
    return getLayerManager().getLayers();
  }

  @Override
  public synchronized ILegendEntry[] getLegendEntries( )
  {
    if( ArrayUtils.isEmpty( m_combinedEntry ) )
    {
      final ILegendEntry[] entries = createLegendEntries();
      m_combinedEntry = entries;
    }
    return m_combinedEntry;
  }

  private ILegendEntry[] createLegendEntries( )
  {
    // TODO: implement combined legend entry and reuse
    final LegendEntry le = new LegendEntry( this, toString() )
    {
      @Override
      public void paintSymbol( final GC gc, final Point size )
      {
        for( final IChartLayer layer : getLayerManager().getLayers() )
        {
          final ILegendEntry[] les = layer.getLegendEntries();
          if( les == null || les.length == 0 )
          {
            continue;
          }
          for( final ILegendEntry l : les )
          {
            ((LegendEntry) l).paintSymbol( gc, size );
          }
        }
      }
    };
    return new ILegendEntry[] { le };
  }

  /**
   * *grml* AbstractProfileLayer overwrites getTitle() implementation
   */
  @Override
  public String getTitle( )
  {
    return m_title;
  }

  /**
   * *grml* AbstractProfileLayer overwrites getTitle() implementation
   */
  @Override
  public void setTitle( final String title )
  {
    super.setTitle( title );

    m_title = title;
  }

  @Override
  public EditInfo drag( final Point newPos, final EditInfo dragStartData )
  {
    final IProfilChartLayer layer = getActiveLayer();
    if( layer == null || layer.isLocked() )
      return null;
    return layer.drag( newPos, dragStartData );
  }

  @Override
  public void executeClick( final EditInfo clickInfo )
  {
    final IProfilChartLayer layer = getActiveLayer();
    if( layer != null )
    {
      layer.executeClick( clickInfo );
    }
  }

  @Override
  public void executeDrop( final Point point, final EditInfo dragStartData )
  {
    final IProfilChartLayer layer = getActiveLayer();
    if( layer != null )
    {
      layer.executeDrop( point, dragStartData );
    }
  }

  protected final void fireLayerContentChanged( )
  {
    getEventHandler().fireLayerContentChanged( this );
  }

  private IProfilChartLayer getActiveLayer( )
  {
    for( final IChartLayer l : getLayerManager().getLayers() )
    {
      if( l.isActive() && l instanceof IProfilChartLayer )
        return (IProfilChartLayer) l;
    }
    return null;
  }

  @Override
  public IComponent getDomainComponent( )
  {
    final IProfilChartLayer layer = getActiveLayer();
    return layer == null ? null : layer.getDomainComponent();
  }

  @Override
  public IDataRange< ? > getDomainRange( )
  {
    Double min = null;
    Double max = null;
    for( final IChartLayer layer : getLayerManager().getLayers() )
    {
      final IDataRange< ? > dr = layer.getDomainRange();
      if( dr != null )
      {
        final double drMax = ((Number) dr.getMax()).doubleValue();
        final double drMin = ((Number) dr.getMin()).doubleValue();

        max = max == null ? drMax : Math.max( max, drMax );
        min = min == null ? drMin : Math.min( min, drMin );
      }
    }
    if( min == null || max == null )
      return null;
    return new DataRange<Number>( min, max );
  }

  @Override
  public EditInfo getHover( final Point pos )
  {
    final IChartLayer[] layers = getLayerManager().getLayers();
    for( int i = layers.length - 1; i > -1; i-- ) // reverse layers, last paint will hover first
    {
      if( layers[i] instanceof IProfilChartLayer )
      {
        final IProfilChartLayer pLayer = (IProfilChartLayer) layers[i];
        final EditInfo info = pLayer.getHover( pos );
        if( info != null )
        {
          if( !pLayer.isActive() )
          {
            pLayer.setActive( true );
          }
          return info;
        }
      }
    }
    return null;

  }

  @Override
  public String getIdentifier( )
  {
    return m_id;
  }

// @Override
// public final ILayerManager getLayerManager( )
// {
// return m_layerManager;
// }

  @Override
  public IComponent getTargetComponent( )
  {
    final IProfilChartLayer layer = getActiveLayer();
    return layer == null ? null : layer.getTargetComponent();
  }

  @Override
  public IDataRange< ? > getTargetRange( final IDataRange< ? > domainIntervall )
  {
    Double min = null;
    Double max = null;
    for( final IChartLayer layer : getLayerManager().getLayers() )
    {
      final IDataRange< ? > dr = layer.getTargetRange( null );
      if( dr != null )
      {
        if( max == null )
        {
          max = ((Number) dr.getMax()).doubleValue();
        }
        else
        {
          max = Math.max( max, ((Number) dr.getMax()).doubleValue() );
        }
        if( min == null )
        {
          min = ((Number) dr.getMin()).doubleValue();
        }
        else
        {
          min = Math.min( min, ((Number) dr.getMin()).doubleValue() );
        }
      }
    }
    if( min == null || max == null )
      return null;
// if( min == max )
// min = 0.9 * max;
    return new DataRange<Number>( min, max );
  }

  @Override
  public void lockLayer( final boolean locked )
  {
    if( locked != isLocked() )
    {
      final EditableChartLayerVisitor visitor = new EditableChartLayerVisitor();
      getLayerManager().accept( visitor );

      for( final IEditableChartLayer layer : visitor.getLayers() )
      {
        layer.lockLayer( locked );
      }
    }

    super.lockLayer( locked );
  }

  @Override
  public void onProfilChanged( final ProfilChangeHint hint )
  {
    if( hint.isActivePointChanged() )
    {
      fireLayerContentChanged();
    }
    else
    {
      for( final IChartLayer layer : getLayerManager().getLayers() )
      {
        if( layer instanceof IProfilChartLayer )
        {
          ((IProfilChartLayer) layer).onProfilChanged( hint );
        }
      }
    }
  }

  @Override
  public void paint( final GC gc )
  {
    for( final IChartLayer layer : getLayerManager().getLayers() )
      if( layer.isVisible() )
      {
        layer.paint( gc );
      }
  }

  @Override
  public void removeYourself( )
  {
    throw new UnsupportedOperationException( Messages.getString( "org.kalypso.model.wspm.ui.view.chart.AbstractProfilTheme.0" ) ); //$NON-NLS-1$
  }

  @Override
  public void setProfil( final IProfil profil )
  {
    super.setProfil( profil );
    for( final IChartLayer layer : getLayerManager().getLayers() )
    {
      if( layer instanceof IProfilChartLayer )
      {
        ((IProfilChartLayer) layer).setProfil( profil );
      }
    }
  }
}
