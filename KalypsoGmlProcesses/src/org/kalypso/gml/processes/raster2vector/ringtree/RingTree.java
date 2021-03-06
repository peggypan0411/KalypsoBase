package org.kalypso.gml.processes.raster2vector.ringtree;

import java.util.ArrayList;
import java.util.Collection;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * @author belger
 */
public class RingTree
{
  public final RingTreeElement root = new RingTreeElement( null, null, Double.NaN, null, "root" ); //$NON-NLS-1$

  public Object walk( final RingTreeWalker rtw )
  {
    // f�r jedes child was machen
    walkInternal( rtw, root );

    return rtw.getResult();
  }

  private void walkInternal( final RingTreeWalker rtw, final RingTreeElement element )
  {
    if( element != root )
      rtw.operate( element );

    for( final RingTreeElement child : element.children() )
      walkInternal( rtw, child );
  }

  public void insertElement( final RingTreeElement rte )
  {
    // rekursiv durch den Baum suchen und das Element an der richtigen Stelle einbauen
    insertInternal( root, rte );
  }

  private void insertInternal( final RingTreeElement target, final RingTreeElement element )
  {
    // f�r jedes Kind checken, ob element drin liegt, falls ja dort einf�gen
    final Coordinate c = element.innerCrd;

    if( c == null )
    {
      final double area = element.polygon.getArea();
      if( area == 0 )
      {
        System.out.println( "Fl�che ist 0.0" ); //$NON-NLS-1$
      }
      return;
    }

    for( final RingTreeElement child : target.children() )
    {
      if( child.pir.isInside( c ) && child.contains( element ) )
      {
        if( element.contains( child ) )
        {
          // child and element have the same geometry

          final double elementValue = element.lineValue;
          final double childValue = child.lineValue;
          // Keep the one with the bigger value: this makes sense, as segments are generated
          // for all borders with values smaller then the actual cell value for edges with Double.NaN
          if( elementValue > childValue )
          {
            // keep element: get children from child and replace child with element
            for( final RingTreeElement childChild : child.children() )
              element.addChild( childChild );

            target.addChild( element );
            target.removeChild( child );
          }
          else
          {
            // keep child: nothing to do, just ignore element
          }
        }
        else
        {
          insertInternal( child, element );
        }

        return;
      }
    }

    // wenns nirgends reinpasst schaun, welche children in das element geh�ren
    final Collection<RingTreeElement> removeElements = new ArrayList<>();
    for( final RingTreeElement child : target.children() )
    {
      if( element.pir.isInside( child.innerCrd ) && element.contains( child ) )
      {
        removeElements.add( child );
        element.addChild( child );
      }
    }

    // und diese elemente l�schen
    for( final RingTreeElement removeChild : removeElements )
      target.removeChild( removeChild );

    // und das element selbst hinzuf�gen
    target.addChild( element );
  }

}
