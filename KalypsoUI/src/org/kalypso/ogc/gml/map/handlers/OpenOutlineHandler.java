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
package org.kalypso.ogc.gml.map.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.kalypso.ogc.gml.outline.ViewContentOutline;
import org.kalypso.ui.internal.i18n.Messages;

/**
 * This handler opens the outline.
 * 
 * @author Holger Albert
 */
public class OpenOutlineHandler extends AbstractHandler
{
  /**
   * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
   */
  @Override
  public Object execute( final ExecutionEvent event ) throws ExecutionException
  {
    final IEvaluationContext context = (IEvaluationContext)event.getApplicationContext();
    final IWorkbenchPart part = (IWorkbenchPart)context.getVariable( ISources.ACTIVE_PART_NAME );
    if( part == null )
      throw new ExecutionException( Messages.getString( "org.kalypso.ogc.gml.map.handlers.OpenOutlineHandler.0" ) ); //$NON-NLS-1$

    final IWorkbenchPartSite site = part.getSite();
    if( site == null )
      throw new ExecutionException( Messages.getString( "org.kalypso.ogc.gml.map.handlers.OpenOutlineHandler.1" ) ); //$NON-NLS-1$

    final IWorkbenchPage page = site.getPage();
    if( page == null )
      throw new ExecutionException( Messages.getString( "org.kalypso.ogc.gml.map.handlers.OpenOutlineHandler.2" ) ); //$NON-NLS-1$

    try
    {
      final IViewPart view = page.findView( ViewContentOutline.ID );
      if( view != null )
      {
        page.hideView( view );

        return Status.OK_STATUS;
      }

      // TODO should not be necessary: better: VIEW_SHOW instead and fix the outline, so it directly finds the map
      // itself.
      /* Open the outline. */
      page.showView( ViewContentOutline.ID, null, IWorkbenchPage.VIEW_ACTIVATE );

      /* Focus the part. If it is a map view, the outline will be filled. */
      page.activate( part );

      return Status.OK_STATUS;
    }
    catch( final Exception e )
    {
      throw new ExecutionException( Messages.getString( "org.kalypso.ogc.gml.map.handlers.OpenOutlineHandler.3" ), e ); //$NON-NLS-1$
    }
  }
}