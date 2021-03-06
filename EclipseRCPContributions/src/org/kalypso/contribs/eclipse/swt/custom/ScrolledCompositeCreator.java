/*
 * ---------------- FILE HEADER KALYPSO ------------------------------------------
 * 
 * This file is part of kalypso. Copyright (C) 2004 by:
 * 
 * Technical University Hamburg-Harburg (TUHH) Institute of River and coastal engineering Denickestraße 22 21073
 * Hamburg, Germany http://www.tuhh.de/wb
 * 
 * and
 * 
 * Bjoernsen Consulting Engineers (BCE) Maria Trost 3 56070 Koblenz, Germany http://www.bjoernsen.de
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Contact:
 * 
 * E-Mail: g.belger@bjoernsen.de m.schlienger@bjoernsen.de v.doemming@tuhh.de
 * 
 * ---------------------------------------------------------------------------
 */
package org.kalypso.contribs.eclipse.swt.custom;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Helper class to construct scrolled composite
 * 
 * @author thuel2
 */
public abstract class ScrolledCompositeCreator
{
  private ScrolledComposite m_scrolledComposite;

  private Control m_contentControl;

  private final SizeProvider m_maxSizeProvider;

  public static interface SizeProvider
  {
    public Point getSize( final Point clientAreaSize );
  }

  public ScrolledCompositeCreator( final SizeProvider maxSizeProvider )
  {
    m_maxSizeProvider = maxSizeProvider;
  }

  public final void createControl( final Composite parent, final int style, final int contentStyle )
  {
    final ScrolledComposite scrolledComposite = new ScrolledComposite( parent, style );

    m_contentControl = createContents( scrolledComposite, contentStyle );
    scrolledComposite.setContent( m_contentControl );

    m_scrolledComposite = scrolledComposite;
    final ControlAdapter controlAdapter = new ControlAdapter()
    {
      /**
       * @see org.eclipse.swt.events.ControlAdapter#controlResized(org.eclipse.swt.events.ControlEvent)
       */
      @Override
      public void controlResized( final ControlEvent e )
      {
        updateControlSize( true );
      }
    };

    scrolledComposite.addControlListener( controlAdapter );

    updateControlSize( false );
  }

  public final void updateControlSize( final boolean ignoreZeroClientSize )
  {
    final Point controlSize = m_contentControl.computeSize( SWT.DEFAULT, SWT.DEFAULT );
    final int controlX = controlSize.x;
    final int controlY = controlSize.y;

    final Rectangle clientArea = m_scrolledComposite.getClientArea();
    final int psizex = clientArea.width;
    final int psizey = clientArea.height;
    if( ignoreZeroClientSize && (psizex == 0 || psizey == 0) )
      return;
    final Point clientareaSize = new Point( clientArea.width, clientArea.height );

    final int style = m_scrolledComposite.getStyle();

    final Point maxSize = m_maxSizeProvider == null ? new Point( psizex, psizey ) : m_maxSizeProvider.getSize( clientareaSize );
    final int newX = (style & SWT.H_SCROLL) != 0 ? Math.max( controlX, psizex ) : Math.min( psizex, maxSize.x );
    final int newY = (style & SWT.V_SCROLL) != 0 ? Math.max( controlY, psizey ) : Math.min( psizey, maxSize.y );

    final Point newSize = new Point( newX == 0 ? controlX : newX, newY == 0 ? controlY : newY );

    // call this async because this leads to another call to this method
    // causing ugly behaviour regarding the scroll bars
    final Control contentControl = m_contentControl;
    m_scrolledComposite.getDisplay().asyncExec( new Runnable()
    {
      @Override
      public void run( )
      {
        if( contentControl != null && !contentControl.isDisposed() )
          contentControl.setSize( newSize );
      }
    } );
  }

  public final Control getContentControl( )
  {
    return m_contentControl;
  }

  public final ScrolledComposite getScrolledComposite( )
  {
    return m_scrolledComposite;
  }

  protected abstract Control createContents( final Composite parent, final int style );
}