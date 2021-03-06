/*
 * Created on 22.04.2003
 */
package org.kalypso.commons.math.geom;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import org.kalypso.commons.internal.i18n.Messages;
import org.kalypso.commons.math.LinearEquation;
import org.kalypso.commons.math.LinearEquation.SameXValuesException;

/**
 * Trapeze.
 * 
 * @author schlienger
 */
public class Trapeze
{
  private final Point2D m_p11;

  private final Point2D m_p12;

  private final Point2D m_p21;

  private final Point2D m_p22;

  /**
   * Constructs a trapeze with two parallel lines.
   * 
   * @param L1
   * @param L2
   * @see setLines(Line2D L1, Line2D L2)
   */
  public Trapeze( final Line2D L1, final Line2D L2 )
  {
    m_p11 = L1.getP1();
    m_p12 = L1.getP2();
    m_p21 = L2.getP1();
    m_p22 = L2.getP2();
  }

  /**
   * Points p11 and p12 must represent a line L1. Points p21 and p22 must represent a line L2. L1 must be parallel to
   * L2.
   * 
   * @param p11
   * @param p12
   * @param p21
   * @param p22
   * @see setPoints(Point2D p11, Point2D p12, Point2D p21, Point2D p22)
   */
  public Trapeze( final Point2D p11, final Point2D p12, final Point2D p21, final Point2D p22 )
  {
    m_p11 = p11;
    m_p12 = p12;
    m_p21 = p21;
    m_p22 = p22;
  }

  /**
   * Returns the height ofthe trapeze.
   * 
   * @return height of the trapeze, that is the distance between the two bases.
   * @throws IllegalStateException
   *           if points of trapeze are null
   */
  public double height( )
  {
    if( m_p11 == null || m_p21 == null )
      throw new IllegalStateException( Messages.getString( "org.kalypso.commons.math.geom.Trapeze.0" ) ); //$NON-NLS-1$

    if( m_p11.getX() == m_p12.getX() )
      return Math.abs( m_p11.getX() - m_p21.getX() );
    try
    {
      final LinearEquation eq = new LinearEquation( m_p11, m_p12 );

      return eq.distance( m_p21 );
    }
    catch( final SameXValuesException e )
    {
      // sollte nicht der fall sein weil wir der test explizit vorher machen
      return 0;
    }
  }

  /**
   * @return area of the trapeze, according to the formula: A = (b + B) H / 2
   * @throws IllegalStateException
   *           -
   */
  public double area( )
  {
    if( m_p11 == null || m_p12 == null || m_p21 == null || m_p22 == null )
      throw new IllegalStateException( Messages.getString( "org.kalypso.commons.math.geom.Trapeze.1" ) ); //$NON-NLS-1$

    final double d1 = PointUtilities.distance( m_p11, m_p12 );
    final double d2 = PointUtilities.distance( m_p21, m_p22 );
    final double h = height();

    return (d1 + d2) * h / 2;
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString( )
  {
    final StringBuffer buf = new StringBuffer();

    buf.append( m_p11 + "|" + m_p12 + "\n" ); //$NON-NLS-1$ //$NON-NLS-2$
    buf.append( "-------------------------------------------------\n" ); //$NON-NLS-1$
    buf.append( m_p21 + "|" + m_p22 + "\n" ); //$NON-NLS-1$ //$NON-NLS-2$

    return buf.toString();
  }
}
