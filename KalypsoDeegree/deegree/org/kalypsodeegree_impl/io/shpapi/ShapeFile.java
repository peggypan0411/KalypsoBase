/** This file is part of kalypso/deegree.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * history:
 *
 * Files in this package are originally taken from deegree and modified here
 * to fit in kalypso. As goals of kalypso differ from that one in deegree
<<<<<<< .working
 * interface-compatibility to deegree is wanted but not retained always.
 * 
 * If you intend to use this software in other ways than in kalypso
=======
 * interface-compatibility to deegree is wanted but not retained always.
 *
 * If you intend to use this software in other ways than in kalypso
>>>>>>> .merge-right.r3720
 * (e.g. OGC-web services), you should consider the latest version of deegree,
 * see http://www.deegree.org .
 *
 * all modifications are licensed as deegree,
 * original copyright:
 *
 * Copyright (C) 2001 by:
 * EXSE, Department of Geography, University of Bonn
 * http://www.giub.uni-bonn.de/exse/
 * lat/lon GmbH
 * http://www.lat-lon.de
 */
package org.kalypsodeegree_impl.io.shpapi;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.commons.lang.NotImplementedException;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypso.gmlschema.property.IValuePropertyType;
import org.kalypso.gmlschema.property.relation.IRelationType;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.geometry.ByteUtils;
import org.kalypsodeegree.model.geometry.GM_Curve;
import org.kalypsodeegree.model.geometry.GM_Envelope;
import org.kalypsodeegree.model.geometry.GM_MultiCurve;
import org.kalypsodeegree.model.geometry.GM_MultiPoint;
import org.kalypsodeegree.model.geometry.GM_MultiSurface;
import org.kalypsodeegree.model.geometry.GM_Object;
import org.kalypsodeegree.model.geometry.GM_Point;
import org.kalypsodeegree.model.geometry.GM_Surface;
import org.kalypsodeegree.model.geometry.GM_SurfacePatch;
import org.kalypsodeegree_impl.io.rtree.HyperBoundingBox;
import org.kalypsodeegree_impl.io.rtree.HyperPoint;
import org.kalypsodeegree_impl.io.rtree.RTree;
import org.kalypsodeegree_impl.io.rtree.RTreeException;
import org.kalypsodeegree_impl.io.shpapi.dataprovider.IShapeDataProvider;
import org.kalypsodeegree_impl.model.geometry.GeometryFactory;
import org.kalypsodeegree_impl.tools.Debug;

/**
 * Class representing an ESRI Shape File.
 * <p>
 * This is a modification of the <tt>ShapeFile</tt> class within the shpapi package of sfcorba2java project performed by
 * the EXSE-Working group of of the geogr. institute of the university of Bonn
 * (http://www.giub.uni-bonn.de/exse/results/welcome.html).
 * <p>
 * ------------------------------------------------------------------------
 * </p>
 * 
 * @version 17.10.2001
 * @author Andreas Poth
 *         <p>
 */
public class ShapeFile
{
  public static final QName PROPERTY_FEATURE_MEMBER = new QName( DBaseFile.SHP_NAMESPACE_URI, "featureMember" ); //$NON-NLS-1$

  public static final String GEOM = "GEOM";

  private DBaseFile m_dbf = null;

  private final SHP2WKS shpwks = new SHP2WKS();

  /**
   * contains the dBase indexes
   */
  private final Hashtable<String, DBaseIndex> dBaseIndexes = new Hashtable<String, DBaseIndex>();

  /**
   * aggregated Instance-variables
   */
  private MainFile shp = null;

  private RTree rti = null;

  private String m_url = null;

  /*
   * indicates if a dBase-file is associated to the shape-file
   */
  private boolean hasDBaseFile = true;

  /*
   * indicates if an R-tree index is associated to the shape-file
   */
  private boolean hasRTreeIndex = true;

  /**
   * constructor: <BR>
   * Construct a ShapeFile from a file name. <BR>
   */
  public ShapeFile( final String url ) throws IOException
  {
    m_url = url;

    /*
     * initialize the MainFile
     */
    shp = new MainFile( url );

    /*
     * initialize the DBaseFile
     */
    try
    {
      m_dbf = new DBaseFile( url, shp.getFileShapeType() );
    }
    catch( final IOException e )
    {
      hasDBaseFile = false;

      e.printStackTrace();
    }

    /*
     * initialize the RTreeIndex
     */
    try
    {
      rti = new RTree( url + ".rti" );
    }
    catch( final RTreeException e )
    {
      hasRTreeIndex = false;
    }

    if( hasDBaseFile )
    {
      String[] s = null;

      try
      {
        s = getProperties();
      }
      catch( final Exception e )
      {
        e.printStackTrace();
      }

      for( final String element : s )
      {
        try
        {
          dBaseIndexes.put( element, new DBaseIndex( url + "$" + element ) );
        }
        catch( final IOException e )
        {
          // shouldnt we do something here?
        }
      }
    }
  }

  /**
   * constructor: <BR>
   * Construct a ShapeFile from a file name. <BR>
   */
  public ShapeFile( final String url, final String rwflag ) throws IOException
  {
    m_url = url;

    shp = new MainFile( url, rwflag );

    // TODO: initialize dbf, rti
    hasDBaseFile = false;
    hasRTreeIndex = false;
  }

  /**
   *
   */
  @SuppressWarnings("unchecked")
  public void close( ) throws IOException
  {
    shp.close();

    if( m_dbf != null )
    {
      m_dbf.close();
    }

    if( rti != null )
    {
      try
      {
        rti.close();
      }
      catch( final Exception e )
      {
        e.printStackTrace();
      }
    }

    for( final Enumeration e = dBaseIndexes.elements(); e.hasMoreElements(); )
    {
      final DBaseIndex index = (DBaseIndex) e.nextElement();

      index.close();
    }
  }

  /**
   * returns true if a column is indexed
   */
  public boolean hasDBaseIndex( final String column )
  {
    final DBaseIndex index = dBaseIndexes.get( column );
    return index != null;
  }

  /**
   * returns true if a dBase-file is associated to the shape-file <BR>
   */
  public boolean hasDBaseFile( )
  {
    return hasDBaseFile;
  }

  /**
   * returns true if an R-tree index is associated to the shape-file <BR>
   */
  public boolean hasRTreeIndex( )
  {
    return hasRTreeIndex;
  }

  /**
   * returns the number of records within a shape-file <BR>
   */
  public int getRecordNum( )
  {
    return shp.getRecordNum();
  }

  /**
   * returns the minimum bounding rectangle of all geometries <BR>
   * within the shape-file
   */
  public GM_Envelope getFileMBR( )
  {
    final double xmin = shp.getFileMBR().west;
    final double xmax = shp.getFileMBR().east;
    final double ymin = shp.getFileMBR().south;
    final double ymax = shp.getFileMBR().north;

    return GeometryFactory.createGM_Envelope( xmin, ymin, xmax, ymax, null );
  }

  /**
   * returns the minimum bound rectangle of RecNo'th Geometrie <BR>
   */
  public GM_Envelope getMBRByRecNo( final int recNo ) throws IOException
  {
    final SHPEnvelope shpenv = shp.getRecordMBR( recNo );
    final double xmin = shpenv.west;
    final double xmax = shpenv.east;
    final double ymin = shpenv.south;
    final double ymax = shpenv.north;

    return GeometryFactory.createGM_Envelope( xmin, ymin, xmax, ymax, null );
  }

  /**
   * Same as {@link #getFeatureByRecNo(int, boolean) getFeatureByRecNo(int, true)}
   */
  public Feature getFeatureByRecNo( final Feature parent, final IRelationType parentRelation, final int RecNo ) throws IOException, HasNoDBaseFileException, DBaseException
  {
    return getFeatureByRecNo( parent, parentRelation, RecNo, false );
  }

  /**
   * returns the RecNo'th entry of the shape file as Feature. This contains the geometry as well as the attributes
   * stored into the dbase file.
   * 
   * @param allowNull
   *          if true, everything wich cannot parsed gets 'null' instaed of ""
   */
  public Feature getFeatureByRecNo( final Feature parent, final IRelationType parentRelation, final int RecNo, final boolean allowNull ) throws IOException, HasNoDBaseFileException, DBaseException
  {
    if( !hasDBaseFile )
      throw new HasNoDBaseFileException( "Exception: there is no dBase-file " + "associated to this shape-file" );

    final Feature feature = m_dbf.getFRow( parent, parentRelation, RecNo, allowNull );
    final GM_Object geo = getGM_ObjectByRecNo( RecNo );
    final IFeatureType featureType = feature.getFeatureType();
    final IPropertyType pt = featureType.getProperty( new QName( featureType.getQName().getNamespaceURI(), GEOM ) );
    feature.setProperty( pt, geo );

    return feature;
  }

  /**
   * returns RecNo'th Geometrie <BR>
   */
  @SuppressWarnings("unchecked")
  public GM_Object getGM_ObjectByRecNo( final int RecNo ) throws IOException
  {
    final ISHPGeometry shpGeom = shp.getByRecNo( RecNo );
    if( shpGeom == null )
      return null;

    if( shpGeom instanceof SHPPoint )
      return shpwks.transformPoint( null, (SHPPoint) shpGeom );

    if( shpGeom instanceof SHPMultiPoint )
    {
      final GM_Point[] points = shpwks.transformMultiPoint( null, (SHPMultiPoint) shpGeom );
      if( points == null )
        return null;

      return GeometryFactory.createGM_MultiPoint( points );
    }
    
    if( shpGeom instanceof SHPPolyLine )
    {
      final GM_Curve[] curves = shpwks.transformPolyLine( null, (SHPPolyLine) shpGeom );
      if( curves == null )
        return null;

      return GeometryFactory.createGM_MultiCurve( curves );
    }
    
    if( shpGeom instanceof SHPPolygon )
    {
      final GM_Surface[] polygons = shpwks.transformPolygon( null, (SHPPolygon) shpGeom );
      if( polygons == null || polygons.length <= 0 )
        return null;

      return GeometryFactory.createGM_MultiSurface( polygons, null );
    }
    
    if( shpGeom instanceof SHPPointz )
      return shpwks.transformPointz( null, (SHPPointz) shpGeom );

    if( shpGeom instanceof SHPPolyLinez )
    {
      final GM_Curve[] curves = shpwks.transformPolyLinez( null, (SHPPolyLinez) shpGeom );
      if( curves == null )
        return null;

      return GeometryFactory.createGM_MultiCurve( curves );
    }
    
    if( shpGeom instanceof SHPPolygonz )
    {
      final GM_Surface[] polygonsz = shpwks.transformPolygonz( null, (SHPPolygonz) shpGeom );
      if( polygonsz != null )
        return GeometryFactory.createGM_MultiSurface( polygonsz, null );

      return null;
    }
    
    throw new NotImplementedException( "Unknown shpe class: " + shpGeom );
  }

  /**
   * returns a int array that contains all the record numbers that matches the search operation
   */
  @SuppressWarnings("unchecked")
  public int[] getGeoNumbersByAttribute( final String column, final Comparable value ) throws IOException, DBaseIndexException
  {
    final DBaseIndex index = dBaseIndexes.get( column );

    if( index == null )
      return null;

    return index.search( value );
  }

  /**
   * is a property unique?
   */
  public boolean isUnique( final String property )
  {
    final DBaseIndex index = dBaseIndexes.get( property );

    if( index == null )
      return false;

    return index.isUnique();
  }

  /**
   * returns the properties (column headers) of the dBase-file <BR>
   * associated to the shape-file <BR>
   */
  public String[] getProperties( ) throws HasNoDBaseFileException, DBaseException
  {
    if( !hasDBaseFile )
      throw new HasNoDBaseFileException( "Exception: there is no dBase-file " + "associated to this shape-file" );

    return m_dbf.getProperties();
  }

  /**
   * returns the datatype of each column of the database file <BR>
   * associated to the shape-file <BR>
   */
  public String[] getDataTypes( ) throws HasNoDBaseFileException, DBaseException
  {
    if( !hasDBaseFile )
      throw new HasNoDBaseFileException( "Exception: there is no dBase-file " + "associated to this shape-file" );

    return m_dbf.getDataTypes();
  }

  /**
   * @throws HasNoDBaseFileException
   * @throws DBaseException
   */
  public int[] getDataLengths( ) throws HasNoDBaseFileException, DBaseException
  {
    final String[] properties = getProperties();
    final int[] retval = new int[properties.length];

    for( int i = 0; i < properties.length; i++ )
    {
      retval[i] = m_dbf.getDataLength( properties[i] );
    }

    return retval;
  }

  /**
   * returns the datatype of each column of the dBase associated <BR>
   * to the shape-file specified by fields <BR>
   */
  public String[] getDataTypes( final String[] fields ) throws HasNoDBaseFileException, DBaseException
  {
    if( !hasDBaseFile )
      throw new HasNoDBaseFileException( "Exception: there is no dBase-file " + "associated to this shape-file" );

    return m_dbf.getDataTypes( fields );
  }

  /**
   * returns a row of the dBase-file <BR>
   * associated to the shape-file <BR>
   */
  public Object[] getRow( final int rowNo ) throws HasNoDBaseFileException, DBaseException
  {
    if( !hasDBaseFile )
      throw new HasNoDBaseFileException( "Exception: there is no dBase-file " + "associated to this shape-file" );

    return m_dbf.getRow( rowNo );
  }

  /**
   */
  @SuppressWarnings("unchecked")
  private void initDBaseFile( final IFeatureType featT ) throws DBaseException
  {
    // count regular fields
    final IPropertyType[] ftp = featT.getProperties();

    // get properties names and types and create a FieldDescriptor
    // for each properties except the geometry-property
    final List<FieldDescriptor> fieldList = new ArrayList<FieldDescriptor>();
    for( int i = 0; i < ftp.length; i++ )
    {
      final String ftpName = ftp[i].getQName().getLocalPart();
      int pos = ftpName.lastIndexOf( '.' );
      if( pos < 0 )
      {
        pos = -1;
      }
      final String s = ftpName.substring( pos + 1 );
      if( !(ftp[i] instanceof IValuePropertyType) )
      {// TODO: this seems to be a bug;
      }
      final IValuePropertyType vpt = (IValuePropertyType) ftp[i];
      final Class clazz = vpt.getValueClass();
      if( clazz == Integer.class )
      {
        fieldList.add( new FieldDescriptor( s, "N", (byte) 20, (byte) 0 ) );
      }
      else if( clazz == Byte.class )
      {
        fieldList.add( new FieldDescriptor( s, "N", (byte) 4, (byte) 0 ) );
      }
      else if( clazz == Character.class )
      {
        fieldList.add( new FieldDescriptor( s, "C", (byte) 1, (byte) 0 ) );
      }
      else if( clazz == Float.class )
      {
        // TODO: Problem: reading/writing a shape will change the precision/size of the column!
        fieldList.add( new FieldDescriptor( s, "N", (byte) 30, (byte) 10 ) );
      }
      else if( (clazz == Double.class) || (clazz == Number.class) )
      {
        fieldList.add( new FieldDescriptor( s, "N", (byte) 30, (byte) 10 ) );
      }
      else if( clazz == BigDecimal.class )
      {
        fieldList.add( new FieldDescriptor( s, "N", (byte) 30, (byte) 10 ) );
      }
      else if( clazz == String.class )
      {
        fieldList.add( new FieldDescriptor( s, "C", (byte) 127, (byte) 0 ) );
      }
      else if( clazz == Date.class )
      {
        fieldList.add( new FieldDescriptor( s, "D", (byte) 12, (byte) 0 ) );
      }
      else if( clazz == Long.class || clazz == BigInteger.class )
      {
        fieldList.add( new FieldDescriptor( s, "N", (byte) 30, (byte) 0 ) );
      }
      else if( clazz == Boolean.class )
      {
        fieldList.add( new FieldDescriptor( s, "L", (byte) 1, (byte) 0 ) );
      }
      else
      {
        // System.out.println("no db-type:" + ftp[i].getType());
      }
    }

    // allocate memory for fielddescriptors
    final FieldDescriptor[] fieldDesc = fieldList.toArray( new FieldDescriptor[fieldList.size()] );
    m_dbf = new DBaseFile( m_url, fieldDesc );
  }

  @SuppressWarnings("unchecked")
  public void writeShape( final IShapeDataProvider dataProvider ) throws Exception
  {
    Debug.debugMethodBegin( this, "writeShape" );

    final int featuresLength = dataProvider.getFeaturesLength();

    if( featuresLength == 0 )
      throw new Exception( "Can't write an empty shape." );

    // mbr of the whole shape file
    SHPEnvelope shpmbr = new SHPEnvelope();

    // Set the Offset to the end of the fileHeader
    int offset = ShapeConst.SHAPE_FILE_HEADER_LENGTH;

    // ====================DBASE TABLE =====================

    /* initialize the dbasefile associated with the shape file */
    final IFeatureType featureType = dataProvider.getFeatureType();
    initDBaseFile( featureType );

    /* loop through the Geometries of the feature collection and write them to a bytearray */
    final IPropertyType[] ftp = featureType.getProperties();

    /* loop over all features */
    for( int i = 0; i < featuresLength; i++ )
    {
      // write i'th feature properties to a ArrayList
      final ArrayList<Object> vec = new ArrayList<Object>();
      for( int j = 0; j < ftp.length; j++ )
      {
        /* get the property of the current feature */
        // final Object value = feature.getProperty( ftp[j] );
        final Object value = dataProvider.getFeatureProperty( i, ftp[j] );
        if( !(ftp[j] instanceof IValuePropertyType) )
        {
          continue;
        }
        final IValuePropertyType ivp = (IValuePropertyType) ftp[j];

        final Class clazz = ivp.getValueClass();
        if( (clazz == Integer.class) || (clazz == Byte.class) || (clazz == Character.class) || (clazz == Float.class) || (clazz == Double.class) || (clazz == Number.class) || (clazz == Date.class)
            || (clazz == Long.class) || (clazz == String.class) || (clazz == Boolean.class) )
        {
          vec.add( value );
        }
        else if( clazz == BigDecimal.class )
        {
          if( value != null )
          {
            vec.add( new Double( ((java.math.BigDecimal) value).doubleValue() ) );
          }
          else
          {
            vec.add( null );
          }
        }
        else if( clazz == BigInteger.class )
        {
          if( value != null )
          {
            vec.add( new Long( ((BigInteger) value).longValue() ) );
          }
          else
          {
            vec.add( null );
          }
        }
      }

      // write the ArrayList (properties) to the dbase file
      try
      {
        m_dbf.setRecord( vec );
      }
      catch( final DBaseException db )
      {
        db.printStackTrace();
        throw new Exception( db.toString(), db );
      }

      // ==================== SHAPE ENTRIES =====================

      /* create a new SHP type entry in the specified shape type */
      /* convert feature geometry into output geometry */
      ISHPGeometry shpGeom = getShapeGeometry( dataProvider.getGeometry( i ), dataProvider.getOutputShapeConstant() );
// if( shpGeom == null )
// throw new UnsupportedOperationException( "Data type (" + dataProvider.getGeometry( i ).toString() + ") cannot
// converted into the specified shape type ("
// + ShapeConst.getShapeConstantAsString( dataProvider.getOutputShapeConstant() ) + ") or geometry is null." );

      byte[] byteArray = null;
      if( shpGeom != null )
      {
        byteArray = shpGeom.writeShape();
      }

      /* check for null geometry */
      if( byteArray == null )
      {
        shpGeom = new SHPNullShape();
        byteArray = shpGeom.writeShape();
      }

      final SHPEnvelope mbr = shpGeom.getEnvelope();

      final int nbyte = shpGeom.size();
      if( i == 0 || (i > 0 && shpmbr == null) )
      {
        shpmbr = mbr;
      }

      // write bytearray to the shape file
      final IndexRecord record = new IndexRecord( offset / 2, nbyte / 2 );

      // write recordheader to the bytearray
      ByteUtils.writeBEInt( byteArray, 0, i );
      ByteUtils.writeBEInt( byteArray, 4, nbyte / 2 );

      // write record (bytearray) including recordheader to the shape file
      shp.write( byteArray, record, mbr );

      // icrement offset for pointing at the end of the file
      offset += (nbyte + ShapeConst.SHAPE_FILE_RECORD_HEADER_LENGTH);

      // actualize shape file minimum boundary rectangle
      if( mbr != null )
      {
        if( mbr.west < shpmbr.west )
        {
          shpmbr.west = mbr.west;
        }

        if( mbr.east > shpmbr.east )
        {
          shpmbr.east = mbr.east;
        }

        if( mbr.south < shpmbr.south )
        {
          shpmbr.south = mbr.south;
        }

        if( mbr.north > shpmbr.north )
        {
          shpmbr.north = mbr.north;
        }
      }
    }

    m_dbf.writeAllToFile();

    // Header schreiben
    shp.writeHeader( offset, dataProvider.getOutputShapeConstant(), shpmbr );

    Debug.debugMethodEnd();

  }

  private ISHPGeometry getShapeGeometry( final GM_Object geom, final byte outputShapeConstant )
  {
    if( geom == null )
      return null;

    switch( outputShapeConstant )
    {
      case ShapeConst.SHAPE_TYPE_NULL:
      {
        // do nothing
      }
      case ShapeConst.SHAPE_TYPE_POINT:
      {
        final GM_Point point = (GM_Point) geom.getAdapter( GM_Point.class );
        if( point == null )
          return null;
        else
          return new SHPPoint( point );
      }
      case ShapeConst.SHAPE_TYPE_POLYLINE:
      {
        final GM_Curve[] curves = (GM_Curve[]) geom.getAdapter( GM_Curve[].class );
        if( curves == null )
          return null;
        else
          return new SHPPolyLine( curves );
      }
      case ShapeConst.SHAPE_TYPE_POLYGON:
      {
        final GM_SurfacePatch[] surfacePatches = (GM_SurfacePatch[]) geom.getAdapter( GM_SurfacePatch[].class );
        if( surfacePatches == null )
          return null;
        else
          return new SHPPolygon( surfacePatches );
      }
      case ShapeConst.SHAPE_TYPE_POINTZ:
      {
        final GM_Point point = (GM_Point) geom.getAdapter( GM_Point.class );
        if( point == null )
          return null;
        else
          return new SHPPointz( point );
      }
      case ShapeConst.SHAPE_TYPE_POLYLINEZ:
      {
        final GM_Curve[] curves = (GM_Curve[]) geom.getAdapter( GM_Curve[].class );
        if( curves == null )
          return null;
        else
          return new SHPPolyLinez( curves );
      }
      case ShapeConst.SHAPE_TYPE_POLYGONZ:
      {
        final GM_SurfacePatch[] surfacePatches = (GM_SurfacePatch[]) geom.getAdapter( GM_SurfacePatch[].class );
        if( surfacePatches == null )
          return null;
        else
          return new SHPPolygonz( surfacePatches );
      }
    }
    return null;
  }

  public IFeatureType getFeatureType( )
  {
    return m_dbf.getFeatureType();
  }

  public int getFileShapeType( )
  {
    return shp.getFileShapeType();
  }

}