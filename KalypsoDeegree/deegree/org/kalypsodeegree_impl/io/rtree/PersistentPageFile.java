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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Stack;

/**
 * Persistente Implementierung einer PageFile. Implementiert als RandomAccesFile. Aufbau der Datei -- Header -- int
 * pageFileVersion int dimension int capacity = maximum + 1 for overflow int minimum -- Body -- aufeinanderfolgende
 * Pages mit int typ - 1 LeafNode 2 NoneLeafNode int place - Platz, wo Knoten im Vaterknoten steht int counter - Derzeit
 * benutzer Platz im Knoten int parentNode - Nummer der PageFile des Vaterknotens int pageNumber - eigene
 * PageFile-Nummer - for(i = 0; i < capacity; i++) int data Eintrag i - pageNumber Kindknoten oder Objekt-ID der
 * Dateneintr�ge - jeweils Abh�ngigkeit von dimension = x double pMin x.Dimension - pMin der gemeinsamen
 * HyperBoundingBox double pMax x.Dimension - pMax der gemeinsamen HyperBoundingBox - for(i = 0; i < capacity; i++)
 * double pMin x.Dimension - pMin HyperBB f�r Eintrag i double pMax x.Dimension - pMax HyperBB f�r Eintrag i int entspr.
 * 4 Bytes - double entspr. 8 Bytes PageSize = (4 * (5 + capacity)) + (capacity + 1) * (dimension * 16)
 * 
 * @version 1.0
 * @author Wolfgang B�r
 */
public class PersistentPageFile extends PageFile
{
  private static final int PAGEFILE_VERSION = 060676002;

  private static final int EMPTY_PAGE = -22;

  private RandomAccessFile file;

  private final Stack emptyPages;

  private final String fileName;

  private byte[] buffer;

  private boolean closed;

  private int pageSize;

  /**
   * Konstruktor f�r PersistentPageFile.
   * 
   * @param fileName
   */
  public PersistentPageFile( final String fileName )
  {
    super();
    this.fileName = fileName;
    emptyPages = new Stack();
    closed = false;
  }

  /**
   * Initialisiert die PageFile. �berschreibt initialize() in PageFile.
   * 
   * @param dimension
   *          der Daten
   * @param capacity
   *          Kapazit�t der Knoten
   * @throws PageFileException
   */
  @Override
  public void initialize( final int dimension, final int capacity ) throws PageFileException
  {
    // Initialisierung
    super.initialize( dimension, capacity );

    final File fileTest = new File( fileName );

    try
    {
      if( dimension == -999 )
      {
        // Initialisierung aus bestehender Datei
        if( !fileTest.exists() )
        {
          throw new PageFileException( "Datei exisitiert nicht !" );
        }

        file = new RandomAccessFile( fileTest, "rw" );

        // �berpr�fung der Korrektheit der Datei
        file.seek( 0 );

        if( file.readInt() != PAGEFILE_VERSION )
        {
          throw new PageFileException( "Keine PersistenPageFile oder falsche Version !" );
        }

        // Auslesend der Dimension - Initialisierung PageFile
        m_dimension = file.readInt();
        m_capacity = file.readInt();
        m_minimum = file.readInt();
        pageSize = 4 * (5 + m_capacity) + (m_capacity + 1) * m_dimension * 16;
        buffer = new byte[pageSize];

        // Einlesen leerer Seiten
        int i = 0;

        try
        {
          while( true )
          {
            file.seek( 16 + i * pageSize );

            if( EMPTY_PAGE == file.readInt() )
            {
              emptyPages.push( new Integer( i ) );
            }

            i++;
          }
        }
        catch( final IOException ioe )
        {
          // shouldnt we print stack trace?
        }
      }
      else
      {
        // neue Datei
        file = new RandomAccessFile( fileTest, "rw" );
        file.setLength( 0 );
        pageSize = 4 * (5 + capacity) + (capacity + 1) * dimension * 16;
        buffer = new byte[pageSize];

        // header schreiben (Dimension , Dateiversion)
        file.seek( 0 );
        file.writeInt( PAGEFILE_VERSION );
        file.writeInt( m_dimension );
        file.writeInt( m_capacity );
        file.writeInt( m_minimum );
      }
    }
    catch( final IOException e )
    {
      e.fillInStackTrace();
      throw new PageFileException( "IOException occured: \n " + e.getMessage() );
    }
  }

  /**
   * @see PageFile#readNode(int)
   */
  @Override
  public Node readNode( final int pageNumber ) throws PageFileException
  {
    Node node = null;

    try
    {
      file.seek( 16 + pageNumber * pageSize );

      final int read = file.read( buffer );

      if( pageSize == read )
      {
        final DataInputStream ds = new DataInputStream( new ByteArrayInputStream( buffer ) );

        final int type = ds.readInt();

        if( type == 1 )
        {
          node = new LeafNode( -1, this );
        }
        else
        {
          node = new NoneLeafNode( -1, this );
        }

        node.place = ds.readInt();
        node.counter = ds.readInt();
        node.parentNode = ds.readInt();
        node.pageNumber = ds.readInt();

        if( type == 1 )
        {
          for( int i = 0; i < m_capacity; i++ )
            ((LeafNode) node).data[i] = ds.readInt();
        }
        else
        {
          for( int i = 0; i < m_capacity; i++ )
            ((NoneLeafNode) node).childNodes[i] = ds.readInt();
        }

        node.unionMinBB = readNextHyperBoundingBox( ds );

        for( int i = 0; i < m_capacity; i++ )
          node.hyperBBs[i] = readNextHyperBoundingBox( ds );

        ds.close();
      }
      else
      {
        throw new PageFileException( "Fehler in bei PageFile-Lesen" );
      }

      return node;
    }
    catch( final IOException e )
    {
      e.fillInStackTrace();
      throw new PageFileException( "PageFileException occured ! \n " + e.getMessage() );
    }
  }

  /**
   * @throws IOException
   */
  public HyperBoundingBox readNextHyperBoundingBox( final DataInputStream ds ) throws IOException
  {
    double[] point1;
    double[] point2;
    point1 = new double[m_dimension];
    point2 = new double[m_dimension];

    for( int i = 0; i < m_dimension; i++ )
      point1[i] = ds.readDouble();

    for( int i = 0; i < m_dimension; i++ )
      point2[i] = ds.readDouble();

    return new HyperBoundingBox( new HyperPoint( point1 ), new HyperPoint( point2 ) );
  }

  /**
   * @see PageFile#writeNode(Node)
   */
  @Override
  public int writeNode( final Node node ) throws PageFileException
  {
    try
    {
      if( node.pageNumber < 0 )
      {
        if( !emptyPages.empty() )
        {
          node.setPageNumber( ((Integer) emptyPages.pop()).intValue() );
        }
        else
        {
          node.setPageNumber( (int) ((file.length() - 16) / pageSize) );
        }
      }

      final ByteArrayOutputStream bs = new ByteArrayOutputStream( pageSize );
      final DataOutputStream ds = new DataOutputStream( bs );

      int type;

      if( node instanceof LeafNode )
      {
        type = 1;
      }
      else
      {
        type = 2;
      }

      ds.writeInt( type );

      ds.writeInt( node.place );
      ds.writeInt( node.counter );
      ds.writeInt( node.parentNode );
      ds.writeInt( node.pageNumber );

      if( node instanceof LeafNode )
      {
        for( int i = 0; i < node.counter; i++ )
        {
          ds.writeInt( ((LeafNode) node).data[i] );
        }

        for( int i = 0; i < m_capacity - node.counter; i++ )
          ds.writeInt( -1 );
      }
      else
      {
        for( int i = 0; i < node.counter; i++ )
        {
          ds.writeInt( ((NoneLeafNode) node).childNodes[i] );
        }

        for( int i = 0; i < m_capacity - node.counter; i++ )
          ds.writeInt( -1 );
      }

      for( int i = 0; i < m_dimension; i++ )
        ds.writeDouble( node.unionMinBB.getPMin().getCoord( i ) );

      for( int i = 0; i < m_dimension; i++ )
        ds.writeDouble( node.unionMinBB.getPMax().getCoord( i ) );

      for( int j = 0; j < node.counter; j++ )
      {
        for( int i = 0; i < m_dimension; i++ )
          ds.writeDouble( node.hyperBBs[j].getPMin().getCoord( i ) );

        for( int i = 0; i < m_dimension; i++ )
          ds.writeDouble( node.hyperBBs[j].getPMax().getCoord( i ) );
      }

      for( int j = 0; j < m_capacity - node.counter; j++ )
      {
        for( int i = 0; i < m_dimension * 2; i++ )
          ds.writeDouble( -1 );
      }

      ds.flush();
      bs.flush();

      file.seek( 16 + pageSize * node.pageNumber );

      file.write( bs.toByteArray() );

      ds.close();

      return node.pageNumber;
    }
    catch( final IOException e )
    {
      e.fillInStackTrace();
      throw new PageFileException( "PageFileException occured ! \n " + e.getMessage() );
    }
  }

  /**
   * @see PageFile#deleteNode(int)
   */
  @Override
  public Node deleteNode( final int pageNumber ) throws PageFileException
  {
    final Node node = readNode( pageNumber );

    try
    {
      file.seek( 16 + pageSize * node.pageNumber );
      file.writeInt( EMPTY_PAGE );
    }
    catch( final IOException e )
    {
      e.fillInStackTrace();
      throw new PageFileException( "PageFileException occured ! \n " + e.getMessage() );
    }

    emptyPages.push( new Integer( pageNumber ) );
    return node;
  }

  /**
   * @see PageFile#close()
   */
  @Override
  public void close( ) throws PageFileException
  {
    try
    {
      file.close();
    }
    catch( final IOException e )
    {
      e.fillInStackTrace();
      throw new PageFileException( "PageFileException during close()" );
    }

    closed = true;
  }

  /**
   * @throws Throwable
   */
  @Override
  public void finalize( ) throws Throwable
  {
    if( !closed && file != null )
    {
      file.close();
    }

    super.finalize();
  }
}