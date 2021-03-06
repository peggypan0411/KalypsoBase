package org.kalypso.grid;

import java.math.BigDecimal;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * A {@link IGeoGrid} implementation based on an arbitrary {@link IGeoValueProvider}.
 * <p>
 * The size of the grid is given, the value are taken from the provider.
 * </p>
 * 
 * @author Gernot Belger
 */
public class DoubleFakeRaster extends AbstractGeoGrid
{
  private final IGeoValueProvider m_doubleProvider;

  private final int m_sizeX;

  private final int m_sizeY;

  public DoubleFakeRaster( final int sizeX, final int sizeY, final Coordinate origin, final Coordinate offsetX, final Coordinate offsetY, final IGeoValueProvider dp, final String sourceCRS )
  {
    super( origin, offsetX, offsetY, sourceCRS );

    m_sizeX = sizeX;
    m_sizeY = sizeY;
    m_doubleProvider = dp;
  }

  @Override
  public final double getValue( final int x, final int y ) throws GeoGridException
  {
    final Coordinate c = GeoGridUtilities.toCoordinate( this, x, y, null );
    return m_doubleProvider.getValue( c );
  }

  @Override
  public int getSizeX( )
  {
    return m_sizeX;
  }

  @Override
  public int getSizeY( )
  {
    return m_sizeY;
  }

  @Override
  public BigDecimal getMax( )
  {
    return null;
  }

  @Override
  public BigDecimal getMin( )
  {
    return null;
  }
}