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
package org.kalypso.transformation.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.progress.UIJob;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.i18n.Messages;
import org.kalypso.transformation.CRSHelper;
import org.kalypso.transformation.crs.ICoordinateSystem;
import org.kalypso.transformation.ui.provider.CRSLabelProvider;

/**
 * This class represents a panel with elements for choosing a coordinate system.<br>
 * *
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>NO_GROUP</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * 
 * @author Holger Albert
 */
public class CRSSelectionPanel extends Composite implements IJobChangeListener
{
  /**
   * Style constant. If set, no group is shown in this composite.
   */
  public static final int NO_GROUP = SWT.SEARCH;

  /**
   * The list of selection changed listener. This listeners will be added to the combo viewer, too. The list is only
   * maintained here for a special case. It should not be used otherwise.
   */
  protected List<ISelectionChangedListener> m_listener;

  /**
   * The combo viewer of the coordinate systems. Null, if no controls has been created.
   */
  protected ComboViewer m_viewer;

  /**
   * A hash of the displayed coordinate systems.
   */
  protected HashMap<String, ICoordinateSystem> m_coordHash;

  /**
   * List of all images.
   */
  private List<Image> m_imageList;

  /**
   * Some extra text for displaying after the group title.
   */
  private String m_extText;

  /**
   * This selection is memorized, if it was set, and no coordinate systems were available. It will be set, if coordinate
   * systems got available.
   */
  protected String m_lazySelection;

  /**
   * The constructor.
   * 
   * @param parent
   * @param style
   */
  public CRSSelectionPanel( Composite parent, int style )
  {
    this( parent, style, null );
  }

  /**
   * The constructor.
   * 
   * @param parent
   * @param style
   * @param extText
   *          Some extra text for displaying after the group title.
   */
  public CRSSelectionPanel( Composite parent, int style, String extText )
  {
    super( parent, style );

    m_extText = extText;

    m_listener = new ArrayList<ISelectionChangedListener>();
    m_viewer = null;
    m_coordHash = new HashMap<String, ICoordinateSystem>();
    m_imageList = new ArrayList<Image>();
    m_lazySelection = null;

    createControls();
  }

  /**
   * This function creates the controls.
   */
  private void createControls( )
  {
    super.setLayout( new FillLayout() );

    /* Create the main group for the panel. */
    Composite main = createMainComposite();

    /* Create the combo. */
    m_viewer = new ComboViewer( main, SWT.READ_ONLY | SWT.DROP_DOWN );
    m_viewer.getCombo().setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    m_viewer.setContentProvider( new ArrayContentProvider() );
    m_viewer.setLabelProvider( new CRSLabelProvider( false ) );
    m_viewer.setSorter( new ViewerSorter() );
    m_viewer.setInput( new String[] { Messages.getString( "org.kalypso.transformation.ui.CRSSelectionPanel.2" ) } ); //$NON-NLS-1$
    m_viewer.setSelection( new StructuredSelection( Messages.getString( "org.kalypso.transformation.ui.CRSSelectionPanel.3" ) ) ); //$NON-NLS-1$

    /* Create the info image. */
    final Label imageLabel = new Label( main, SWT.NONE );
    imageLabel.setLayoutData( new GridData( SWT.CENTER, SWT.CENTER, false, false ) );

    /* Set the image. */
    ImageDescriptor imgDesc = ImageDescriptor.createFromURL( getClass().getResource( "resources/info.gif" ) ); //$NON-NLS-1$
    Image infoImage = imgDesc.createImage();
    m_imageList.add( infoImage );
    imageLabel.setImage( infoImage );

    /* Refresh the tooltip. */
    m_viewer.addSelectionChangedListener( new ISelectionChangedListener()
    {
      /**
       * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
       */
      @Override
      public void selectionChanged( SelectionChangedEvent event )
      {
        /* Get the code of the selected coordinate system. */
        String selectedCRS = getSelectedCRS();
        if( selectedCRS == null )
        {
          imageLabel.setToolTipText( "" ); //$NON-NLS-1$
          return;
        }

        /* Get the hashed coordinate system. */
        imageLabel.setToolTipText( CRSHelper.getTooltipText( selectedCRS ) );
      }
    } );

    /* Set the input. */
    updateCoordinateSystemsCombo( CRSHelper.getAllNames() );
  }

  private Composite createMainComposite( )
  {
    if( (getStyle() & NO_GROUP) != 0 )
    {
      Composite composite = new Composite( this, SWT.NONE );
      GridLayout layout = new GridLayout( 2, false );
      layout.marginHeight = 0;
      layout.marginWidth = 0;
      composite.setLayout( layout );
      return composite;
    }

    Group main = new Group( this, SWT.NONE );
    String title = Messages.getString( "org.kalypso.transformation.ui.CRSSelectionPanel.0" ); //$NON-NLS-1$

    if( StringUtils.isEmpty( m_extText ) )
      main.setText( title );
    else
      main.setText( String.format( "%s %s", title, m_extText ) ); //$NON-NLS-1$
    main.setLayout( new GridLayout( 2, false ) );

    return main;
  }

  /**
   * @see org.eclipse.swt.widgets.Composite#setLayout(org.eclipse.swt.widgets.Layout)
   */
  @Override
  public void setLayout( Layout layout )
  {
    /* Ignore user set layouts, only layout datas are permitted. */
  }

  /**
   * @see org.eclipse.swt.widgets.Control#setEnabled(boolean)
   */
  @Override
  public void setEnabled( boolean enabled )
  {
    super.setEnabled( enabled );

    if( m_viewer != null && !m_viewer.getControl().isDisposed() )
      m_viewer.getControl().setEnabled( enabled );
  }

  /**
   * This function disposes the images.
   */
  @Override
  public void dispose( )
  {
    super.dispose();

    /* Dispose all images and colors. */
    for( Image image : m_imageList )
    {
      if( !image.isDisposed() )
        image.dispose();
    }
  }

  /**
   * This function sets the selection of the panel.
   * 
   * @param selection
   *          The selection.
   */
  public void setSelectedCRS( String selectedCRS )
  {
    if( m_viewer != null && !m_viewer.getControl().isDisposed() )
    {
      /* If no coordinate systems are available, save the selection. */
      if( m_coordHash == null || m_coordHash.size() == 0 )
      {
        m_lazySelection = selectedCRS;
        return;
      }

      /* If coordinate systems are available, set it directly. */
      ICoordinateSystem coordinateSystem = m_coordHash.get( selectedCRS );
      if( coordinateSystem != null )
        m_viewer.setSelection( new StructuredSelection( coordinateSystem ) );
    }
  }

  /**
   * This function returns the code of the selected coordinate system.
   * 
   * @return The code of the selected coordinate system or null, if none is selected.
   */
  public String getSelectedCRS( )
  {
    if( m_viewer == null || m_viewer.getControl().isDisposed() )
      return null;

    /* Get the selection. */
    ISelection selection = m_viewer.getSelection();

    /* If not empty and the right type, the name is returned. */
    if( !selection.isEmpty() && selection instanceof IStructuredSelection )
    {
      /* Cast. */
      IStructuredSelection structuredSelection = (IStructuredSelection) selection;

      /* Get the selected element. */
      Object selectedElement = structuredSelection.getFirstElement();

      /* Check type. */
      if( selectedElement instanceof ICoordinateSystem )
      {
        /* Cast. */
        ICoordinateSystem coordinateSystem = (ICoordinateSystem) selectedElement;

        /* Return the code of the selected coordinate system. */
        return coordinateSystem.getCode();
      }
    }

    return null;
  }

  /**
   * This function adds a selection changed listener.
   * 
   * @param listener
   *          The selection changed listener.
   */
  public void addSelectionChangedListener( ISelectionChangedListener listener )
  {
    if( m_viewer != null && !m_viewer.getControl().isDisposed() )
    {
      m_viewer.addSelectionChangedListener( listener );
      m_listener.add( listener );
    }
  }

  /**
   * This function removes a selection changed listener.
   * 
   * @param listener
   *          The selection changed listener.
   */
  public void removeSelectionChangedListener( ISelectionChangedListener listener )
  {
    if( m_viewer != null && !m_viewer.getControl().isDisposed() )
    {
      m_viewer.removeSelectionChangedListener( listener );
      m_listener.remove( listener );
    }
  }

  /**
   * This function updates the combobox, displaying the coordinate systems.
   * 
   * @param codes
   *          The codes of the coordinate systems, to initialize.
   */
  public void updateCoordinateSystemsCombo( String[] codes )
  {
    if( m_viewer == null || m_viewer.getControl().isDisposed() )
      return;

    if( codes == null || codes.length == 0 )
    {
      m_coordHash.clear();
      m_viewer.setInput( null );
      return;
    }

    /* Disable. */
    setEnabled( false );

    /* Start the job. */
    CRSInitializeJob initCRSJob = new CRSInitializeJob( "CRSInitializeJob", codes ); //$NON-NLS-1$
    initCRSJob.setSystem( true );

    /* Add myself as a listener. */
    initCRSJob.addJobChangeListener( this );

    /* Schedule. */
    initCRSJob.schedule();
  }

  /**
   * @see org.eclipse.core.runtime.jobs.IJobChangeListener#aboutToRun(org.eclipse.core.runtime.jobs.IJobChangeEvent)
   */
  @Override
  public void aboutToRun( IJobChangeEvent event )
  {
  }

  /**
   * @see org.eclipse.core.runtime.jobs.IJobChangeListener#awake(org.eclipse.core.runtime.jobs.IJobChangeEvent)
   */
  @Override
  public void awake( IJobChangeEvent event )
  {
  }

  /**
   * @see org.eclipse.core.runtime.jobs.IJobChangeListener#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
   */
  @Override
  public void done( final IJobChangeEvent event )
  {
    if( isDisposed() )
      return;

    /* Get the display. */
    Display display = getDisplay();

    /* Create a UI job. */
    UIJob uiJob = new UIJob( display, "CRSSelectionPanelRefreshJob" ) //$NON-NLS-1$
    {
      /**
       * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      public IStatus runInUIThread( IProgressMonitor monitor )
      {
        try
        {
          /* If no monitor is given, create a null progress monitor. */
          if( monitor == null )
            monitor = new NullProgressMonitor();

          /* Monitor. */
          monitor.beginTask( Messages.getString( "org.kalypso.transformation.ui.CRSSelectionPanel.8" ), 100 ); //$NON-NLS-1$

          /* Get the job. */
          Job job = event.getJob();
          if( !(job instanceof CRSInitializeJob) )
          {
            /* Monitor. */
            monitor.worked( 100 );

            return Status.OK_STATUS;
          }

          /* Cast. */
          CRSInitializeJob initCRSJob = (CRSInitializeJob) job;

          /* If the viewer is already disposed, do nothing any more. */
          if( isDisposed() || m_viewer == null || m_viewer.getControl().isDisposed() )
          {
            /* Monitor. */
            monitor.worked( 100 );

            /* Remove myself as a listener. */
            initCRSJob.removeJobChangeListener( CRSSelectionPanel.this );

            return Status.OK_STATUS;
          }

          /* Get the hash of them. */
          HashMap<String, ICoordinateSystem> coordHash = initCRSJob.getCoordHash();
          if( coordHash == null || coordHash.size() == 0 )
          {
            /* Clear the hash. */
            m_coordHash.clear();

            /* Set the input. */
            m_viewer.setInput( null );

            /* Remove myself as a listener. */
            initCRSJob.removeJobChangeListener( CRSSelectionPanel.this );

            /* Enable. */
            setEnabled( true );

            /* Monitor. */
            monitor.worked( 100 );

            return Status.OK_STATUS;
          }

          /* The code of the coordinate system, which was set before. */
          String selectedCRS = getSelectedCRS();

          /* Store the hash. */
          m_coordHash = coordHash;

          /* Set the input (also resets the selection). */
          m_viewer.setInput( coordHash.values().toArray( new ICoordinateSystem[] {} ) );

          /* Get the codes. */
          List<String> codes = Arrays.asList( coordHash.keySet().toArray( new String[] {} ) );

          /* If the name of the old coordinate system is also among the new ones, select it again. */
          if( selectedCRS != null && codes.contains( selectedCRS ) )
            setSelectedCRS( selectedCRS );

          /* There was a coordinate system selected, but it does not exist any more. So the selection changes. */
          if( selectedCRS != null && !codes.contains( selectedCRS ) )
          {
            for( int i = 0; i < m_listener.size(); i++ )
            {
              ISelectionChangedListener listener = m_listener.get( i );
              listener.selectionChanged( new SelectionChangedEvent( m_viewer, m_viewer.getSelection() ) );
            }
          }

          /* If no coordinate system was selected, but someone wanted to select one, */
          /* this can now be set, if it is contained in the new list coordinate systems. */
          if( selectedCRS == null && m_lazySelection != null && codes.contains( m_lazySelection ) )
            setSelectedCRS( m_lazySelection );

          /* Delete the lazy selection. */
          m_lazySelection = null;

          /* Remove myself as a listener. */
          initCRSJob.removeJobChangeListener( CRSSelectionPanel.this );

          /* Enable. */
          setEnabled( true );

          /* Monitor. */
          monitor.worked( 100 );

          return Status.OK_STATUS;
        }
        catch( Exception ex )
        {
          ex.printStackTrace();

          return StatusUtilities.statusFromThrowable( ex );
        }
        finally
        {
          /* Monitor. */
          monitor.done();
        }
      }
    };

    /* Execute the UI job. */
    uiJob.schedule();
  }

  /**
   * @see org.eclipse.core.runtime.jobs.IJobChangeListener#running(org.eclipse.core.runtime.jobs.IJobChangeEvent)
   */
  @Override
  public void running( IJobChangeEvent event )
  {
  }

  /**
   * @see org.eclipse.core.runtime.jobs.IJobChangeListener#scheduled(org.eclipse.core.runtime.jobs.IJobChangeEvent)
   */
  @Override
  public void scheduled( IJobChangeEvent event )
  {
  }

  /**
   * @see org.eclipse.core.runtime.jobs.IJobChangeListener#sleeping(org.eclipse.core.runtime.jobs.IJobChangeEvent)
   */
  @Override
  public void sleeping( IJobChangeEvent event )
  {
  }
}