/*----------------    FILE HEADER KALYPSO ------------------------------------------
 *
 *  This file is part of kalypso.
 *  Copyright (C) 2004 by:
 *
 *  Technical University Hamburg-Harburg (TUHH)
 *  Institute of River and coastal engineering
 *  Denickestraße 22
 *  21073 Hamburg, Germany
 *  http://www.tuhh.de/wb
 *
 *  and
 *
 *  Bjoernsen Consulting Engineers (BCE)
 *  Maria Trost 3
 *  56070 Koblenz, Germany
 *  http://www.bjoernsen.de
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  Contact:
 *
 *  E-Mail:
 *  belger@bjoernsen.de
 *  schlienger@bjoernsen.de
 *  v.doemming@tuhh.de
 *
 *  ---------------------------------------------------------------------------*/
package org.kalypso.core.internal.layoutwizard.part;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.kalypso.core.layoutwizard.ILayoutPart;

/**
 * @author Gernot Belger
 */
public class GridLayoutContainer extends AbstractLayoutContainer
{
  private final GridLayout m_gridLayout;

  private final String m_groupText;

  public GridLayoutContainer( final String id, final GridLayout gridLayout )
  {
    this( id, gridLayout, null );
  }

  /**
   * @param groupText
   *          If not-<code>null</code> a {@link Group} is created instead of a {@link Composite} and the text is set as
   *          the group's lable.
   */
  public GridLayoutContainer( final String id, final GridLayout gridLayout, final String groupText )
  {
    super( id );

    m_gridLayout = gridLayout;
    m_groupText = groupText;
  }

  /**
   * @see org.kalypso.hwv.ui.wizards.calculation.modelpages.layout.ILayoutPart#createControl(org.eclipse.ui.forms.widgets.FormToolkit,
   *      org.eclipse.swt.widgets.Composite)
   */
  @Override
  public Control createControl( final Composite parent, final FormToolkit toolkit )
  {
    final Composite panel = createComposite( toolkit, parent );

    panel.setLayout( m_gridLayout );

    final ILayoutPart[] children = getChildren();
    for( final ILayoutPart child : children )
    {
      final Control childControl = child.createControl( panel, toolkit );
      childControl.setLayoutData( child.getLayoutData() );
    }

    return panel;
  }

  private Composite createComposite( final FormToolkit toolkit, final Composite parent )
  {
    if( m_groupText == null )
      return toolkit.createComposite( parent, getStyle() );

    final Group group = new Group( parent, getStyle() );
    toolkit.adapt( group );
    group.setText( m_groupText );

    return group;
  }
}
