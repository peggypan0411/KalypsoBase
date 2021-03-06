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
package org.kalypso.ogc.sensor.tableview;

import java.util.List;

import org.kalypso.ogc.sensor.IAxis;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.ObservationTokenHelper;
import org.kalypso.ogc.sensor.ObservationUtilities;
import org.kalypso.ogc.sensor.provider.IObsProvider;
import org.kalypso.ogc.sensor.status.KalypsoStatusUtils;
import org.kalypso.ogc.sensor.tableview.rules.ITableViewRules;
import org.kalypso.ogc.sensor.tableview.rules.RulesFactory;
import org.kalypso.ogc.sensor.template.ObsView;
import org.kalypso.ogc.sensor.template.ObsViewEvent;
import org.kalypso.ogc.sensor.timeseries.TimeseriesUtils;
import org.kalypso.ui.internal.i18n.Messages;

/**
 * A table view template for observations. Each observation is wrapped up in a theme which in turn delivers columns for
 * each value axis.
 * 
 * @author schlienger
 */
public class TableView extends ObsView
{
  private final ITableViewRules m_rules = RulesFactory.getDefaultRules();

  private boolean m_alphaSort = true;

  @Override
  public String toString( )
  {
    return Messages.getString( "org.kalypso.ogc.sensor.tableview.TableView.0" ); //$NON-NLS-1$
  }

  public ITableViewRules getRules( )
  {
    return m_rules;
  }

  public boolean isAlphaSort( )
  {
    return m_alphaSort;
  }

  /**
   * Set the flag for alphabetical sorting order. If true, columns are sorted according to their name in alphabetical
   * order.
   */
  public void setAlphaSort( final boolean alphaSort )
  {
    m_alphaSort = alphaSort;

    fireObsViewChanged( new ObsViewEvent( this, ObsViewEvent.TYPE_VIEW_CHANGED ) );
  }

  /**
   * Update the tableView with the new observation, perform best guess to know which columns will be added to the
   * tableView.
   * 
   * @see org.kalypso.ogc.sensor.template.ObsView#addObservation(org.kalypso.ogc.sensor.template.IObsProvider, java.lang.String, org.kalypso.ogc.sensor.template.ObsView.ItemData)
   */
  @Override
  public void addObservation( final IObsProvider provider, final String tokenizedName, final ItemData data )
  {
    final List<String> ignoreTypeList = getIgnoreTypesAsList();

    final IObservation obs = provider.getObservation();
    // FIXME: should also work if the provider has a null-observation currently. This code should always be called
    // again, if the provider-observation changes
    if( obs != null )
    {
      final IAxis[] axes = obs.getAxes();

      // do not even continue if there are no axes
      if( axes == null || axes.length == 0 )
        return;

      // actually just the first key axis is relevant in our case
      final IAxis[] keyAxes = ObservationUtilities.findAxesByKey( axes );

      // do not continue if no key axis
      if( keyAxes.length == 0 )
        return;

      for( int i = 0; i < axes.length; i++ )
      {
        // ignore axis if it is a kalypso status axis
        if( !KalypsoStatusUtils.isStatusAxis( axes[i] ) && !axes[i].equals( keyAxes[0] ) )
        {
          final IAxis valueAxis = ObservationUtilities.findAxisByName( axes, axes[i].getName() );

          if( !ignoreTypeList.contains( valueAxis.getType() ) )
          {
            // if axis has no name, default name will be generated (defined by tokenizedName)
            // otherwise, user defined axis name should take precedence
            String name = valueAxis.getName();
            // FIXME: the next line was probably introduced for Sachsen, but makes no sense...
            // Check where we need that for Sachsen and fix it there
            // if( name == null || name.length() == 0 )
            name = ObservationTokenHelper.replaceTokens( tokenizedName, obs, valueAxis );
            final TableViewColumn col = new TableViewColumn( this, provider.copy(), name, data.editable, 50, keyAxes[0], valueAxis, TimeseriesUtils.getDefaultFormatString( valueAxis.getType() ) );

            addItem( col );
          }
        }
      }
    }
  }
}