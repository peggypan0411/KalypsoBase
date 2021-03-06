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
package org.kalypso.ogc.gml.featureview.maker;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.kalypso.commons.i18n.ITranslator;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.core.jaxb.TemplateUtilities;
import org.kalypso.gmlschema.GMLSchemaUtilities;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.template.featureview.Featuretemplate;
import org.kalypso.template.featureview.FeatureviewType;
import org.kalypso.template.types.I18NTranslatorType;
import org.kalypso.ui.KalypsoGisPlugin;
import org.kalypso.ui.catalogs.FeatureTypeFeatureviewCatalog;
import org.kalypsodeegree.model.feature.Feature;

/**
 * Helper class to manage generated feature-view-templates.
 * 
 * @author Belger
 */
public class CachedFeatureviewFactory implements IFeatureviewFactory
{
  /** Used for the compability-hack. Is it possible to get this from the binding classes? */
  private static String FEATUREVIEW_NAMESPACE = "featureview.template.kalypso.org"; //$NON-NLS-1$

  /** Map of especially added view-templates. */
  private final Map<QName, FeatureviewTypeWithContext> m_viewMap = new HashMap<>();

  private final Map<QName, FeatureviewTypeWithContext> m_cache = new HashMap<>();

  private final IFeatureviewFactory m_delegateFactory;

  /**
   * @param delegateFactory
   *          If the questioned featureType is not not in the cache, the delegateFactory is asked.
   */
  public CachedFeatureviewFactory( final IFeatureviewFactory delegateFactory )
  {
    m_delegateFactory = delegateFactory;
  }

  public void reset( )
  {
    m_cache.clear();
  }

  @Override
  public FeatureviewTypeWithContext get( final IFeatureType featureType, final Feature feature )
  {
    /* Is there a special view already registered for this type? */
    final QName qname = featureType != null ? featureType.getQName() : null;
    final FeatureviewTypeWithContext view = m_viewMap.get( qname );
    if( view != null )
      return view;

    // REMARK: this code section is for backwards compatibility. Before, for the typename, only
    // the local part was given in the featureViewType (type xs:string). Now it is of type xs:qname.
    // So old entries are interpretated against the namespace of the featureview, which allows us
    // to try against this namespace uri.
    if( qname != null )
    {
      final QName compabilityName = new QName( FEATUREVIEW_NAMESPACE, qname.getLocalPart(), qname.getPrefix() );
      final FeatureviewTypeWithContext compabilityView = m_viewMap.get( compabilityName );
      if( compabilityView != null )
        return compabilityView;
    }
    // REMARK end

    /* else ask cache */
    if( m_cache.containsKey( qname ) )
      return m_cache.get( qname );

    /* In order to allow substitution, we need to search inside the viewMap */
    for( final Map.Entry<QName, FeatureviewTypeWithContext> viewEntry : m_viewMap.entrySet() )
    {
      final QName key = viewEntry.getKey();
      if( featureType != null && key != null && GMLSchemaUtilities.substitutes( featureType, key ) )
        return viewEntry.getValue();
    }

    FeatureviewTypeWithContext newView = null;

    /* Maybe the catalog has a view for this type. */
    try
    {
      if( qname != null )
        newView = FeatureTypeFeatureviewCatalog.getFeatureview( null, qname );
    }
    catch( final JAXBException e )
    {
      // we only log it to the plugin, if we have a problem we create a default view
      final IStatus status = StatusUtilities.statusFromThrowable( e );
      KalypsoGisPlugin.getDefault().getLog().log( status );
    }

    /* Last thing to do: ask the delegate. */
    if( newView == null )
      newView = m_delegateFactory.get( featureType, feature );

    m_cache.put( qname, newView );

    return newView;
  }

  public void addView( final URL url )
  {
    Assert.isNotNull( url );

    try
    {
      final Unmarshaller unmarshaller = TemplateUtilities.createFeatureviewUnmarshaller();
      Object unmarshal = unmarshaller.unmarshal( url );
      if( unmarshal instanceof JAXBElement< ? > )
        unmarshal = ((JAXBElement< ? >)unmarshal).getValue();

      if( unmarshal instanceof FeatureviewType )
        addView( (FeatureviewType)unmarshal, url );
      else if( unmarshal instanceof Featuretemplate )
      {
        final Featuretemplate ftt = (Featuretemplate)unmarshal;
        final List<FeatureviewType> view = ftt.getView();
        for( final FeatureviewType element : view )
          addView( element, url );
      }
      else
        System.out.println( getClass().getName() + ": Unsupported type: " + unmarshal.getClass().getName() + " in " + url.toString() ); //$NON-NLS-1$ //$NON-NLS-2$
    }
    catch( final JAXBException e )
    {
      e.printStackTrace();
    }
  }

  /**
   * Adds a feature view template for its type. The added templates are used in preference before asking the delegate
   * factory.
   */
  public void addView( final FeatureviewType type, final URL context )
  {
    final FeatureviewTypeWithContext view = new FeatureviewTypeWithContext( type, context );
    m_viewMap.put( type.getTypename(), view );
  }

  /**
   * Adds all views of the given template to this factory and sets the default translator.
   */
  public void addViews( final Featuretemplate template, final URL context )
  {
    final List<FeatureviewType> view = template.getView();
    for( final FeatureviewType element : view )
      addView( element, context );

    final I18NTranslatorType translatorElement = template.getTranslator();
    final ITranslator translator = TemplateUtilities.createTranslator( translatorElement, context );
    setDefaultTranslator( translator );
  }

  @Override
  public ITranslator getDefaultTranslator( )
  {
    return m_delegateFactory.getDefaultTranslator();
  }

  @Override
  public void setDefaultTranslator( final ITranslator translator )
  {
    m_delegateFactory.setDefaultTranslator( translator );
  }

  @Override
  public ITranslator getTranslator( final FeatureviewTypeWithContext view, final URL context )
  {
    return m_delegateFactory.getTranslator( view, context );
  }
}
