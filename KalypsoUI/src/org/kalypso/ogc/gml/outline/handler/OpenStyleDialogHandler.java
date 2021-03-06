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
package org.kalypso.ogc.gml.outline.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.kalypso.contribs.eclipse.jface.viewers.SelectionProviderAdapter;
import org.kalypso.ui.editor.mapeditor.views.StyleEditorViewPart;
import org.kalypso.ui.internal.i18n.Messages;

/**
 * @author Gernot Belger
 */
public class OpenStyleDialogHandler extends AbstractHandler
{
  /**
   * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
   */
  @Override
  public Object execute( final ExecutionEvent event ) throws ExecutionException
  {
    final IEvaluationContext context = (IEvaluationContext)event.getApplicationContext();
    final IWorkbenchWindow window = (IWorkbenchWindow)context.getVariable( ISources.ACTIVE_WORKBENCH_WINDOW_NAME );
    final IStructuredSelection selection = (IStructuredSelection)context.getVariable( ISources.ACTIVE_CURRENT_SELECTION_NAME );

    try
    {
      final IViewPart view = window.getActivePage().showView( "org.kalypso.ui.editor.mapeditor.views.styleeditor" ); //$NON-NLS-1$
      // Might be an ErrorView instead
      if( view instanceof StyleEditorViewPart )
      {
        final StyleEditorViewPart part = (StyleEditorViewPart)view; //$NON-NLS-1$
        part.selectionChanged( new SelectionChangedEvent( new SelectionProviderAdapter(), selection ) );
      }
    }
    catch( final PartInitException e )
    {
      throw new ExecutionException( Messages.getString( "org.kalypso.ogc.gml.outline.handler.OpenStyleDialogHandler.0" ), e ); //$NON-NLS-1$
    }

    return null;
  }

}
