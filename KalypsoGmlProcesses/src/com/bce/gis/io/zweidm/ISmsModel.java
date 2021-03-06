package com.bce.gis.io.zweidm;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * The handler interface for RMA�Kalypso model element. Class implementing this interface can be set to handle parsing
 * events from a {@link IRMA10SModelReader}
 *
 * @author Thomas Jung
 */
public interface ISmsModel
{
  /**
   * Invoked by the reader to signal that an node has been read and parsed
   *
   * @param the
   *          complete line read
   * @param id
   *          the id of the node
   * @param the
   *          easting coordinate of the node
   * @param the
   *          northing coordinate of the node
   * @param the
   *          elevation of the node
   */
  void addNode( String lineString, int id, double easting, double northing, double elevation );

  /**
   * Invoked by the reader to signal that an element has been read and parsed
   */
  void addElement( String lineString, int id, Integer[] nodeIds, int rougthnessClassID );

  Coordinate getNode( Integer nodeId );

  /**
   * Returns a (unmodifiable) list of all elements of this model.
   */
  List<SmsElement> getElementList( );

  /**
   * Returns the coordinate systems all elements and nodes of this model are in.
   */
  int getSrid( );

  GeometryFactory getGeometryFactory( );
}