/*--------------- Kalypso-Header --------------------------------------------------------------------

 This file is part of kalypso.
 Copyright (C) 2004, 2005 by:

 Technical University Hamburg-Harburg (TUHH)
 Institute of River and coastal engineering
 Denickestr. 22
 21073 Hamburg, Germany
 http://www.tuhh.de/wb

 and

 Bjoernsen Consulting Engineers (BCE)
 Maria Trost 3
 56070 Koblenz, Germany
 http://www.bjoernsen.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 Contact:

 E-Mail:
 belger@bjoernsen.de
 schlienger@bjoernsen.de
 v.doemming@tuhh.de

 ---------------------------------------------------------------------------------------------------*/
package org.kalypso.ui.editor.featureeditor;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.progress.IProgressService;
import org.kalypso.commons.command.DefaultCommandManager;
import org.kalypso.contribs.eclipse.core.resources.ResourceUtilities;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.core.util.pool.IPoolableObjectType;
import org.kalypso.ogc.gml.GisTemplateHelper;
import org.kalypso.template.featureview.Featuretemplate;
import org.kalypso.ui.internal.i18n.Messages;
import org.kalypso.util.command.JobExclusiveCommandTarget;

/**
 * <p>
 * Eclipse-Editor zum Editieren von Features
 * </p>
 * 
 * @author belger
 */
public class FeatureEditor extends EditorPart
{
  private final Runnable m_dirtyRunnable = new Runnable()
  {
    @Override
    public void run( )
    {
      final Shell shell = getSite().getShell();
      if( shell != null )
      {
        shell.getDisplay().asyncExec( new Runnable()
        {
          @Override
          public void run( )
          {
            fireDirtyChange();
          }
        } );
      }
    }
  };

  private final JobExclusiveCommandTarget m_commandTarget = new JobExclusiveCommandTarget( new DefaultCommandManager(), m_dirtyRunnable );

  private final FeatureTemplateviewer m_viewer = new FeatureTemplateviewer( m_commandTarget );

  /**
   * @see org.kalypso.ui.editor.AbstractWorkbenchPart#dispose()
   */
  @Override
  public void dispose( )
  {
    m_commandTarget.dispose();

    m_viewer.dispose();

    super.dispose();
  }

  /**
   * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
   */
  @Override
  public boolean isSaveAsAllowed( )
  {
    return false;
  }

  /**
   * @see org.eclipse.ui.part.EditorPart#doSaveAs()
   */
  @Override
  public void doSaveAs( )
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
   */
  @Override
  public void init( final IEditorSite site, final IEditorInput input ) throws PartInitException
  {
    if( !(input instanceof IStorageEditorInput) )
      throw new PartInitException( "Can only use IStorageEditorInput" ); //$NON-NLS-1$

    setSite( site );

    setInput( input );
  }

  /**
   * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
   */
  @Override
  protected final void setInput( final IEditorInput input )
  {
    if( !(input instanceof IStorageEditorInput) )
      throw new IllegalArgumentException( "Only IStorageEditorInput supported" ); //$NON-NLS-1$

    super.setInput( input );

    load( (IStorageEditorInput)input );
  }

  /**
   * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
   */
  @Override
  public void doSave( final IProgressMonitor monitor )
  {
    try
    {
      m_viewer.saveGML( monitor );
    }
    catch( final Exception e )
    {
      e.printStackTrace();

      final IStatus status = StatusUtilities.statusFromThrowable( e );
      ErrorDialog.openError( getSite().getShell(), Messages.getString( "org.kalypso.ui.editor.featureeditor.FeatureEditor.2" ), Messages.getString( "org.kalypso.ui.editor.featureeditor.FeatureEditor.3" ), status ); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  /**
   * @see org.eclipse.ui.part.EditorPart#isDirty()
   */
  @Override
  public boolean isDirty( )
  {
    return false;
  }

  /**
   * @see org.eclipse.ui.IWorkbenchPart#setFocus()
   */
  @Override
  public void setFocus( )
  {
    // nix
  }

  /**
   * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createPartControl( final Composite parent )
  {
    m_viewer.createControls( parent, SWT.V_SCROLL );

    final IActionBars actionBars = getEditorSite().getActionBars();
    actionBars.setGlobalActionHandler( ActionFactory.UNDO.getId(), m_commandTarget.undoAction );
    actionBars.setGlobalActionHandler( ActionFactory.REDO.getId(), m_commandTarget.redoAction );
    actionBars.updateActionBars();
  }

  protected final void load( final IStorageEditorInput input )
  {
    final WorkspaceModifyOperation op = new WorkspaceModifyOperation()
    {
      @Override
      protected void execute( final IProgressMonitor monitor ) throws CoreException
      {
        loadInput( input, monitor );
      }
    };

    final IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
    try
    {
      progressService.busyCursorWhile( op );

      setPartName( input.getName() );
      if( input instanceof IFileEditorInput )
        setContentDescription( ((IFileEditorInput)input).getFile().getFullPath().toOSString() );
    }
    catch( final InvocationTargetException e )
    {
      e.printStackTrace();

      final Throwable targetException = e.getTargetException();

      final IStatus status;
      if( targetException instanceof CoreException )
        status = ((CoreException)targetException).getStatus();
      else
      {
        final String locmsg = targetException.getLocalizedMessage();
        final String msg = locmsg == null ? "" : locmsg; //$NON-NLS-1$
        status = StatusUtilities.statusFromThrowable( targetException, msg );
      }

      ErrorDialog.openError( getEditorSite().getShell(), Messages.getString( "org.kalypso.ui.editor.featureeditor.FeatureEditor.5" ), Messages.getString( "org.kalypso.ui.editor.featureeditor.FeatureEditor.6" ), status ); //$NON-NLS-1$ //$NON-NLS-2$
    }
    catch( final InterruptedException e )
    {
      e.printStackTrace();
    }
  }

  protected final void loadInput( final IStorageEditorInput input, final IProgressMonitor monitor ) throws CoreException
  {
    monitor.beginTask( Messages.getString( "org.kalypso.ui.editor.featureeditor.FeatureEditor.7" ), 1000 ); //$NON-NLS-1$

    try
    {
      final IStorage storage = input.getStorage();

      final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile( storage.getFullPath() );
      final URL templateContext = ResourceUtilities.createURL( file );

      final Featuretemplate template = GisTemplateHelper.loadGisFeatureTemplate( storage );

      final IPoolableObjectType key = FeatureTemplateviewer.createKey( template, null, null, templateContext );

      m_viewer.setTemplate( template, key, null, templateContext );
    }
    catch( final MalformedURLException e )
    {
      e.printStackTrace();

      throw new CoreException( StatusUtilities.statusFromThrowable( e, Messages.getString( "org.kalypso.ui.editor.featureeditor.FeatureEditor.8" ) ) ); //$NON-NLS-1$
    }
    catch( final CoreException e )
    {
      e.printStackTrace();

      throw e;
    }
    catch( final Throwable e )
    {
      e.printStackTrace();

      throw new CoreException( StatusUtilities.statusFromThrowable( e, Messages.getString( "org.kalypso.ui.editor.featureeditor.FeatureEditor.9" ) ) ); //$NON-NLS-1$
    }
    finally
    {
      monitor.done();
    }
  }

  protected void fireDirtyChange( )
  {
    firePropertyChange( PROP_DIRTY );
  }
}