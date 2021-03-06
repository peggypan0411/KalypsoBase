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
package org.kalypsodeegree.xml;

/**
 * This exception is thrown when a syntactic or semantic error has been encountered during the parsing process of a XML
 * document.
 * <p>
 * 
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @version $Revision$
 */
public class XMLParsingException extends Exception
{

  private String message = "org.kalypsodeegree.xml.XMLParsingException";

  private String st = "";

  /**
   * Creates a new instance of <code>XMLParsingException</code> without detail message.
   */
  public XMLParsingException( )
  {
  }

  /**
   * Constructs an instance of <code>XMLParsingException</code> with the specified detail message.
   * 
   * @param msg
   *          the detail message.
   */
  public XMLParsingException( final String msg )
  {
    message = msg;
  }

  /**
   * Constructs an instance of <code>XMLParsingException</code> with the specified detail message.
   * 
   * @param msg
   *          the detail message.
   */
  public XMLParsingException( final String msg, final Exception e )
  {
    this( msg );
    final StackTraceElement[] se = e.getStackTrace();
    final StringBuffer sb = new StringBuffer();
    for( final StackTraceElement element : se )
    {
      sb.append( element.getClassName() + " " );
      sb.append( element.getFileName() + " " );
      sb.append( element.getMethodName() + "(" );
      sb.append( element.getLineNumber() + ")\n" );
    }
    st = e.getMessage() + sb.toString();
  }

  @Override
  public String toString( )
  {
    return message + "\n" + st;
  }

  @Override
  public String getMessage( )
  {
    return message;
  }
}