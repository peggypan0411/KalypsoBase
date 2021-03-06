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
package org.kalypso.core.status;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.internal.ide.IDEInternalWorkbenchImages;
import org.kalypso.contribs.eclipse.swt.widgets.ControlUtils;
import org.kalypso.core.KalypsoCoreImages;
import org.kalypso.core.KalypsoCorePlugin;
import org.kalypso.core.i18n.Messages;

/**
 * A composite, showing an {@link org.eclipse.core.runtime.IStatus}.<br>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>DETAILS, HIDE_TEXT, HIDE_DETAILS_IF_DISABLED</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * 
 * @author Gernot Belger
 */
@SuppressWarnings( "restriction" )
public class StatusComposite extends Composite
{
  /**
   * Style constant: If set, a details button is shown.
   */
  public static final int DETAILS = SWT.SEARCH;

  /**
   * Style constant: If set, the text label is hidden.
   */
  public static final int HIDE_TEXT = SWT.SIMPLE;

  /**
   * Style constant: If set, the details button is hidden, if it is disabled.
   */
  public static final int HIDE_DETAILS_IF_DISABLED = SWT.PASSWORD;

  /**
   * The form toolkit. May be null.
   */
  private final FormToolkit m_toolkit;

  private Label m_imageLabel;

  private Text m_messageText;

  private Button m_detailsButton;

  private ILabelProvider m_labelProvider;

  /**
   * The status.
   */
  private IStatus m_status;

  /**
   * The constructor.
   * 
   * @param parent
   *          The parent composite.
   * @param style
   *          The style.
   */
  public StatusComposite( final Composite parent, final int style )
  {
    this( null, parent, style );
  }

  /**
   * The constructor.
   * 
   * @param toolkit
   *          The form toolkit. May be null.
   * @param parent
   *          The parent composite.
   * @param style
   *          The style.
   */
  public StatusComposite( final FormToolkit toolkit, final Composite parent, final int style )
  {
    super( parent, style );

    m_toolkit = toolkit;
    m_imageLabel = null;
    m_messageText = null;
    m_detailsButton = null;
    m_labelProvider = null;
    m_status = null;

    if( m_toolkit != null )
      ControlUtils.adapt( this, m_toolkit );

    init( style );
  }

  /**
   * This function creates the controls.
   */
  protected void init( final int style )
  {
    /* The column count. */
    int colCount = 1;

    /* Create the image label. */
    createImageLabel();

    /* Create the message text, if it should not be hidden. */
    if( (style & HIDE_TEXT) == 0 )
    {
      colCount++;
      createMessageText();
    }

    /* Create the details button, if it is wanted. */
    if( (style & DETAILS) != 0 )
    {
      colCount++;
      createDetailsButton();
    }

    /* Set the status. */
    setStatus( m_status );

    /* Create the layout. */
    super.setLayout( GridLayoutFactory.fillDefaults().numColumns( colCount ).create() );
  }

  private void createImageLabel( )
  {
    m_imageLabel = new Label( this, SWT.NONE );
    m_imageLabel.setLayoutData( new GridData( SWT.CENTER, SWT.CENTER, false, true ) );
    m_imageLabel.addMouseListener( new MouseAdapter()
    {
      /**
       * @see org.eclipse.swt.events.MouseAdapter#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
       */
      @Override
      public void mouseDoubleClick( final MouseEvent e )
      {
        detailsButtonPressed();
      }
    } );

    if( m_toolkit != null )
      ControlUtils.adapt( m_imageLabel, m_toolkit );
  }

  private void createMessageText( )
  {
    m_messageText = new Text( this, SWT.READ_ONLY | SWT.WRAP );
    m_messageText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, true ) );
    m_messageText.addMouseListener( new MouseAdapter()
    {
      @Override
      public void mouseDoubleClick( final MouseEvent e )
      {
        detailsButtonPressed();
      }
    } );

    if( m_toolkit != null )
      ControlUtils.adapt( m_messageText, m_toolkit );
  }

  private void createDetailsButton( )
  {
    m_detailsButton = new Button( this, SWT.PUSH );
    m_detailsButton.setLayoutData( new GridData( SWT.CENTER, SWT.CENTER, false, true ) );
    m_detailsButton.setText( Messages.getString( "org.kalypso.util.swt.StatusComposite.1" ) ); //$NON-NLS-1$
    m_detailsButton.addSelectionListener( new SelectionAdapter()
    {
      /**
       * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected( final SelectionEvent e )
      {
        detailsButtonPressed();
      }
    } );

    if( m_toolkit != null )
      ControlUtils.adapt( m_detailsButton, m_toolkit );
  }

  /**
   * @see org.eclipse.swt.widgets.Control#setBackground(org.eclipse.swt.graphics.Color)
   */
  @Override
  public void setBackground( final Color color )
  {
    super.setBackground( color );

    if( m_imageLabel != null )
      m_imageLabel.setBackground( color );

    if( m_messageText != null )
      m_messageText.setBackground( color );

    if( m_detailsButton != null )
      m_detailsButton.setBackground( color );
  }

  protected void detailsButtonPressed( )
  {
    if( m_status == null )
      return;

    final StatusDialog statusTableDialog = new StatusDialog( getShell(), m_status, Messages.getString( "org.kalypso.util.swt.StatusComposite.2" ) ); //$NON-NLS-1$
    statusTableDialog.open();
  }

  /**
   * @see org.eclipse.swt.widgets.Composite#setLayout(org.eclipse.swt.widgets.Layout)
   */
  @Override
  public void setLayout( final Layout layout )
  {
    throw new UnsupportedOperationException( "The layout of this composite is fixed." ); //$NON-NLS-1$
  }

  /**
   * Sets the status of this composites and updates it to show it in the composite.
   * 
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *              </ul>
   */
  public void setStatus( final IStatus status )
  {
    m_status = status;

    if( isDisposed() )
      return;

    updateForStatus();
  }

  private void updateForStatus( )
  {
    final Image image = getStatusImage();
    final String text = getStatusText();
    final String tooltipText = getStatusTooltipText();
    final boolean enabled = getStatusIsEnabled();

    if( m_imageLabel != null )
    {
      m_imageLabel.setImage( image );
      m_imageLabel.setToolTipText( tooltipText );
    }

    if( m_messageText != null )
    {
      /* Set the text. */
      m_messageText.setText( text );

      /* Set same text as tooltip, if label is too short to hold the complete text. */
      m_messageText.setToolTipText( tooltipText );
    }

    if( m_detailsButton != null )
    {
      m_detailsButton.setEnabled( enabled );
      final boolean hideDetailsIfdisabled = (getStyle() & HIDE_DETAILS_IF_DISABLED) != 0;
      final boolean visible = !hideDetailsIfdisabled || enabled;
      m_detailsButton.setVisible( visible );
      ((GridData)m_detailsButton.getLayoutData()).exclude = !visible;
    }

    layout();
  }

  private boolean getStatusIsEnabled( )
  {
    if( m_status == null )
      return false;

    if( m_status.getException() != null )
      return true;

    return m_status.isMultiStatus();
  }

  private String getStatusText( )
  {
    if( m_status == null )
      return ""; //$NON-NLS-1$

    if( m_labelProvider != null )
    {
      final String providerText = m_labelProvider.getText( m_status );
      if( providerText != null )
        return providerText;
    }

    final String message = m_status.getMessage();
    final String message1 = message.replace( "\r\n", " " ); //$NON-NLS-1$ //$NON-NLS-2$
    final String message2 = message1.replace( "\r", " " ); //$NON-NLS-1$ //$NON-NLS-2$
    final String message3 = message2.replace( "\n", " " ); //$NON-NLS-1$ //$NON-NLS-2$
    final String message4 = message3.replace( "\t", " " ); //$NON-NLS-1$ //$NON-NLS-2$

    return message4;
  }

  private String getStatusTooltipText( )
  {
    /* Status is same as text, but null instead of empty so totally suppress the tooltip. */
    final String statusText = getStatusText();
    if( statusText == null || statusText.isEmpty() )
      return null;

    return statusText;
  }

  private Image getStatusImage( )
  {
    if( m_status == null )
      return null;

    if( m_labelProvider != null )
    {
      final Image providerImage = m_labelProvider.getImage( m_status );
      if( providerImage != null )
        return providerImage;
    }

    return getStatusImage( m_status );
  }

  public static Image getIDEImage( final String constantName )
  {
    return JFaceResources.getResources().createImageWithDefault( IDEInternalWorkbenchImages.getImageDescriptor( constantName ) );
  }

  public static Image getStatusImage( final IStatus status )
  {
    return getStatusImage( status.getSeverity() );
  }

  /**
   * Get the appropriate image for the given status severity.<br>
   * The returned images does not need to be disposed.
   */
  public static Image getStatusImage( final int severity )
  {
    switch( severity )
    {
      case IStatus.OK:
        return getOKImage();

      case IStatus.ERROR:
        return getErrorImage();

      case IStatus.WARNING:
        return getWarningImage();

      case IStatus.INFO:
        return getInfoImage();

      default:
        return null;
    }
  }

  public static ImageDescriptor getStatusImageDescriptor( final int severity )
  {
    switch( severity )
    {
      case IStatus.OK:
        return KalypsoCorePlugin.getImageProvider().getImageDescriptor( KalypsoCoreImages.DESCRIPTORS.STATUS_IMAGE_OK );

      case IStatus.ERROR:
        return IDEInternalWorkbenchImages.getImageDescriptor( IDEInternalWorkbenchImages.IMG_OBJS_ERROR_PATH );

      case IStatus.WARNING:
        return IDEInternalWorkbenchImages.getImageDescriptor( IDEInternalWorkbenchImages.IMG_OBJS_WARNING_PATH );

      case IStatus.INFO:
        return IDEInternalWorkbenchImages.getImageDescriptor( IDEInternalWorkbenchImages.IMG_OBJS_INFO_PATH );

      default:
        return null;
    }
  }

  public static Image getOKImage( )
  {
    return KalypsoCorePlugin.getImageProvider().getImage( KalypsoCoreImages.DESCRIPTORS.STATUS_IMAGE_OK );
  }

  public static Image getErrorImage( )
  {
    return getIDEImage( IDEInternalWorkbenchImages.IMG_OBJS_ERROR_PATH );
  }

  public static Image getWarningImage( )
  {
    return getIDEImage( IDEInternalWorkbenchImages.IMG_OBJS_WARNING_PATH );
  }

  public static Image getInfoImage( )
  {
    return getIDEImage( IDEInternalWorkbenchImages.IMG_OBJS_INFO_PATH );
  }

  public IStatus getStatus( )
  {
    return m_status;
  }

  public void enableButton( final boolean b )
  {
    if( m_detailsButton != null )
      m_detailsButton.setEnabled( b );
  }

  /**
   * Registers a {@link ILabelProvider} with this {@link StatusComposite}.<br>
   * If a label provider is set, it is used to show text and image of the status.<br>
   * If the label provider returns <code>null</code> text or image for a certain status, the composite will fall back to
   * its default behaviour.
   */
  public void setLabelProvider( final ILabelProvider provider )
  {
    m_labelProvider = provider;
  }
}