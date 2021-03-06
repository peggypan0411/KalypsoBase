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
package org.kalypso.ogc.gml.featureview.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypso.ui.internal.i18n.Messages;
import org.kalypsodeegree.model.feature.Feature;

/**
 * This class controls the GeometryLabel and its behavior.
 * 
 * @author Holger Albert
 */
public class GeometryFeatureControl extends AbstractFeatureControl
{
  /**
   * This variable stores the label itself.
   */
  private Label m_label;

  public GeometryFeatureControl( final Feature feature, final IPropertyType ftp )
  {
    super( feature, ftp );
  }

  @Override
  public Control createControl( final Composite parent, final int style )
  {
    /* Create a new label. */
    m_label = new Label( parent, style | SWT.WRAP );

    updateControl();

    return m_label;
  }

  @Override
  public void updateControl( )
  {
    final Feature feature = getFeature();

    if( feature != null )
    {
      final IPropertyType ftp = getFeatureTypeProperty();

      if( feature.getProperty( ftp ) != null )
        m_label.setText( Messages.getString( "org.kalypso.ogc.gml.featureview.control.GeometryFeatureControl.0" ) ); //$NON-NLS-1$
      else
        m_label.setText( Messages.getString( "org.kalypso.ogc.gml.featureview.control.GeometryFeatureControl.1" ) ); //$NON-NLS-1$
    }
  }

  @Override
  public boolean isValid( )
  {
    return true;
  }
}