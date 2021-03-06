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
import org.kalypso.gmlschema.property.relation.IRelationType;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.FeatureList;
import org.kalypsodeegree.model.feature.GMLWorkspace;
import org.kalypsodeegree.model.feature.event.FeatureStructureChangeModellEvent;

/**
 * Command that wraps {@link Feature#removeMember(IRelationType, Feature)}
 *
 * @author Gernot Belger
 */
public class RemoveMemberCommand implements ICommand
{
  private final Feature m_srcFE;

  private final Object m_member;

  private final IRelationType m_linkRelation;

  private int m_pos;

  public RemoveMemberCommand( final Feature srcFE, final IRelationType linkPropName, final Object member )
  {
    m_srcFE = srcFE;
    m_linkRelation = linkPropName;
    m_member = member;
  }

  @Override
  public boolean isUndoable( )
  {
    return true;
  }

  @Override
  public void process( ) throws Exception
  {
    final GMLWorkspace workspace = m_srcFE.getWorkspace();

    m_pos = m_srcFE.removeMember( m_linkRelation, m_member );

    final Feature removedElement = m_member instanceof Feature ? (Feature) m_member : null;
    workspace.fireModellEvent( new FeatureStructureChangeModellEvent( workspace, m_srcFE, removedElement, FeatureStructureChangeModellEvent.STRUCTURE_CHANGE_DELETE ) );

    workspace.fireModellEvent( new FeatureChangeModellEvent( workspace, new FeatureChange[] { new FeatureChange( m_srcFE, m_linkRelation, null ) } ) );
  }

  @Override
  public void redo( ) throws Exception
  {
    process();
  }

  @Override
  public void undo( ) throws Exception
  {
    final GMLWorkspace workspace = m_srcFE.getWorkspace();

    if( m_linkRelation.isList() )
    {
      final FeatureList memberList = (FeatureList) m_srcFE.getProperty( m_linkRelation );
      memberList.add( m_pos, m_member );
    }
    else
    {
      m_srcFE.setProperty( m_linkRelation, m_member );
    }

    final Feature addedElement = m_member instanceof Feature ? (Feature) m_member : null;
    workspace.fireModellEvent( new FeatureStructureChangeModellEvent( workspace, m_srcFE, addedElement, FeatureStructureChangeModellEvent.STRUCTURE_CHANGE_ADD ) );
  }

  @Override
  public String getDescription( )
  {
    return "Remove link"; //$NON-NLS-1$
  }
}