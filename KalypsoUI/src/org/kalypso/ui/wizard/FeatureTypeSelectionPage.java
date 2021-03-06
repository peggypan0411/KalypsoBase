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
package org.kalypso.ui.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.kalypso.contribs.eclipse.jface.operation.ICoreRunnableWithProgress;
import org.kalypso.contribs.eclipse.jface.operation.RunnableContextHelper;
import org.kalypso.gmlschema.GMLSchema;
import org.kalypso.gmlschema.GMLSchemaCatalog;
import org.kalypso.gmlschema.GMLSchemaException;
import org.kalypso.gmlschema.KalypsoGMLSchemaPlugin;
import org.kalypso.gmlschema.annotation.IAnnotation;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.ogc.gml.filterdialog.model.FeatureTypeLabelProvider;
import org.kalypso.ui.internal.i18n.Messages;

/**
 * @author Gernot
 */
public class FeatureTypeSelectionPage extends WizardPage implements ISelectionChangedListener
{
  private IFeatureType m_selectedfeatureType = null;

  private ListViewer m_viewer;

  private String m_namespace;

  public FeatureTypeSelectionPage( )
  {
    super( "FeatureTypeSelectionPage", Messages.getString( "org.kalypso.ui.wizard.FeatureTypeSelectionPage.1" ), null ); //$NON-NLS-1$ //$NON-NLS-2$

    setPageComplete( false );
    setMessage( Messages.getString( "org.kalypso.ui.wizard.FeatureTypeSelectionPage.2" ) ); //$NON-NLS-1$
  }

  /**
   * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createControl( final Composite parent )
  {
    m_viewer = new ListViewer( parent, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER );

    m_viewer.setContentProvider( new ArrayContentProvider() );
    m_viewer.setLabelProvider( new FeatureTypeLabelProvider( IAnnotation.ANNO_NAME ) );
    m_viewer.setSorter( new ViewerSorter() );

    final List list = m_viewer.getList();
    final GridData listData = new GridData( SWT.FILL, SWT.FILL, true, true );
    listData.heightHint = 300;
    list.setLayoutData( listData );

    setControl( list );

    m_viewer.addSelectionChangedListener( this );

    m_viewer.addFilter( new ViewerFilter()
    {
      @Override
      public boolean select( final Viewer viewer, final Object parentElement, final Object element )
      {
        return element instanceof IFeatureType && !((IFeatureType)element).isAbstract();
      }
    } );

    update();
  }

  public void setNamespace( final String namespace )
  {
    if( m_namespace == null || !m_namespace.equals( namespace ) )
      m_namespace = namespace;
  }

  private void update( )
  {
    final ListViewer viewer = m_viewer;
    final String namespace = m_namespace;

    if( namespace == null )
    {
      m_viewer.setInput( null );
      return;
    }

    final ICoreRunnableWithProgress runnable = new ICoreRunnableWithProgress()
    {
      @Override
      public IStatus execute( final IProgressMonitor monitor ) throws InvocationTargetException
      {
        try
        {
          final GMLSchemaCatalog schemaCatalog = KalypsoGMLSchemaPlugin.getDefault().getSchemaCatalog();
          final GMLSchema schema = schemaCatalog.getSchema( namespace, (String)null );
          viewer.setInput( schema.getAllFeatureTypes() );
          return Status.OK_STATUS;
        }
        catch( final GMLSchemaException e )
        {
          throw new InvocationTargetException( e );
        }
      }
    };
    RunnableContextHelper.execute( getContainer(), false, false, runnable );
  }

  /**
   * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
   */
  @Override
  public void setVisible( final boolean visible )
  {
    super.setVisible( visible );

    if( visible == true )
      update();
  }

  /**
   * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
   */
  @Override
  public void selectionChanged( final SelectionChangedEvent event )
  {
    final IStructuredSelection selection = (IStructuredSelection)event.getSelection();
    m_selectedfeatureType = (IFeatureType)selection.getFirstElement();

    setPageComplete( m_selectedfeatureType != null );
  }

  public IFeatureType getSelectedFeatureType( )
  {
    return m_selectedfeatureType;
  }

}
