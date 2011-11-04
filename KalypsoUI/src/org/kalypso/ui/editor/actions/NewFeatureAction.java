/*
 * --------------- Kalypso-Header --------------------------------------------------------------------
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
 * ---------------------------------------------------------------------------------------------------
 */
package org.kalypso.ui.editor.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Event;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.i18n.Messages;
import org.kalypso.ui.ImageProvider;
import org.kalypso.ui.catalogs.FeatureTypeImageCatalog;

/**
 * @author kuepfer
 */
public class NewFeatureAction extends Action
{
  private final NewFeaturePropertyScope m_scope;

  private final IFeatureType m_featureType;

  public NewFeatureAction( final NewFeaturePropertyScope scope, final IFeatureType featureType )
  {
    m_scope = scope;

    m_featureType = featureType;

    final String actionLabel = FeatureActionUtilities.newFeatureActionLabel( featureType );
    setText( actionLabel );

    setImageDescriptor( createImage() );
  }

  private ImageDescriptor createImage( )
  {
    final ImageDescriptor catalogDescriptor = FeatureTypeImageCatalog.getImage( null, m_featureType.getQName() );

    if( catalogDescriptor == null )
      return ImageProvider.IMAGE_FEATURE_NEW;

    return catalogDescriptor;
  }

  /**
   * @see org.eclipse.jface.action.Action#runWithEvent(org.eclipse.swt.widgets.Event)
   */
  @Override
  public void runWithEvent( final Event event )
  {
    try
    {
      m_scope.createNewFeature( m_featureType );
    }
    catch( final Exception e )
    {
      final IStatus status = StatusUtilities.statusFromThrowable( e );
      ErrorDialog.openError( event.widget.getDisplay().getActiveShell(), getText(), Messages.getString( "org.kalypso.ui.editor.gmleditor.ui.NewFeatureAction.0" ), status ); //$NON-NLS-1$
    }
  }

}
