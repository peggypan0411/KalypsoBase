/*
 * --------------- Kalypso-Header --------------------------------------------
 * 
 * This file is part of kalypso. Copyright (C) 2004, 2005 by:
 * 
 * Technical University Hamburg-Harburg (TUHH) Institute of River and coastal engineering Denickestr. 22 21073 Hamburg,
 * Germany http://www.tuhh.de/wb
 * 
 * and
 * 
 * Bjoernsen Consulting Engineers (BCE) Maria Trost 3 56070 Koblenz, Germany http://www.bjoernsen.de
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Contact:
 * 
 * E-Mail: belger@bjoernsen.de schlienger@bjoernsen.de v.doemming@tuhh.de
 * 
 * ------------------------------------------------------------------------------------
 */
package org.kalypso.ogc.gml.featureview.dialog;

import java.util.Collection;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.kalypso.contribs.eclipse.jface.dialog.DialogSettingsUtils;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypso.ogc.gml.command.FeatureChange;
import org.kalypso.ogc.sensor.view.observationDialog.ObservationViewerDialog;
import org.kalypso.ui.KalypsoGisPlugin;
import org.kalypso.zml.obslink.ObjectFactory;
import org.kalypso.zml.obslink.TimeseriesLinkType;
import org.kalypsodeegree.model.feature.Feature;

/**
 * TimeserieLinkFeatureDialog
 * 
 * @author schlienger (23.05.2005)
 */
public class TimeserieLinkFeatureDialog implements IFeatureDialog
{
  private final Feature m_feature;

  private final IPropertyType m_ftp;

  private FeatureChange m_change;

  public TimeserieLinkFeatureDialog( final Feature feature, final IPropertyType ftp )
  {
    m_feature = feature;
    m_ftp = ftp;
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.dialog.IFeatureDialog#open(org.eclipse.swt.widgets.Shell)
   */
  @Override
  public int open( final Shell shell )
  {
    final ObservationViewerDialog dialog = new ObservationViewerDialog( shell );
    final IDialogSettings dialogSettings = DialogSettingsUtils.getDialogSettings( KalypsoGisPlugin.getDefault(), getClass().getName() );
    dialog.setDialogSettings( dialogSettings );

    final TimeseriesLinkType tslink = (TimeseriesLinkType) m_feature.getProperty( m_ftp );
    dialog.setContext( m_feature.getWorkspace().getContext() );
    dialog.setInput( tslink == null ? "" : tslink.getHref() ); //$NON-NLS-1$

    final int open = dialog.open();
    FeatureChange fChange = null;
    if( open == Window.OK )
    {
      final String href = (String) dialog.getInput();
      if( href == null )
        fChange = new FeatureChange( m_feature, m_ftp, null );
      else if( href != null && href.length() > 0 )
      {
        final ObjectFactory linkFactory = new ObjectFactory();
        final TimeseriesLinkType link = linkFactory.createTimeseriesLinkType();
        link.setHref( href );
        fChange = new FeatureChange( m_feature, m_ftp, link );
      }
    }
    m_change = fChange;
    return open;
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.dialog.IFeatureDialog#collectChanges(java.util.Collection)
   */
  @Override
  public void collectChanges( final Collection<FeatureChange> c )
  {
    if( m_change != null )
      c.add( m_change );
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.dialog.IFeatureDialog#getLabel()
   */
  @Override
  public String getLabel( )
  {
    return "..."; //$NON-NLS-1$
  }
}