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
package org.kalypso.simulation.ui.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Generates a new property regex replacing a old one. The same rules as
 * {@link java.lang.String#replaceAll(java.lang.String, java.lang.String)}apply.
 * 
 * @author belger
 */
public class PropertyReplaceTask extends Task
{
  private final PropertyAdder m_propertyAdder = new PropertyAdder( this );

  private String m_name;

  private String m_value;

  private String m_regex;

  private String m_replacement;

  public void setName( final String name )
  {
    m_name = name;
  }

  public void setValue( final String value )
  {
    m_value = value;
  }

  public void setRegex( final String regex )
  {
    m_regex = regex;
  }

  public void setReplacement( final String replacement )
  {
    m_replacement = replacement;
  }

  /**
   * @see org.apache.tools.ant.Task#execute()
   */
  @Override
  public void execute( ) throws BuildException
  {
    final String newValue = m_value.replaceAll( m_regex, m_replacement );
    m_propertyAdder.addProperty( m_name, newValue, null );
  }
}
