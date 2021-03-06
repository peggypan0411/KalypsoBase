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

package org.kalypso.metadoc.impl;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.kalypso.commons.java.io.FileUtilities;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.contribs.java.lang.ISupplier;
import org.kalypso.metadoc.IExporter;
import org.kalypso.metadoc.ui.ExportableTreeItem;

/**
 * Abstract exporter, which handles the common extension point stuff.
 * 
 * @author schlienger
 */
public abstract class AbstractExporter implements IExporter
{
  private String m_name;

  private String m_desc;

  private ImageDescriptor m_imageDescriptor;

  protected ISupplier m_supplier;

  /**
   * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement,
   *      java.lang.String, java.lang.Object)
   */
  @Override
  public final void setInitializationData( final IConfigurationElement config, final String propertyName, final Object data )
  {
    m_name = config.getAttribute( "name" ); //$NON-NLS-1$
    m_desc = config.getAttribute( "description" ); //$NON-NLS-1$

    final String iconLocation = config.getAttribute( "icon" ); //$NON-NLS-1$
    if( iconLocation != null )
      m_imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin( config.getDeclaringExtension().getContributor().getName(), iconLocation );
  }

  /**
   * @see org.kalypso.metadoc.IExporter#getName()
   */
  @Override
  public final String getName( )
  {
    return m_name;
  }

  /**
   * @see org.kalypso.metadoc.IExporter#setName(java.lang.String)
   */
  @Override
  public void setName( final String name )
  {
    m_name = name;
  }

  /**
   * @see org.kalypso.metadoc.IExporter#getDescription()
   */
  @Override
  public final String getDescription( )
  {
    return m_desc;
  }

  /** Overwrite the internal description */
  @Override
  public void setDescription( final String desc )
  {
    m_desc = desc;
  }

  /**
   * @see org.kalypso.metadoc.IExporter#getImageDescriptor()
   */
  @Override
  public ImageDescriptor getImageDescriptor( )
  {
    return m_imageDescriptor;
  }

  /**
   * @see org.kalypso.metadoc.IExporter#init(org.kalypso.contribs.java.lang.ISupplier)
   */
  @Override
  @SuppressWarnings("unused")
  public void init( final ISupplier supplier ) throws CoreException
  {
    m_supplier = supplier;
  }

  /**
   * @see org.kalypso.metadoc.IExporter#createTreeItem()
   */
  @Override
  public ExportableTreeItem createTreeItem( final ExportableTreeItem parent ) throws CoreException
  {
    final ExportableTreeItem item = new ExportableTreeItem( getName(), getImageDescriptor(), parent, null, true, false );
    item.setChildren( createTreeItems( item ) );
    return item;
  }

  @SuppressWarnings("unused")
  protected ExportableTreeItem[] createTreeItems( final ExportableTreeItem parent ) throws CoreException
  {
    return new ExportableTreeItem[0];
  }

  /**
   * Convenience method for subclasses which want to retrieve objects from the supplier set in the init() method.
   * <p>
   * Wraps the InvocationTargetException into a CoreException.
   */
  protected Object getFromSupplier( final Object request ) throws CoreException
  {
    try
    {
      return m_supplier.supply( request );
    }
    catch( final InvocationTargetException e )
    {
      throw new CoreException( StatusUtilities.statusFromThrowable( e ) );
    }
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString( )
  {
    return getName();
  }

  protected String validateDocumentName( final String documentName )
  {
    return FileUtilities.validateName( documentName, "_" ); //$NON-NLS-1$
  }

}
