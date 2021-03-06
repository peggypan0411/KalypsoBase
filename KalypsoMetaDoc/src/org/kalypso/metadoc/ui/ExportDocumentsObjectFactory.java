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
package org.kalypso.metadoc.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.ExtendedProperties;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.kalypso.metadoc.IExportableObject;
import org.kalypso.metadoc.IExportableObjectFactory;
import org.kalypso.metadoc.IExporter;
import org.kalypso.metadoc.KalypsoMetaDocPlugin;
import org.kalypso.metadoc.configuration.PublishingConfiguration;

/**
 * @author Gernot Belger
 */
public class ExportDocumentsObjectFactory implements IExportableObjectFactory
{
  private final IExporter[] m_exporter;

  private ExportableTreePage m_page;

  public ExportDocumentsObjectFactory( final IExporter[] exporter )
  {
    m_exporter = exporter;
  }

  @Override
  public IExportableObject[] createExportableObjects( final ExtendedProperties configuration )
  {
    final Object[] checkedElements = m_page.getCheckedElements();
    final List<IExportableObject> result = new ArrayList<>( checkedElements.length );
    for( final Object object : checkedElements )
    {
      final ExportableTreeItem treeItem = (ExportableTreeItem) object;
      final IExportableObject exportableObject = treeItem.getExportableObject();
      if( exportableObject != null )
        result.add( exportableObject );
    }

    return result.toArray( new IExportableObject[result.size()] );
  }

  @Override
  public IWizardPage[] createWizardPages( final PublishingConfiguration configuration, final ImageDescriptor defaultImage ) throws CoreException
  {
    // wizard selection for each exporter
    final ExportableTreeItem[] exportItems = new ExportableTreeItem[m_exporter.length];
    for( int i = 0; i < m_exporter.length; i++ )
    {
      final IExporter exporter = m_exporter[i];
      exportItems[i] = exporter.createTreeItem( null );
    }

    final ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin( KalypsoMetaDocPlugin.getId(), "icons/metadoc_wiz.gif" ); //$NON-NLS-1$

    m_page = new ExportableTreePage( "templatePage", "Wählen Sie die zu exportierenden Dokumente", imageDescriptor ); //$NON-NLS-1$
    m_page.setViewerSorter( new ViewerSorter() );
    m_page.setInput( exportItems );

    final List<ExportableTreeItem> checkedItems = new ArrayList<>();
    final List<ExportableTreeItem> grayedItems = new ArrayList<>();
    ExportableTreeItem.filterChecked( exportItems, checkedItems, grayedItems );
    m_page.setChecked( checkedItems.toArray( new Object[checkedItems.size()] ) );
    m_page.setGrayed( grayedItems.toArray( new Object[grayedItems.size()] ) );

    return new IWizardPage[] { m_page };
  }

}
