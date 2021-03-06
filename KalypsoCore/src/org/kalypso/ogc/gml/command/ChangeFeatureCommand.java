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
package org.kalypso.ogc.gml.command;

import org.kalypso.commons.command.ICommand;
import org.kalypso.core.i18n.Messages;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypsodeegree.model.feature.Feature;

/**
 * This command changes the property of one feature
 * 
 * @author Gernot Belger
 */
public class ChangeFeatureCommand implements ICommand
{
  private final Feature m_feature;

  private final IPropertyType m_property;

  private final Object m_newValue;

  private final Object m_oldValue;

  public ChangeFeatureCommand( final Feature feature, final IPropertyType property, final Object newValue )
  {
    m_feature = feature;
    m_property = property;
    m_newValue = newValue;
    m_oldValue = feature.getProperty( property );
  }

  @Override
  public boolean isUndoable( )
  {
    return true;
  }

  @Override
  public void process( ) throws Exception
  {
    applyChanges( m_newValue );
  }

  @Override
  public void redo( ) throws Exception
  {
    applyChanges( m_newValue );
  }

  @Override
  public void undo( ) throws Exception
  {
    applyChanges( m_oldValue );
  }

  @Override
  public String getDescription( )
  {
    return Messages.getString( "org.kalypso.ogc.gml.command.ChangeFeatureCommand.0" ); //$NON-NLS-1$
  }

  protected void applyChanges( final Object valueToSet )
  {
    m_feature.setProperty( m_property, valueToSet );

    final FeatureChange change = new FeatureChange( m_feature, m_property, valueToSet );
    m_feature.getWorkspace().fireModellEvent( new FeatureChangeModellEvent( m_feature.getWorkspace(), new FeatureChange[] { change } ) );
  }

  public FeatureChange asFeatureChange( )
  {
    return new FeatureChange( m_feature, m_property, m_newValue );
  }
}