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
 * interface-compatibility to deegree is wanted but not retained always. 
 * 
 * If you intend to use this software in other ways than in kalypso 
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
package org.kalypsodeegree_impl.io.rtree;

/**
 * Abstracte Klasse AbstractNode von der LeafNode und NoneLeafNode erben. Definiert und implementiert gemeinsame Felder
 * und Operationen Definiert abstrakte Methoden.
 * 
 * @version 1.0
 * @author Wolfgang B�r
 */
public abstract class Node
{
  protected transient PageFile file;

  protected HyperBoundingBox unionMinBB;

  protected HyperBoundingBox[] hyperBBs;

  protected int counter;

  protected int pageNumber;

  protected int parentNode;

  protected int place;

  /**
   * Konstruktor der Klasse AbstractNode.
   * 
   * @param pageNumber
   *          PageNumber des Knotens
   * @param pageFile
   *          PageFile zum Knoten geh�rende PageFile
   */
  public Node( final int pageNumber, final PageFile pageFile )
  {
    file = pageFile;
    this.pageNumber = pageNumber;
    parentNode = 0;
    hyperBBs = new HyperBoundingBox[file.getCapacity()];

    for( int i = 0; i < file.getCapacity(); i++ )
      hyperBBs[i] = HyperBoundingBox.getNullHyperBoundingBox( file.getDimension() );

    unionMinBB = HyperBoundingBox.getNullHyperBoundingBox( file.getDimension() );
    counter = 0;
  }

  /**
   * F�gt Daten in AbstractNode ein.
   * 
   * @param obj
   *          einzuf�gendes Objekt (Typ Integer oder AbstractNode)
   * @param box
   *          BoundingBox des Objektes
   */
  public abstract void insertData( Object obj, HyperBoundingBox box );

  /**
   * L�scht Eintrag index aus dem Knoten
   * 
   * @param index
   *          des Eintrages
   */
  public abstract void deleteData( int index );

  /**
   * Holt Daten aus AbstractNode.
   * 
   * @param index
   *          des Eintrages
   */
  public abstract Object getData( int index );

  /**
   * Gibt den Vater-Knoten zur�ck.
   * 
   * @return AbstractNode Vater des aktuellen Knotens.
   */
  public Node getParent( )
  {
    Node node = null;

    try
    {
      node = file.readNode( parentNode );
    }
    catch( final PageFileException e )
    {
      System.out.println( "PageFileException: AbstractNode.getParent() - readNode" );
    }

    return node;
  }

  /**
   * Gibt die PageFile-Nummer zur�ck, wo Knoten gespeichert ist.
   * 
   * @return int Page-Nummer
   */
  public int getPageNumber( )
  {
    return pageNumber;
  }

  /**
   * Setzt die PageFile-Nummer, wo Knoten gespeichert.
   * 
   * @param number
   *          Page-Nummer
   */
  public void setPageNumber( final int number )
  {
    pageNumber = number;
  }

  /**
   * Derzeit belegter Platz im Knoten.
   * 
   * @return int belegter Platz im Knoten
   */
  public int getUsedSpace( )
  {
    return counter;
  }

  /**
   * Gemeinsame HyperBoundingBox �ber alle Eintr�ge im Knoten.
   * 
   * @return HyperBoundingBox UnionMinBB *
   */
  public HyperBoundingBox getUnionMinBB( )
  {
    return unionMinBB;
  }

  /**
   *  
   */
  protected void updateNodeBoundingBox( )
  {
    unionMinBB = HyperBoundingBox.getNullHyperBoundingBox( file.getDimension() );

    for( int i = 0; i < getUsedSpace(); i++ )
      unionMinBB = unionMinBB.unionBoundingBox( hyperBBs[i] );
  }

  /**
   * Array von HyperBoundingBoxen der Eintr�ge im Knoten. Array kann leer sein ! Anzahl belegter Pl�tze siehe
   * getUsedSpace.
   * 
   * @return HyperBoundingBox[] Boxes der Eintr�ge
   * @see #getUsedSpace()
   */
  public HyperBoundingBox[] getHyperBoundingBoxes( )
  {
    return hyperBBs;
  }

  /**
   * HyperBoundingBox f�r Eintrag index im Knoten.
   * 
   * @param index
   *          des Eintrages
   * @return HyperBoundingBox Box f�r den Eintrag
   */
  public HyperBoundingBox getHyperBoundingBox( final int index )
  {
    return hyperBBs[index];
  }

  /**
   * Pr�ft ob Knoten Rootknoten ist.
   * 
   * @return boolean true, wenn root
   */
  public boolean isRoot( )
  {
    return pageNumber == 0;
  }

  /**
   * Tiefe Kopie ohne Dateneintr�ge (nur HyperBoundingBoxes) �berschreibt Methode clone in Object.
   * 
   * @see java.lang.Object#clone()
   */
  @Override
  public abstract Object clone( );

  /**
   * String-Repr�sentation des Knotens
   */
  @Override
  public String toString( )
  {
    String str = "";

    if( this instanceof LeafNode )
    {
      str = "LeafNode: " + unionMinBB.toString();
    }
    else
    {
      str = "NoneLeafNode: " + unionMinBB.toString();
    }

    return str;
  }
}