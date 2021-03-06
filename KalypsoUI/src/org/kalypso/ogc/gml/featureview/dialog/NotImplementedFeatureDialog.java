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
package org.kalypso.ogc.gml.featureview.dialog;

import java.util.Collection;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.kalypso.ogc.gml.command.FeatureChange;
import org.kalypso.ui.internal.i18n.Messages;

/**
 * @author belger
 */
public class NotImplementedFeatureDialog implements IFeatureDialog
{
  private final String m_userInfoMessage;

  private final String m_cellInfoMessage;

  public NotImplementedFeatureDialog( final String userInfoMessage, final String cellInfoMessage )
  {
    m_userInfoMessage = userInfoMessage;
    m_cellInfoMessage = cellInfoMessage;
  }

  public NotImplementedFeatureDialog( )
  {
    m_userInfoMessage = Messages.getString( "org.kalypso.ogc.gml.featureview.dialog.NotImplementedFeatureDialog.implemented" ); //$NON-NLS-1$
    m_cellInfoMessage = Messages.getString( "org.kalypso.ogc.gml.featureview.dialog.NotImplementedFeatureDialog.editable" ); //$NON-NLS-1$
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.dialog.IFeatureDialog#open(org.eclipse.swt.widgets.Shell)
   */
  @Override
  public int open( final Shell shell )
  {
    MessageDialog.openInformation( shell, Messages.getString( "org.kalypso.ogc.gml.featureview.dialog.NotImplementedFeatureDialog.edit" ), m_userInfoMessage ); //$NON-NLS-1$
    return Window.CANCEL;
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.dialog.IFeatureDialog#collectChanges(java.util.Collection)
   */
  @Override
  public void collectChanges( final Collection<FeatureChange> c )
  {
    //
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.dialog.IFeatureDialog#getLabel()
   */
  @Override
  public String getLabel( )
  {
    return m_cellInfoMessage;
  }

}
