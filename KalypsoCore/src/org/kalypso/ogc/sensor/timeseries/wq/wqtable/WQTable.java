package org.kalypso.ogc.sensor.timeseries.wq.wqtable;

import java.text.DateFormat;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import org.kalypso.commons.math.LinearEquation;
import org.kalypso.commons.math.LinearEquation.SameXValuesException;
import org.kalypso.core.i18n.Messages;
import org.kalypso.ogc.sensor.timeseries.wq.WQException;

/**
 * WQTable
 *
 * @author schlienger
 */
public class WQTable
{
  private final TreeSet<WQPair> m_qSortedPairs;

  private final TreeSet<WQPair> m_wSortedPairs;

  private final static WQException CANNOT_INTERPOLATE_EXCEPTION = new WQException( Messages.getString("org.kalypso.ogc.sensor.timeseries.wq.wqtable.WQTable.0") ); //$NON-NLS-1$

  private final LinearEquation EQ = new LinearEquation();

  private final Date m_validity;

  private int m_offset;

  /**
   * Creates a WQTable with a default offset of 0
   *
   * @param validity
   *          date up from which this table is valid
   */
  public WQTable( final Date validity, final double[][] table )
  {
    this( validity, 0, table );
  }

  /**
   * Creates a WQTable
   *
   * @param validity
   *          date up from which this table is valid
   * @param offset
   *          offset used for W, before conversion W = W + offset
   */
  public WQTable( final Date validity, final int offset, final double[][] table )
  {
    this( validity, offset, WQPair.convert2pairs( table ) );
  }

  /**
   * Creates a WQTable with a default offset of 0
   *
   * @param validity
   *          date up from which this table is valid
   */
  public WQTable( final Date validity, final double[] W, final double[] Q )
  {
    this( validity, 0, W, Q );
  }

  /**
   * Creates a WQTable
   *
   * @param validity
   *          date up from which this table is valid
   * @param offset
   *          offset used for W, before conversion W = W + offset
   */
  public WQTable( final Date validity, final int offset, final double[] W, final double[] Q )
  {
    this( validity, offset, WQPair.convert2pairs( W, Q ) );
  }

  /**
   * Creates a WQTable with a default offset of 0
   *
   * @param validity
   *          date up from which this table is valid
   */
  public WQTable( final Date validity, final Number[] W, final Number[] Q )
  {
    this( validity, 0, W, Q );
  }

  /**
   * Creates a WQTable
   *
   * @param validity
   *          date up from which this table is valid
   * @param offset
   *          offset used for W, before conversion W = W + offset
   */
  public WQTable( final Date validity, final int offset, final Number[] W, final Number[] Q )
  {
    this( validity, offset, WQPair.convert2pairs( W, Q ) );
  }

  /**
   * Creates a WQTable
   *
   * @param validity
   *          date up from which this table is valid
   * @param offset
   *          offset used for W, before conversion W = W + offset
   */
  public WQTable( final Date validity, final int offset, final WQPair[] wqpairs )
  {
    m_validity = validity;
    m_offset = offset;

    m_qSortedPairs = new TreeSet<WQPair>( WQPairComparator.Q_COMPARATOR );
    m_wSortedPairs = new TreeSet<WQPair>( WQPairComparator.W_COMPARATOR );

    for( final WQPair wqpair : wqpairs )
    {
      m_qSortedPairs.add( wqpair );
      m_wSortedPairs.add( wqpair );
    }
  }

  public double getWFor( final double q ) throws WQException
  {
    final WQPair p = new WQPair( 0, q );
    final SortedSet<WQPair> headSet = m_qSortedPairs.headSet( p );
    final SortedSet<WQPair> tailSet = m_qSortedPairs.tailSet( p );

    if( headSet.isEmpty() || tailSet.isEmpty() )
      throw CANNOT_INTERPOLATE_EXCEPTION; // should exception be thrown or a value returned?

    final WQPair p1 = headSet.last();
    final WQPair p2 = tailSet.first();

    try
    {
      EQ.setPoints( p1.getW(), p1.getQ(), p2.getW(), p2.getQ() );
    }
    catch( final SameXValuesException e )
    {
      throw new WQException( Messages.getString("org.kalypso.ogc.sensor.timeseries.wq.wqtable.WQTable.1") + q, e ); //$NON-NLS-1$
    }

    return EQ.computeX( q );
  }

  public double getQFor( final double w ) throws WQException
  {
    final WQPair p = new WQPair( w, 0 );
    final SortedSet<WQPair> headSet = m_wSortedPairs.headSet( p );
    final SortedSet<WQPair> tailSet = m_wSortedPairs.tailSet( p );

    if( headSet.isEmpty() || tailSet.isEmpty() )
      throw CANNOT_INTERPOLATE_EXCEPTION; // should exception be thrown or a value returned?

    final WQPair p1 = headSet.last();
    final WQPair p2 = tailSet.first();

    try
    {
      EQ.setPoints( p1.getW(), p1.getQ(), p2.getW(), p2.getQ() );
    }
    catch( final SameXValuesException e )
    {
      throw new WQException( Messages.getString("org.kalypso.ogc.sensor.timeseries.wq.wqtable.WQTable.2") + w, e ); //$NON-NLS-1$
    }

    return EQ.computeY( w );
  }

  public Date getValidity()
  {
    return m_validity;
  }

  public int getOffset()
  {
    return m_offset;
  }

  public void setOffset( final int offset )
  {
    m_offset = offset;
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    final StringBuffer sb = new StringBuffer();

    final DateFormat df = DateFormat.getDateTimeInstance();
    sb.append( Messages.getString("org.kalypso.ogc.sensor.timeseries.wq.wqtable.WQTable.3") ).append( df.format( m_validity ) ).append( Messages.getString("org.kalypso.ogc.sensor.timeseries.wq.wqtable.WQTable.4") ).append( m_offset );//.append( "\n" ).append( //$NON-NLS-1$ //$NON-NLS-2$
    //    m_wSortedPairs ).append( "\n" );

    return sb.toString();
  }

  public WQPair[] getPairs()
  {
    return m_wSortedPairs.toArray( new WQPair[m_wSortedPairs.size()] );
  }
}
