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
package org.kalypso.simulation.core.ant.copyobservation.target;

import java.io.File;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.kalypso.commons.java.io.FileUtilities;
import org.kalypso.contribs.eclipse.core.resources.ResourceUtilities;
import org.kalypso.ogc.sensor.DateRange;
import org.kalypso.ogc.sensor.zml.ZmlURL;
import org.kalypso.simulation.core.KalypsoSimulationCorePlugin;
import org.kalypso.simulation.core.i18n.Messages;
import org.kalypso.zml.obslink.ObjectFactory;
import org.kalypso.zml.obslink.TimeseriesLinkType;
import org.kalypsodeegree.model.feature.Feature;

/**
 * FIXME: this class is only used by KalypsoNA. move this class to the NA plugin!
 *
 * @author Dirk Kuch
 */
public class CopyObservationNATarget extends AbstractObservationTarget
{
  private static final ObjectFactory OF = new ObjectFactory();

  private final File m_targetobservationDir;

  public CopyObservationNATarget( final URL context, final File targetobservationDir, final DateRange targetRange, final DateRange forecastRange )
  {
    super( context, targetRange, forecastRange );

    m_targetobservationDir = targetobservationDir;
  }

  @Override
  public final String getTargetHref( final Feature f ) throws CoreException
  {
    final TimeseriesLinkType targetlink = getTargetLink( f );
    if( targetlink == null )
    {
      throw new CoreException( new Status( IStatus.WARNING, KalypsoSimulationCorePlugin.getID(), Messages.getString( "org.kalypso.ogc.util.CopyObservationFeatureVisitor.1" ) + f.getId() ) );//$NON-NLS-1$
    }

    // remove query part if present, href is also used as file name here!
    final String href = ZmlURL.getIdentifierPart( targetlink.getHref() );
    return href;
  }

  private TimeseriesLinkType getTargetLink( final Feature f )
  {
    final String name = guessFilename( f );
    final File file = getValidFile( name, 0 );
    final TimeseriesLinkType link = OF.createTimeseriesLinkType();
    final IFile contextIFile = ResourceUtilities.findFileFromURL( getContext() );
    final File contextFile = contextIFile.getLocation().toFile();
    final String relativePathTo = FileUtilities.getRelativePathTo( contextFile, file );
    link.setHref( relativePathTo );
    return link;
  }

  private String guessFilename( final Feature f )
  {
    final String name = f.getName();

    if( StringUtils.isBlank( name ) )
      return f.getId();

    return name;
  }

  private File getValidFile( final String name, final int index )
  {
    final String newName;

    if( index > 0 )
      newName = name + "_" + Integer.toString( index ); //$NON-NLS-1$
    else
      newName = name;

    final String newName2 = FileUtilities.validateName( newName, "_" ); //$NON-NLS-1$
    final File file = new File( m_targetobservationDir, newName2 + ".zml" ); //$NON-NLS-1$
    if( file.exists() )
    {
      return getValidFile( name, index + 1 );
    }

    return file;
  }
}