package org.kalypso.ogc.gml.convert;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.kalypso.contribs.java.net.IUrlResolver;
import org.kalypso.core.i18n.Messages;
import org.kalypso.gml.util.CsvTargetType;
import org.kalypso.gml.util.CsvTargetType.Column;
import org.kalypso.ogc.gml.convert.target.ITargetHandler;
import org.kalypso.ogc.gml.serialize.CsvWriterVisitor;
import org.kalypsodeegree.model.feature.FeatureList;
import org.kalypsodeegree.model.feature.GMLWorkspace;

/**
 * @author belger
 */
public class CsvTargetHandler implements ITargetHandler
{
  private final CsvTargetType m_target;

  private final URL m_context;

  private final IUrlResolver m_resolver;

  public CsvTargetHandler( final IUrlResolver resolver, final URL context, final CsvTargetType target )
  {
    m_resolver = resolver;
    m_context = context;
    m_target = target;
  }

  @Override
  public void saveWorkspace( final GMLWorkspace workspace ) throws GmlConvertException
  {
    PrintWriter writer = null;
    try
    {
      final URL url = m_resolver.resolveURL( m_context, m_target.getHref() );

      writer = new PrintWriter( new BufferedWriter( m_resolver.createWriter( url ) ) );

      final String delemiter = m_target.getDelemiter();
      final boolean writeHeader = m_target.isWriteHeader();
      final String featurePath = m_target.getFeaturePath();
      final List<Column> columnList = m_target.getColumn();
      final Map<String, String> properties = new LinkedHashMap<>();
      for( final Iterator<Column> colIt = columnList.iterator(); colIt.hasNext(); )
      {
        final Column column = colIt.next();
        final String property = column.getValue();
        final String def = column.getDefault();
        final String label = column.getLabel() == null ? property : column.getLabel();
        properties.put( property, def );

        if( writeHeader )
        {
          writer.print( label );
          if( colIt.hasNext() )
            writer.print( delemiter );
        }
      }

      if( writeHeader )
        writer.println();

      final Object featureFromPath = workspace.getFeatureFromPath( featurePath );
      if( featureFromPath instanceof FeatureList )
      {
        final CsvWriterVisitor visitor = new CsvWriterVisitor( writer, properties, delemiter );
        ((FeatureList)featureFromPath).accept( visitor );
      }
      else
        throw new GmlConvertException( Messages.getString( "org.kalypso.ogc.gml.convert.CsvTargetHandler.0" ) + featurePath ); //$NON-NLS-1$
    }
    catch( final GmlConvertException gce )
    {
      throw gce;
    }
    catch( final Exception e )
    {
      throw new GmlConvertException( Messages.getString( "org.kalypso.ogc.gml.convert.CsvTargetHandler.1" ), e ); //$NON-NLS-1$
    }
    finally
    {
      IOUtils.closeQuietly( writer );
    }
  }
}
