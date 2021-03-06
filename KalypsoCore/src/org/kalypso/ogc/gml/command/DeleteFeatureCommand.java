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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.kalypso.commons.command.ICommand;
import org.kalypso.core.KalypsoCorePlugin;
import org.kalypso.core.i18n.Messages;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypso.gmlschema.property.relation.IRelationType;
import org.kalypso.ogc.gml.selection.EasyFeatureWrapper;
import org.kalypso.ogc.gml.selection.IFeatureSelectionManager;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.FeatureVisitor;
import org.kalypsodeegree.model.feature.GMLWorkspace;
import org.kalypsodeegree.model.feature.event.FeatureStructureChangeModellEvent;
import org.kalypsodeegree.model.feature.event.FeaturesChangedModellEvent;
import org.kalypsodeegree.model.feature.event.ModellEvent;

/**
 * @author Gernot Belger
 * @author Monika Th�l
 */
public class DeleteFeatureCommand implements ICommand
{
  private final Map<Feature, Integer> m_listIndexMap = new HashMap<>();

  final List<RemoveBrokenLinksCommand> m_removeBrokenLinksCommands = new ArrayList<>();

  private final Feature[] m_featuresToDelete;

  public DeleteFeatureCommand( final Feature featureToDelete )
  {
    m_featuresToDelete = new Feature[] { featureToDelete };
  }

  public DeleteFeatureCommand( final Feature[] featuresToDelete )
  {
    m_featuresToDelete = featuresToDelete;
  }

  public DeleteFeatureCommand( final EasyFeatureWrapper[] wrappers )
  {
    m_featuresToDelete = new Feature[wrappers.length];
    for( int i = 0; i < wrappers.length; i++ )
      m_featuresToDelete[i] = wrappers[i].getFeature();
  }

  @Override
  public boolean isUndoable( )
  {
    return true;
  }

  @Override
  public void process( ) throws Exception
  {
    delete();
  }

  @Override
  public void redo( ) throws Exception
  {
    delete();
  }

  @Override
  public void undo( ) throws Exception
  {
    final Map<Feature, List<Feature>> parentMap = new HashMap<>();

    for( final Feature featureToAdd : m_featuresToDelete )
    {
      final GMLWorkspace workspace = featureToAdd.getWorkspace();
      final Feature parentFeature = featureToAdd.getOwner();
      final IRelationType rt = featureToAdd.getParentRelation();

      if( workspace.contains( featureToAdd ) )
        continue;

      if( rt.isList() )
      {
        final int index = m_listIndexMap.get( featureToAdd ).intValue();
        workspace.addFeatureAsComposition( parentFeature, rt, index, featureToAdd );
      }
      else
        workspace.addFeatureAsComposition( parentFeature, rt, 0, featureToAdd );

      // collect infos for event
      if( !parentMap.containsKey( parentFeature ) )
        parentMap.put( parentFeature, new ArrayList<Feature>() );

      final List<Feature> children = parentMap.get( parentFeature );
      children.add( featureToAdd );
    }

    /* fire modell events */
    for( final Map.Entry<Feature, List<Feature>> entry : parentMap.entrySet() )
    {
      final Feature parentFeature = entry.getKey();
      final List<Feature> childList = entry.getValue();
      final Feature[] children = childList.toArray( new Feature[childList.size()] );
      final GMLWorkspace workspace = parentFeature.getWorkspace();
      workspace.fireModellEvent( new FeatureStructureChangeModellEvent( workspace, parentFeature, children, FeatureStructureChangeModellEvent.STRUCTURE_CHANGE_ADD ) );
    }

    for( final ICommand command : m_removeBrokenLinksCommands )
      command.undo();
  }

  @Override
  public String getDescription( )
  {
    return Messages.getString( "org.kalypso.ogc.gml.command.DeleteFeatureCommand.0" ); //$NON-NLS-1$
  }

  private void delete( ) throws Exception
  {
    m_removeBrokenLinksCommands.clear();

    final Set<GMLWorkspace> touchedWorkspaces = new HashSet<>();

    // collect event information
    final Map<Feature, List<Feature>> parentMap = new HashMap<>();

    for( final Feature featureToRemove : m_featuresToDelete )
    {
      final GMLWorkspace workspace = featureToRemove.getWorkspace();

      final Feature parentFeature = featureToRemove.getOwner();
      final IRelationType parentRelationType = featureToRemove.getParentRelation();

      if( !workspace.contains( featureToRemove ) )
        continue; // was already removed

      touchedWorkspaces.add( workspace );

      // Remember indices for undo
      if( parentRelationType.isList() )
      {
        final List< ? > list = (List< ? >) parentFeature.getProperty( parentRelationType );
        m_listIndexMap.put( featureToRemove, new Integer( list.indexOf( featureToRemove ) ) );
      }

      /* Remove the feature. */
      workspace.removeLinkedAsCompositionFeature( parentFeature, parentRelationType, featureToRemove );

      // collect infos for event
      if( !parentMap.containsKey( parentFeature ) )
        parentMap.put( parentFeature, new ArrayList<Feature>() );

      final List<Feature> children = parentMap.get( parentFeature );
      children.add( featureToRemove );
    }

    /* Remove from the selection, if it was selected. */
    final IFeatureSelectionManager selectionManager = KalypsoCorePlugin.getDefault().getSelectionManager();
    selectionManager.changeSelection( m_featuresToDelete, new EasyFeatureWrapper[] {} );

    final List<ModellEvent> linkEvents = new ArrayList<>();

    for( final GMLWorkspace workspace : touchedWorkspaces )
    {
      final FindLinksFeatureVisitor visitor = new FindLinksFeatureVisitor( workspace );
      workspace.accept( visitor, workspace.getRootFeature(), FeatureVisitor.DEPTH_INFINITE );

      final ModellEvent[] events = visitor.getEvents();
      for( final ModellEvent modellEvent : events )
        linkEvents.add( modellEvent );
    }

    for( final ICommand command : m_removeBrokenLinksCommands )
      command.process();

    /* fire events for deletion */
    for( final Map.Entry<Feature, List<Feature>> entry : parentMap.entrySet() )
    {
      final Feature parentFeature = entry.getKey();
      final List<Feature> childList = entry.getValue();
      final Feature[] children = childList.toArray( new Feature[childList.size()] );
      final GMLWorkspace workspace = parentFeature.getWorkspace();
      workspace.fireModellEvent( new FeatureStructureChangeModellEvent( workspace, parentFeature, children, FeatureStructureChangeModellEvent.STRUCTURE_CHANGE_DELETE ) );
    }

    /* fire events for removed links */
    for( final ModellEvent modellEvent : linkEvents )
    {
      final GMLWorkspace workspace = (GMLWorkspace) modellEvent.getEventSource();
      workspace.fireModellEvent( modellEvent );
    }
  }

  /**
   * @author Gernot Belger
   */
  private final class FindLinksFeatureVisitor implements FeatureVisitor
  {
    private final Set<Feature> m_changedFeatures = new HashSet<>();

    private final Map<Feature, FeatureStructureChangeModellEvent> m_structureEvents = new HashMap<>();

    private final GMLWorkspace m_workspace;

    public FindLinksFeatureVisitor( final GMLWorkspace workspace )
    {
      m_workspace = workspace;
    }

    public ModellEvent[] getEvents( )
    {
      final ModellEvent[] modellEvents = m_structureEvents.values().toArray( new ModellEvent[0] );
      return ArrayUtils.addAll( modellEvents, new ModellEvent[] { getChangedFeatures() } );
    }

    private FeaturesChangedModellEvent getChangedFeatures( )
    {
      return new FeaturesChangedModellEvent( m_workspace, m_changedFeatures.toArray( new Feature[0] ) );
    }

    // checks all properties for broken links
    @Override
    public boolean visit( final Feature f )
    {
      final IFeatureType ft = f.getFeatureType();
      final IPropertyType[] ftps = ft.getProperties();
      for( final IPropertyType ftp : ftps )
        if( ftp instanceof IRelationType )
        {
          final IRelationType linkftp = (IRelationType) ftp;
          if( linkftp.isList() )
          {
            final List< ? > propList = (List< ? >) f.getProperty( linkftp );
            // important: count down not up
            for( int k = propList.size() - 1; k >= 0; k-- )
              if( m_workspace.isBrokenLink( f, linkftp, k ) )
              {
                m_removeBrokenLinksCommands.add( new RemoveBrokenLinksCommand( m_workspace, f, linkftp, (String) propList.get( k ), k ) );
                m_structureEvents.put( f, new FeatureStructureChangeModellEvent( m_workspace, f, FeatureStructureChangeModellEvent.STRUCTURE_CHANGE_DELETE ) );
              }
          }
          else if( m_workspace.isBrokenLink( f, linkftp, 1 ) )
          {
            final String childID = (String) f.getProperty( linkftp );
            m_removeBrokenLinksCommands.add( new RemoveBrokenLinksCommand( m_workspace, f, linkftp, childID, 1 ) );
            m_changedFeatures.add( f );
          }
        }
      return true;
    }
  }
}