/*--------------- Kalypso-Header ------------------------------------------

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

--------------------------------------------------------------------------*/

package org.kalypso.ogc.sensor.view.wq;

import java.io.StringReader;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.kalypso.contribs.eclipse.core.resources.ResourceUtilities;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.metadata.ITimeseriesConstants;
import org.kalypso.ogc.sensor.timeseries.wq.wqtable.WQTableFactory;
import org.kalypso.ogc.sensor.timeseries.wq.wqtable.WQTableSet;
import org.kalypso.ogc.sensor.zml.ZmlFactory;
import org.kalypso.ui.internal.i18n.Messages;
import org.xml.sax.InputSource;

/**
 * This action is contributed to IFile Objects. If they are Zml-Files, then the WQ-Relation is shown, if availabe.
 * 
 * @author schlienger
 */
public class ViewWQRelationObjectContribution implements IObjectActionDelegate
{
  private IWorkbenchPart m_part = null;

  private ISelection m_selection;

  /**
   * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
   */
  @Override
  public void setActivePart( final IAction action, final IWorkbenchPart targetPart )
  {
    m_part = targetPart;
  }

  /**
   * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
   */
  @Override
  public void run( final IAction action )
  {
    final Object object = ((IStructuredSelection)m_selection).getFirstElement();
    IFile file = null;
    if( object instanceof IFile )
    {
      file = (IFile)object;
    }
    String wqTabelle = null;
    String wqWechmann = null;
    String obsName = "-"; //$NON-NLS-1$

    if( file != null )
    {
      try
      {
        final URL url = ResourceUtilities.createURL( file );
        final IObservation obs = ZmlFactory.parseXML( url ); //$NON-NLS-1$
        obsName = obs.getName();
        wqTabelle = obs.getMetadataList().getProperty( ITimeseriesConstants.MD_WQ_TABLE );
        wqWechmann = obs.getMetadataList().getProperty( ITimeseriesConstants.MD_WQ_WECHMANN );
        action.setEnabled( wqTabelle != null && !wqTabelle.isEmpty() );
      }
      catch( final Exception ignored )
      {
        action.setEnabled( false );
      }
    }

    final Shell shell = m_part.getSite().getShell();

    if( wqTabelle == null || wqTabelle.isEmpty() )
    {
      String msg = Messages.getString( "org.kalypso.ogc.sensor.view.wq.ViewWQRelationObjectContribution.2" ); //$NON-NLS-1$
      if( wqWechmann != null )
        msg += Messages.getString( "org.kalypso.ogc.sensor.view.wq.ViewWQRelationObjectContribution.3" ); //$NON-NLS-1$

      MessageDialog.openWarning( shell, action.getText(), msg );
      return;
    }

    try
    {
      final WQTableSet set = WQTableFactory.parse( new InputSource( new StringReader( wqTabelle ) ) );

      final WQRelationDialog dlg = new WQRelationDialog( shell, obsName, set );
      dlg.open();
    }
    catch( final Exception e )
    {
      e.printStackTrace();
    }
  }

  /**
   * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
   */
  @Override
  public void selectionChanged( final IAction action, final ISelection selection )
  {
    m_selection = selection;

    if( selection instanceof IStructuredSelection )
    {
      final Object object = ((IStructuredSelection)selection).getFirstElement();

      if( object instanceof IFile )
      {
        final IFile file = (IFile)object;
        action.setEnabled( file.getFileExtension().equalsIgnoreCase( "zml" ) ); //$NON-NLS-1$
      }
    }
  }
}
