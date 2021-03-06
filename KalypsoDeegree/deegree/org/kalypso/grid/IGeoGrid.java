package org.kalypso.grid;

import java.math.BigDecimal;

import org.kalypsodeegree.model.elevation.IElevationModel;
import org.kalypsodeegree.model.geometry.GM_Polygon;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Implementations of this interface represent georeferences grids.
 * <p>
 * Each cell of the grid has a georeferences position as well as a double-value.
 * </p>
 * 
 * @author Gernot Belger
 */
public interface IGeoGrid extends IGeoValueProvider, AutoCloseable, IElevationModel
{
  /**
   * Size of the raster. Amount of raster-cells in direction of x.
   */
  int getSizeX( ) throws GeoGridException;

  /**
   * Size of the raster. Amount of raster-cells in direction of y.
   */
  int getSizeY( ) throws GeoGridException;

  /**
   * Returns the value of the raster at (cell-)coordinates x-y. Does not check, if x or y lie within the raster bounds.<br/>
   * The behavior is undefined, if x-y lie out of bounds. (May throw an exception or return random numbers).
   * 
   * @param x
   *          The (cell-)coordinate x.
   * @param y
   *          The (cell-)coordinate y.
   * @return The value.
   */
  double getValue( final int x, final int y ) throws GeoGridException;

  /**
   * Similar to {@link #getValue(int, int, String)} but checks if the given cell coordinates lie in the raster. If not,
   * return {@link Double#NaN}.
   */
  double getValueChecked( final int x, final int y ) throws GeoGridException;

  /**
   * This function returns the surface of this grid.
   * 
   * @param targetCRS
   *          The coordinate system will be used to transform the surface, after it was created and before it is
   *          returned.
   * @return The surface of the grid.
   */
  GM_Polygon getSurface( final String targetCRS ) throws GeoGridException;

  /**
   * This function returns the cell at the given (cell-)coordinates. We interpret the grid cell as a surface with the
   * grid point as center point of the cell.
   * 
   * @param x
   *          The (cell-)coordinate x.
   * @param y
   *          The (cell-)coordinate y.
   * @param targetCRS
   *          The coordinate system will be used to transform the cell, after it was created and before it is returned.
   * @return The cell.
   */
  GM_Polygon getCell( final int x, final int y, final String targetCRS ) throws GeoGridException;

  /**
   * The envelope of this grid.<br>
   * <br>
   * HINT: It will not be transformed. If you need it transformed, you have to do it afterwards by yourself.
   * 
   * @return The envelope of this grid.
   */
  Envelope getEnvelope( ) throws GeoGridException;

  /**
   * This function returns the origin point of this grid.<br>
   * <br>
   * HINT: It will not be transformed to any coordinate system, so it will be in the source coordinate system of this
   * grid. If you need it transformed, you have to do it afterwards by yourself. The reason for this is, all operations,
   * like calculating a cell or determining the surface of the grid need an untransformed origin to do it, because their
   * results will be transformed then.
   * 
   * @return The origin of this grid.
   */
  Coordinate getOrigin( ) throws GeoGridException;

  /**
   * Offset from the origin in direction of x. Add (as vector) to origin to to walk along the raster alon the x-Axis.
   */
  Coordinate getOffsetX( ) throws GeoGridException;

  /**
   * Offset from the origin in direction of y. Add (as vector) to origin to to walk along the raster alon the y-Axis.
   */
  Coordinate getOffsetY( ) throws GeoGridException;

  /**
   * This function returns the source coordinate system of this grid.
   * 
   * @return The source coordinate system.
   */
  String getSourceCRS( ) throws GeoGridException;

  /**
   * Returns a walking strategy suitable to iterate through this grid.
   */
  IGeoWalkingStrategy getWalkingStrategy( ) throws GeoGridException;

  BigDecimal getMin( ) throws GeoGridException;

  BigDecimal getMax( ) throws GeoGridException;
}