package org.kalypso.ogc.gml.convert.source;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.kalypso.contribs.java.net.IUrlResolver;
import org.kalypso.core.i18n.Messages;
import org.kalypso.core.jaxb.TemplateUtilities;
import org.kalypso.gml.util.Refsource;
import org.kalypso.gml.util.SourceType;
import org.kalypso.gmlschema.GMLSchemaException;
import org.kalypso.ogc.gml.convert.GmlConvertException;
import org.kalypso.ogc.gml.convert.GmlConvertFactory;
import org.kalypsodeegree.model.feature.GMLWorkspace;

/**
 * @author Gernot Belger
 */
public class RefSourceHandler implements ISourceHandler
{
  private final String m_href;

  private final URL m_context;

  private final IUrlResolver m_resolver;

  private final Map< ? , ? > m_externData;

  public RefSourceHandler( final IUrlResolver resolver, final URL context, final Refsource source, final Map< ? , ? > externData )
  {
    m_resolver = resolver;
    m_context = context;
    m_externData = externData;
    m_href = source.getHref();
  }

  @Override
  public GMLWorkspace getWorkspace( ) throws GmlConvertException, GMLSchemaException
  {
    try
    {
      final URL sourceURL = m_resolver.resolveURL( m_context, m_href );

      final Unmarshaller unmarshaller = TemplateUtilities.JC_GMC.createUnmarshaller();
      final JAXBElement< ? > sourceElement = (JAXBElement< ? >) unmarshaller.unmarshal( sourceURL );
      final SourceType source = (SourceType) sourceElement.getValue();
      return GmlConvertFactory.loadSource( m_resolver, sourceURL, source, m_externData );
    }
    catch( final MalformedURLException e )
    {
      final String message = String.format( Messages.getString("RefSourceHandler_0"), m_href ); //$NON-NLS-1$
      throw new GmlConvertException( message, e );
    }
    catch( final JAXBException e )
    {
      final String message = String.format( Messages.getString("RefSourceHandler_1"), m_href ); //$NON-NLS-1$
      throw new GmlConvertException( message, e );
    }
  }
}