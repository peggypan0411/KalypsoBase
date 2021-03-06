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
package de.openali.odysseus.chart.factory.config;

import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.openali.odysseus.chart.factory.OdysseusChartFactory;
import de.openali.odysseus.chart.factory.config.parameters.impl.XmlbeansParameterContainer;
import de.openali.odysseus.chart.factory.config.resolver.ChartTypeResolver;
import de.openali.odysseus.chart.factory.util.IReferenceResolver;
import de.openali.odysseus.chart.framework.model.IChartModel;
import de.openali.odysseus.chart.framework.model.layer.IParameterContainer;
import de.openali.odysseus.chartconfig.x020.AxisType;
import de.openali.odysseus.chartconfig.x020.ParametersType;
import de.openali.odysseus.chartconfig.x020.ProviderType;
import de.openali.odysseus.chartconfig.x020.ReferencingType;

/**
 * @author Dirk Kuch
 */
public abstract class AbstractChartFactory
{
  private final IChartModel m_model;

  private final IReferenceResolver m_resolver;

  private final IExtensionLoader m_loader;

  private final URL m_context;

  protected static final String CONFIGURATION_TYPE_KEY = "de.openali.odysseus.chart.factory.configurationType"; //$NON-NLS-1$

  protected AbstractChartFactory( final IChartModel model, final IReferenceResolver resolver, final IExtensionLoader loader, final URL context )
  {
    m_model = model;
    m_resolver = resolver;
    m_loader = loader;
    m_context = context;
  }

  protected IChartModel getModel( )
  {
    return m_model;
  }

  protected IReferenceResolver getResolver( )
  {
    return m_resolver;
  }

  protected IExtensionLoader getLoader( )
  {
    return m_loader;
  }

  protected URL getContext( )
  {
    return m_context;
  }

  protected IParameterContainer createParameterContainer( final String ownerIdentifier, final ProviderType providerType )
  {
    return createParameterContainer( ownerIdentifier, providerType.getEpid(), providerType );
  }

  protected IParameterContainer createParameterContainer( final String ownerIdentifier, final String providerIdentifier, final ProviderType providerType )
  {
    ParametersType parameters = null;
    if( providerType != null )
      parameters = providerType.getParameters();

    return new XmlbeansParameterContainer( ownerIdentifier, providerIdentifier, parameters );
  }

  public AxisType findMapperType( final ReferencingType reference )
  {
    final String ref = reference.getRef();
    if( StringUtils.isNotEmpty( ref ) )
      return (AxisType) getResolver().resolveReference( ref );

    final String url = reference.getUrl();
    if( StringUtils.isNotEmpty( url ) )
    {
      try
      {
        final ChartTypeResolver resolver = ChartTypeResolver.getInstance();
        return resolver.findMapperType( url, getContext() );
      }
      catch( final CoreException e )
      {
        OdysseusChartFactory.getDefault().getLog().log( new Status( IStatus.ERROR, OdysseusChartFactory.PLUGIN_ID, e.getLocalizedMessage(), e ) );

        e.printStackTrace();
      }
    }

    throw new IllegalStateException( String.format( "MapperType not found: %s", reference.getUrl() ) ); //$NON-NLS-1$
  }

}
