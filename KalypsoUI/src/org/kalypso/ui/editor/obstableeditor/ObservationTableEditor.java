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
package org.kalypso.ui.editor.obstableeditor;

import java.awt.Frame;
import java.io.OutputStreamWriter;

import org.apache.commons.collections.ExtendedProperties;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.kalypso.commons.resources.SetContentHelper;
import org.kalypso.metadoc.IExportableObject;
import org.kalypso.metadoc.IExportableObjectFactory;
import org.kalypso.metadoc.configuration.PublishingConfiguration;
import org.kalypso.ogc.sensor.tableview.TableView;
import org.kalypso.ogc.sensor.tableview.TableViewUtils;
import org.kalypso.ogc.sensor.tableview.swing.ExportableObservationTable;
import org.kalypso.ogc.sensor.tableview.swing.ObservationTable;
import org.kalypso.template.obstableview.Obstableview;
import org.kalypso.ui.editor.abstractobseditor.AbstractObservationEditor;
import org.kalypso.ui.internal.i18n.Messages;

/**
 * The Observation TableEditor.
 * 
 * @author schlienger
 */
public class ObservationTableEditor extends AbstractObservationEditor implements IExportableObjectFactory
{
  public static final String EXTENSIN_OTT = ".ott"; //$NON-NLS-1$

  protected final ObservationTable m_table;

  private Composite m_swingContainer;

  /**
   * The ObservationTable is already created here because of the listening functionality that needs to be set up before
   * the template gets loaded.
   * <p>
   * Doing this stuff in createPartControl would prove inadequate, because the order in which createPartControl and loadIntern are called is not guaranteed to be always the same.
   */
  public ObservationTableEditor( )
  {
    super( new TableView(), EXTENSIN_OTT );

    m_table = new ObservationTable( (TableView)getView() );
  }

  /**
   * @return Returns the table.
   */
  public ObservationTable getTable( )
  {
    return m_table;
  }

  /**
   * @see org.kalypso.ui.editor.AbstractWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createPartControl( final Composite parent )
  {
    super.createPartControl( parent );

    m_swingContainer = new Composite( parent, SWT.RIGHT | SWT.EMBEDDED );
    final Frame vFrame = SWT_AWT.new_Frame( m_swingContainer );

    vFrame.add( m_table );

    vFrame.setVisible( true );
  }

  /**
   * @see org.kalypso.ui.editor.AbstractWorkbenchPart#dispose()
   */
  @Override
  public void dispose( )
  {
    m_table.dispose();

    super.dispose();
  }

  @Override
  public Object getAdapter( final Class adapter )
  {
    if( adapter == IExportableObjectFactory.class )
      return this;

    return super.getAdapter( adapter );
  }

  @Override
  protected void doSaveInternal( final IProgressMonitor monitor, final IFile file ) throws CoreException
  {
    final TableView template = (TableView)getView();
    if( template == null )
      return;

    final SetContentHelper helper = new SetContentHelper()
    {
      @Override
      protected void write( final OutputStreamWriter writer ) throws Throwable
      {
        final Obstableview type = TableViewUtils.buildTableTemplateXML( template, file.getParent() );

        TableViewUtils.saveTableTemplateXML( type, writer );
      }
    };

    helper.setFileContents( file, false, true, monitor );
  }

  /**
   * @see org.kalypso.ui.editor.AbstractWorkbenchPart#setFocus()
   */
  @Override
  public void setFocus( )
  {
    if( m_swingContainer != null )
      m_swingContainer.setFocus();
  }

  @Override
  public IExportableObject[] createExportableObjects( final ExtendedProperties configuration )
  {
    final ExportableObservationTable exportable = new ExportableObservationTable( m_table, getTitle(), Messages.getString( "org.kalypso.ui.editor.obstableeditor.ObservationTableEditor.0" ), getTitle(), null ); //$NON-NLS-1$
    return new IExportableObject[] { exportable };
  }

  @Override
  public IWizardPage[] createWizardPages( final PublishingConfiguration configuration, final ImageDescriptor defaultImage )
  {
    return new IWizardPage[0];
  }
}