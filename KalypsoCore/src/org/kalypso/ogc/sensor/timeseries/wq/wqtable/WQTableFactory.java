package org.kalypso.ogc.sensor.timeseries.wq.wqtable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.kalypso.binding.ratingtable.ObjectFactory;
import org.kalypso.binding.ratingtable.RatingTable;
import org.kalypso.binding.ratingtable.RatingTableList;
import org.kalypso.commons.bind.JaxbUtilities;
import org.kalypso.commons.serializer.ISerializer;
import org.kalypso.core.i18n.Messages;
import org.kalypso.ogc.sensor.timeseries.wq.WQException;
import org.xml.sax.InputSource;

/**
 * WQTableFactory
 * 
 * @author schlienger
 */
public final class WQTableFactory implements ISerializer<WQTableSet>
{
  private static final ObjectFactory OF = new ObjectFactory();

  private static final JAXBContext JC = JaxbUtilities.createQuiet( ObjectFactory.class );

  private WQTableFactory( )
  {
    // not intended to be instanciated
  }

  public static WQTableFactory getInstance( )
  {
    return new WQTableFactory();
  }

  /**
   * Parses the xml and return the binding object
   */
  public static RatingTableList parseSimple( final InputSource ins ) throws WQException
  {
    try
    {
      final Unmarshaller unm = JC.createUnmarshaller();
      final JAXBElement<RatingTableList> element = (JAXBElement<RatingTableList>) unm.unmarshal( ins );
      final RatingTableList xmlTableList = element.getValue();

      return xmlTableList;
    }
    catch( final Exception e )
    {
      throw new WQException( e );
    }
  }

  /**
   * Parses the xml and creates a WQTableSet object.
   * 
   * @return newly created WQTableSet object
   */
  public static WQTableSet parse( final InputSource ins ) throws WQException
  {
    try
    {
      final RatingTableList xmlTableList = parseSimple( ins );
      final List<RatingTable> xmlTables = xmlTableList.getTable();
      final WQTable[] tables = new WQTable[xmlTables.size()];
      int iTable = 0;
      for( final RatingTable ratingTable : xmlTables )
      {
        final Date validity = ratingTable.getValidity().getTime();
        final Integer offset = ratingTable.getOffset();

        final String xses = ratingTable.getX().trim();
        final String yses = ratingTable.getY().trim();

        final String[] strX = xses.length() == 0 ? new String[0] : xses.split( "," ); //$NON-NLS-1$
        final String[] strY = yses.length() == 0 ? new String[0] : yses.split( "," ); //$NON-NLS-1$

        if( strX.length != strY.length )
          throw new WQException( Messages.getString( "org.kalypso.ogc.sensor.timeseries.wq.wqtable.WQTableFactory.2" ) ); //$NON-NLS-1$

        final double[] arrW = new double[strX.length];
        final double[] arrQ = new double[strX.length];
        for( int i = 0; i < strX.length; i++ )
        {
          arrW[i] = Double.parseDouble( strX[i] );
          arrQ[i] = Double.parseDouble( strY[i] );
        }

        tables[iTable++] = new WQTable( validity, offset == null ? 0 : offset, arrW, arrQ );
      }

      return new WQTableSet( tables, xmlTableList.getFromType(), xmlTableList.getToType() );
    }
    catch( final Exception e ) // generic exception caught for simplicity
    {
      throw new WQException( e );
    }
  }

  /**
   * Creates a XML-String from the given WQTableSet object.
   * 
   * @return xml String
   */
  public static String createXMLString( final WQTableSet wqset ) throws WQException
  {
    try
    {
      final RatingTableList xmlTables = OF.createRatingTableList();

      xmlTables.setFromType( wqset.getFromType() );
      xmlTables.setToType( wqset.getToType() );

      final WQTable[] tables = wqset.getTables();
      for( final WQTable table : tables )
      {
        final RatingTable xmlTable = OF.createRatingTable();
        final Calendar cal = Calendar.getInstance();
        cal.setTime( table.getValidity() );
        xmlTable.setValidity( cal );
        xmlTable.setOffset( table.getOffset() );

        final WQPair[] pairs = table.getPairs();
        final double[] w = new double[pairs.length];
        final double[] q = new double[pairs.length];
        WQPair.convert2doubles( pairs, w, q );
        xmlTable.setX( ArrayUtils.toString( w ).replaceAll( "\\{", StringUtils.EMPTY ).replaceAll( "\\}", StringUtils.EMPTY ) ); //$NON-NLS-1$ //$NON-NLS-2$ 
        xmlTable.setY( ArrayUtils.toString( q ).replaceAll( "\\{", StringUtils.EMPTY ).replaceAll( "\\}", StringUtils.EMPTY ) ); //$NON-NLS-1$ //$NON-NLS-2$

        xmlTables.getTable().add( xmlTable );
      }

      final Marshaller marshaller = JaxbUtilities.createMarshaller( JC );
      marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

      final StringWriter writer = new StringWriter();
      final JAXBElement<RatingTableList> ratingTable = OF.createTables( xmlTables );
      marshaller.marshal( ratingTable, writer );

      return writer.toString();
    }
    catch( final JAXBException e )
    {
      throw new WQException( e );
    }
  }

  /**
   * Reads a WQTableSet from an InputStream
   * 
   * @see org.kalypso.commons.serializer.ISerializer#read(java.io.InputStream)
   */
  @Override
  public WQTableSet read( final InputStream ins ) throws InvocationTargetException
  {
    try
    {
      return parse( new InputSource( ins ) );
    }
    catch( final WQException e )
    {
      e.printStackTrace();
      throw new InvocationTargetException( e );
    }
  }

  /**
   * @see org.kalypso.commons.serializer.ISerializer#write(java.lang.Object, java.io.OutputStream)
   */
  @Override
  public void write( final WQTableSet object, final OutputStream os ) throws InvocationTargetException, IOException
  {
    try
    {
      final String xml = createXMLString( object );
      os.write( xml.getBytes() );
    }
    catch( final WQException e )
    {
      e.printStackTrace();
      throw new InvocationTargetException( e );
    }
  }
}
