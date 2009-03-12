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
package org.kalypso.util.transformation;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.kalypso.eclipse.core.runtime.LogStatus;
import org.kalypso.ui.KalypsoGisPlugin;

/**
 * TransformationResult. Convenient class to be used with the ErrorDialog. This class
 * can create an IStatus object which can be displayed in the ErrorDialog. The trick
 * is to use the LogStatus which gets its children from the log file.
 * 
 * @author schlienger
 */
public class TransformationResult
{
  public final static TransformationResult OK_RESULT = new TransformationResult( "", null );
  
  private final String m_summary;
  private final IFile m_logFile;

  public TransformationResult( final String summary, final IFile logFile )
  {
    m_summary = summary;
    m_logFile = logFile;
  }

  /**
   * @return true if there are some messages for the user
   */
  public boolean hasMessages()
  {
    return m_summary.length() > 0;
  }
  
  /**
   * @return Returns the logFile.
   */
  public IFile getLogFile( )
  {
    return m_logFile;
  }
  
  /**
   * @return an IStatus representation of this result.
   */
  public IStatus toStatus()
  {
    if( !hasMessages() )
      return Status.OK_STATUS;
    
    // the ErrorDialog cannot show a message which is too long: the dialog's
    // height would be bigger than the user's monitor. That's why we truncate
    // the summary string here. If the user wants to see more, he can have
    // a look at the log file using the 'details' button in the ErrorDialog
    final String truncatedSummary = StringUtils.left( m_summary, 512 );
    final String msg = truncatedSummary + "...\n" + "Siehe Details oder Logdatei: " + m_logFile.getFullPath().toOSString();
    
    return new LogStatus( IStatus.WARNING, KalypsoGisPlugin.getId(), 0, msg, null, m_logFile );
  }
}
