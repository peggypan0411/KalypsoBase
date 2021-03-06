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

package org.kalypso.ogc.gml.selection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.xml.namespace.QName;

import org.kalypso.gmlschema.GMLSchemaUtilities;
import org.kalypso.ogc.gml.IKalypsoFeatureTheme;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.FeatureList;
import org.kalypsodeegree.model.feature.GMLWorkspace;
import org.kalypsodeegree.model.geometry.GM_Object;

/**
 * Helper methods for Feature-selection.
 * 
 * @author belger
 */
public final class FeatureSelectionHelper
{
  private FeatureSelectionHelper( )
  {
  }

  /** Return all features contained in the selection */
  public static Feature[] getFeatures( final IFeatureSelection selection )
  {
    return getFeatures( selection, null );
  }

  /**
   * Return all features contained in the selection which live in the given workspace. This does NOT include linked
   * features. <br>
   * TODO make this method work for linked features as well. Problem: the LinkedFeatureElement2 returned by the
   * GMLEditorContentProvider2 is defined in the KalypsoUI plugin and this helper lives in KalypsoCore. adding the
   * linked Element Support would add a new dependency. do we still need the LinkedFeatureElement2 object or can we
   * model it differnetly since the GMLWorkspace api changed considerably?
   * 
   * @param filterWorkspace
   *          if null, all features are returned
   */
  public static Feature[] getFeatures( final IFeatureSelection selection, final GMLWorkspace filterWorkspace )
  {
    final List< ? > list = selection.toList();
    final ArrayList<Feature> features = new ArrayList<>( list.size() );
    for( final Object element : list )
    {
      final Feature feature;
      if( element instanceof Feature )
        feature = (Feature)element;
      else if( element instanceof EasyFeatureWrapper )
        feature = ((EasyFeatureWrapper)element).getFeature();
      else
        feature = null;

      if( feature != null )
      {
        if( filterWorkspace == null || selection.getWorkspace( feature ) == filterWorkspace )
          features.add( feature );
      }
    }

    return features.toArray( new Feature[features.size()] );
  }

  /**
   * This function returns all features contained in the selection which are part of the given theme.
   * 
   * @param selection
   *          The selection.
   * @param filterTheme
   *          Only selected features of this theme will be returned. If null, all features will be returned.
   * @return The selected features of the theme.
   */
  public static Feature[] getFeaturesFromTheme( final IFeatureSelection selection, final IKalypsoFeatureTheme filterTheme )
  {
    /* If no filter theme is given, return all features of the selection. */
    if( filterTheme == null )
      return getFeatures( selection );

    /* Get the selection manager. */
    final IFeatureSelectionManager selectionManager = selection.getSelectionManager();

    /* Memory for the results. */
    final ArrayList<Feature> results = new ArrayList<>();

    /* Get all features of the filter theme. */
    final FeatureList features = filterTheme.getFeatureList();
    for( int i = 0; i < features.size(); i++ )
    {
      /* Get the feature. */
      final Feature feature = (Feature)features.get( i );
      if( selectionManager.isSelected( feature ) )
        results.add( feature );
    }

    return results.toArray( new Feature[] {} );
  }

  /** Return the amount of features contained in the given selection. */
  public static int getFeatureCount( final IFeatureSelection selection )
  {
    return getFeatures( selection ).length;
  }

  public static EasyFeatureWrapper[] createEasyWrappers( final IFeatureSelection selection )
  {
    final Feature[] features = getFeatures( selection );
    final EasyFeatureWrapper[] wrappers = new EasyFeatureWrapper[features.length];
    for( int i = 0; i < wrappers.length; i++ )
    {
      final Feature f = features[i];
      wrappers[i] = new EasyFeatureWrapper( selection.getWorkspace( f ), f );
    }

    return wrappers;
  }

  public static Feature getFirstFeature( final IFeatureSelection selection )
  {
    final Feature[] features = getFeatures( selection );
    if( features.length == 0 )
      return null;

    return features[0];
  }

  public static Feature[] getAllFeaturesWithGeometry( final IFeatureSelection selection )
  {
    final Feature[] features = getFeatures( selection );
    final ArrayList<Feature> result = new ArrayList<>();
    for( final Feature feature : features )
    {
      final GM_Object[] geometryProperties = feature.getGeometryPropertyValues();
      if( geometryProperties.length > 0 )
        result.add( feature );
    }
    return result.toArray( new Feature[result.size()] );
  }

  public static Feature[] getAllFeaturesOfType( final IFeatureSelection selection, final QName substitueeName )
  {
    final EasyFeatureWrapper[] features = selection.getAllFeatures();

    final List<Feature> resFeatures = new ArrayList<>();

    for( final EasyFeatureWrapper feature2 : features )
    {
      final Feature feature = feature2.getFeature();

      if( GMLSchemaUtilities.substitutes( feature.getFeatureType(), substitueeName ) )
        resFeatures.add( feature );
    }

    return resFeatures.toArray( new Feature[resFeatures.size()] );
  }

  /**
   * Returns either the focused feature or the first element.
   */
  public static Feature getSelectedFeature( final IFeatureSelection selection )
  {
    final Feature feature = selection.getFocusedFeature();
    return feature == null ? getFirstFeature( selection ) : feature;
  }

  public static EasyFeatureWrapper[] mergeWrapper( final EasyFeatureWrapper[] one, final EasyFeatureWrapper[] two )
  {
    // wird verwendt um zu pr�fen ob das Feature schon im ersten EasyFeatureWrapper[] array vorkommt
    final HashSet<Feature> features = new HashSet<>();
    final HashSet<EasyFeatureWrapper> res = new HashSet<>();
    for( final EasyFeatureWrapper wrapper : one )
    {
      final boolean b = features.add( wrapper.getFeature() );
      if( b )
      {
        res.add( wrapper );
      }
    }
    for( final EasyFeatureWrapper wrapper : two )
    {
      final boolean b = features.add( wrapper.getFeature() );
      if( b )
      {
        res.add( wrapper );
      }
    }
    return res.toArray( new EasyFeatureWrapper[res.size()] );
  }
}
