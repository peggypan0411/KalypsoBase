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
package org.kalypso.gml.ui.internal.shape;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.kalypso.commons.databinding.validation.TypedValidator;
import org.kalypso.gml.ui.i18n.Messages;

/**
 * Validates an {@link org.eclipse.core.runtime.IPath} and fails, if it is an existing directory.
 * 
 * @author Gernot Belger
 */
public class PathIsDirectoryValidator extends TypedValidator<IPath>
{
  private static final String DEFAULT_MESSAGE = Messages.getString( "PathIsDirectoryValidator_0" ); //$NON-NLS-1$

  public PathIsDirectoryValidator( final int severity )
  {
    this( severity, DEFAULT_MESSAGE );
  }

  public PathIsDirectoryValidator( final int severity, final String message )
  {
    super( IPath.class, severity, message );
  }

  /**
   * @see org.kalypso.gml.ui.internal.shape.TypedValidator#doValidate(java.lang.Object)
   */
  @Override
  protected IStatus doValidate( final IPath value ) throws CoreException
  {
    if( value == null )
      return Status.OK_STATUS;

    ValidationStatus.ok();

    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
    final IWorkspaceRoot root = workspace.getRoot();

    // Check: we need to check folder and project separately. Is there an easier way to do that?
    if( value.segmentCount() == 1 )
    {
// final IProject project = root.getProject( value.lastSegment() );
// if( project.exists() )
      fail();
    }

    final IFolder folder = root.getFolder( value );
    if( folder.exists() )
      fail();

    return Status.OK_STATUS;
  }
}
