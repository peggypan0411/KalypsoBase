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

package org.kalypso.featureview.views.actions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.kalypso.commons.bind.JaxbUtilities;
import org.kalypso.featureview.KalypsoFeatureViewPlugin;
import org.kalypso.featureview.i18n.Messages;
import org.kalypso.featureview.views.FeatureView;
import org.kalypso.template.featureview.Featuretemplate;
import org.kalypso.template.featureview.Featuretemplate.Layer;
import org.kalypso.template.featureview.FeatureviewType;
import org.kalypso.template.featureview.ObjectFactory;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.GMLWorkspace;
import org.kalypsodeegree_impl.model.feature.FeaturePath;

/**
 * Speichert die FeatureView als .gft Datei
 * 
 * @author belger
 */
public class SaveAsTemplateActionDelegate implements IViewActionDelegate
{
  protected static final ObjectFactory templateOF = new ObjectFactory();

  protected static final JAXBContext templateJC = JaxbUtilities.createQuiet( ObjectFactory.class );

  private static final String STR_ALS_VORLAGE_SPEICHERN = Messages.getString( "org.kalypso.featureview.views.actions.SaveAsTemplateActionDelegate.0" ); //$NON-NLS-1$

  private IViewPart m_view;

  /**
   * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
   */
  @Override
  public void init( final IViewPart view )
  {
    m_view = view;
  }

  @Override
  public void run( final IAction action )
  {
    final FeatureView view = (FeatureView)m_view;
    final Shell shell = view.getSite().getShell();

    final GMLWorkspace gmlWorkspace = view.getCurrentWorkspace();
    final Feature feature = view.getCurrentFeature();

    if( gmlWorkspace == null || feature == null )
    {
      MessageDialog.openWarning( shell, STR_ALS_VORLAGE_SPEICHERN, Messages.getString( "org.kalypso.featureview.views.actions.SaveAsTemplateActionDelegate.1" ) ); //$NON-NLS-1$
      return;
    }

    final SaveAsDialog dialog = new SaveAsDialog( shell );
    dialog.setBlockOnOpen( true );
    dialog.setTitle( STR_ALS_VORLAGE_SPEICHERN );
    if( dialog.open() != Window.OK )
      return;

    final IPath dlgResult = dialog.getResult();
    final IPath fileToWrite;
    if( dlgResult.getFileExtension() == null )
      fileToWrite = dlgResult.addFileExtension( "gft" ); //$NON-NLS-1$
    else
      fileToWrite = dlgResult;

    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
    final IWorkspaceRoot root = workspace.getRoot();
    final IFile file = root.getFile( fileToWrite );
    if( file.exists() )
    {
      if( !MessageDialog.openQuestion( shell, STR_ALS_VORLAGE_SPEICHERN, Messages.getString( "org.kalypso.featureview.views.actions.SaveAsTemplateActionDelegate.3" ) ) ) //$NON-NLS-1$
        return;
    }

    final Job job = new Job( Messages.getString( "org.kalypso.featureview.views.actions.SaveAsTemplateActionDelegate.4" ) ) //$NON-NLS-1$
    {
      @Override
      protected IStatus run( final IProgressMonitor monitor )
      {
        try
        {
          final Layer layer = templateOF.createFeaturetemplateLayer();
          layer.setFeaturePath( new FeaturePath( feature ).toString() );
          layer.setHref( gmlWorkspace.getContext().toExternalForm() );
          layer.setLinktype( "gml" ); //$NON-NLS-1$
          layer.setId( "layer_1" ); //$NON-NLS-1$

          final Featuretemplate template = templateOF.createFeaturetemplate();
          template.setLayer( layer );

          final List<FeatureviewType> viewList = template.getView();
          viewList.addAll( Arrays.asList( view.getCurrentViewTemplates() ) );

          final Map<String, String> prefixes = new HashMap<>( 1 );
          prefixes.put( "featureview.template.kalypso.org", "gft" ); //$NON-NLS-1$ //$NON-NLS-2$

          final Marshaller marshaller = JaxbUtilities.createMarshaller( templateJC, true, prefixes );

          final ByteArrayOutputStream bos = new ByteArrayOutputStream();
          marshaller.marshal( template, bos );
          bos.close();

          final ByteArrayInputStream bis = new ByteArrayInputStream( bos.toByteArray() );

          if( file.exists() )
            file.setContents( bis, false, true, monitor );
          else
            file.create( bis, false, monitor );
          bis.close();
        }
        catch( final JAXBException e )
        {
          return new Status( IStatus.ERROR, KalypsoFeatureViewPlugin.PLUGIN_ID, Messages.getString( "org.kalypso.featureview.views.actions.SaveAsTemplateActionDelegate.9" ), e ); //$NON-NLS-1$
        }
        catch( final CoreException e )
        {
          return e.getStatus();
        }
        catch( final IOException e )
        {
          return new Status( IStatus.ERROR, KalypsoFeatureViewPlugin.PLUGIN_ID, Messages.getString( "org.kalypso.featureview.views.actions.SaveAsTemplateActionDelegate.9" ), e ); //$NON-NLS-1$
        }

        return Status.OK_STATUS;
      }
    };

    job.setUser( true );
    job.schedule();
  }

  /**
   * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
   */
  @Override
  public void selectionChanged( final IAction action, final ISelection selection )
  {
    // nichts tun
  }
}
