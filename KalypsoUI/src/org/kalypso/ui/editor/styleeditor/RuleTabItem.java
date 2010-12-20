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
/*
 * Created on 12.07.2004
 *
 */
package org.kalypso.ui.editor.styleeditor;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.ogc.gml.IKalypsoStyle;
import org.kalypso.ogc.gml.filterdialog.dialog.FilterDialog;
import org.kalypso.ui.editor.styleeditor.dialogs.StyleEditorErrorDialog;
import org.kalypso.ui.editor.styleeditor.panels.AddSymbolizerPanel;
import org.kalypso.ui.editor.styleeditor.panels.EditSymbolizerPanel;
import org.kalypso.ui.editor.styleeditor.panels.PanelEvent;
import org.kalypso.ui.editor.styleeditor.panels.PanelListener;
import org.kalypso.ui.editor.styleeditor.panels.TextInputPanel;
import org.kalypso.ui.editor.styleeditor.panels.TextInputPanel.ModifyListener;
import org.kalypsodeegree.filterencoding.Filter;
import org.kalypsodeegree.graphics.sld.Rule;
import org.kalypsodeegree.graphics.sld.Symbolizer;

/**
 * @author F.Lindemann
 */
public class RuleTabItem
{
  private final IKalypsoStyle m_style;

  private int m_focusedSymbolizerItem = -1;

  final IFeatureType m_featureType;

  private final FormToolkit m_toolkit;

  private final Rule m_rule;

  public RuleTabItem( final Rule rule, final FormToolkit toolkit, final IKalypsoStyle style, final IFeatureType featureType )
  {
    m_toolkit = toolkit;

    m_rule = rule;
    m_style = style;
    m_featureType = featureType;
  }

  public TabItem createTabItem( final TabFolder tabFolder )
  {
    final TabItem tabItem = new TabItem( tabFolder, SWT.NULL );

    final String ruleLabel = getRuleLabel();
    tabItem.setText( ruleLabel );

    final Control content = createControl( tabFolder, tabItem );
    tabItem.setControl( content );

    return tabItem;
  }

  private Control createControl( final Composite parent, final TabItem tabItem )
  {
    final Composite composite = m_toolkit.createComposite( parent );
    final GridLayout compositeLayout = new GridLayout( 3, false );
    composite.setLayout( compositeLayout );

    final TextInputPanel rowBuilder = new TextInputPanel( m_toolkit, composite );

    /* Text Panel for Rule-Titel */
    rowBuilder.createTextRow( MessageBundle.STYLE_EDITOR_TITLE, m_rule.getTitle(), new ModifyListener()
    {
      @Override
      public String textModified( final String newValue )
      {
        if( newValue == null || newValue.trim().length() == 0 )
        {
          final StyleEditorErrorDialog errorDialog = new StyleEditorErrorDialog( composite.getShell(), MessageBundle.STYLE_EDITOR_ERROR_INVALID_INPUT, MessageBundle.STYLE_EDITOR_ERROR_NO_TITLE );
          errorDialog.showError();
          return m_rule.getTitle();
        }

        m_rule.setTitle( newValue );
        tabItem.setText( newValue );
        getUserStyle().fireStyleChanged();

        return null;
      }
    } );

    /* Text Panel for Rule-Abstract */
    rowBuilder.createTextRow( "Beschreibung", m_rule.getAbstract(), new ModifyListener() //$NON-NLS-1$
    {
      /**
       * @see org.kalypso.ui.editor.styleeditor.panels.TextInputPanel.ModifyListener#textModified(java.lang.String)
       */
      @Override
      public String textModified( final String newValue )
      {
        m_rule.setAbstract( newValue );
        getUserStyle().fireStyleChanged();
        return null;
      }
    } );

    rowBuilder.createDenominatorRow( MessageBundle.STYLE_EDITOR_MIN_DENOM, m_rule.getMinScaleDenominator(), new ModifyListener()
    {
      @Override
      public String textModified( final String newValue )
      {
        final double min = new Double( newValue );
        final double max = m_rule.getMaxScaleDenominator();
        // verify that min<=max
        if( min > max )
        {
          final StyleEditorErrorDialog errorDialog = new StyleEditorErrorDialog( composite.getShell(), MessageBundle.STYLE_EDITOR_ERROR_INVALID_INPUT, MessageBundle.STYLE_EDITOR_ERROR_MIN_DENOM_BIG );
          errorDialog.showError();
          return "" + m_rule.getMinScaleDenominator(); //$NON-NLS-1$
        }

        m_rule.setMinScaleDenominator( min );
        final Symbolizer symbolizers[] = m_rule.getSymbolizers();
        for( final Symbolizer element : symbolizers )
          element.setMinScaleDenominator( min );
        getUserStyle().fireStyleChanged();
        return null;
      }
    } );

    // max denominator cannot be 0.0 as this would imply that the min
    // denominator needs to be smaller than 0.0 -> does not make sense
    // hence, if no max denomiator specified, get the denominator of the
    // individiual symbolizer
    if( m_rule.getMaxScaleDenominator() == 0.0 )
    {
      if( m_rule.getSymbolizers().length > 0 )
        m_rule.setMaxScaleDenominator( m_rule.getSymbolizers()[0].getMaxScaleDenominator() );
      else
        m_rule.setMaxScaleDenominator( Double.MAX_VALUE );
    }

    rowBuilder.createDenominatorRow( MessageBundle.STYLE_EDITOR_MAX_DENOM, m_rule.getMaxScaleDenominator(), new ModifyListener()
    {
      @Override
      public String textModified( final String newValue )
      {
        double max = new Double( newValue );
        final double min = m_rule.getMinScaleDenominator();
        // verify that min<=max
        if( min > max )
        {
          final StyleEditorErrorDialog errorDialog = new StyleEditorErrorDialog( composite.getShell(), MessageBundle.STYLE_EDITOR_ERROR_INVALID_INPUT, MessageBundle.STYLE_EDITOR_ERROR_MAX_DENOM_SMALL );
          errorDialog.showError();
          return "" + m_rule.getMaxScaleDenominator(); //$NON-NLS-1$
        }

        // add a minimum to max in order to be a little bit larger than the
        // current scale and
        // to keep the current view -> otherwise the rule would automatically
        // Exclude this configuration
        max += 0.01;
        m_rule.setMaxScaleDenominator( max );
        final Symbolizer symbolizers[] = m_rule.getSymbolizers();
        for( final Symbolizer element : symbolizers )
          element.setMaxScaleDenominator( max );
        getUserStyle().fireStyleChanged();
        return null;
      }
    } );

// new LegendLabel( composite, m_userStyle, someIndex );

    final AddSymbolizerPanel addSymbolizerPanel = new AddSymbolizerPanel( composite, MessageBundle.STYLE_EDITOR_SYMBOLIZER, m_featureType );
    final EditSymbolizerPanel editSymbolizerPanel = new EditSymbolizerPanel( composite, m_rule.getSymbolizers().length );

    final TabFolder symbolizerTabFolder = new TabFolder( composite, SWT.NULL );
    final GridData tabData = new GridData( SWT.FILL, SWT.FILL, true, true );
    tabData.horizontalSpan = ((GridLayout) composite.getLayout()).numColumns;
    symbolizerTabFolder.setLayoutData( tabData );

    editSymbolizerPanel.addPanelListener( new PanelListener()
    {
      @Override
      public void valueChanged( final PanelEvent event )
      {
        final int action = ((EditSymbolizerPanel) event.getSource()).getAction();

        if( action == EditSymbolizerPanel.REM_SYMB )
        {
          final int index = symbolizerTabFolder.getSelectionIndex();
          if( index >= 0 )
          {
            final Symbolizer s[] = m_rule.getSymbolizers();
            m_rule.removeSymbolizer( s[index] );
            symbolizerTabFolder.getItem( index ).dispose();
            setFocusedSymbolizerItem( index );
            getUserStyle().fireStyleChanged();
          }
          drawSymbolizerTabItems( m_rule, symbolizerTabFolder );
          symbolizerTabFolder.setSelection( index - 1 );
        }
        else if( action == EditSymbolizerPanel.FOR_SYMB )
        {
          final int index = symbolizerTabFolder.getSelectionIndex();
          if( index == (m_rule.getSymbolizers().length - 1) || index < 0 )
          {
            // nothing
          }
          else
          {
            final Symbolizer newOrderedObjects[] = new Symbolizer[m_rule.getSymbolizers().length];
            for( int counter4 = 0; counter4 < m_rule.getSymbolizers().length; counter4++ )
            {
              if( counter4 == index )
                newOrderedObjects[counter4] = m_rule.getSymbolizers()[counter4 + 1];
              else if( counter4 == (index + 1) )
                newOrderedObjects[counter4] = m_rule.getSymbolizers()[counter4 - 1];
              else
                newOrderedObjects[counter4] = m_rule.getSymbolizers()[counter4];
            }
            m_rule.setSymbolizers( newOrderedObjects );
            setFocusedSymbolizerItem( index + 1 );
            getUserStyle().fireStyleChanged();
            drawSymbolizerTabItems( m_rule, symbolizerTabFolder );
            symbolizerTabFolder.setSelection( index + 1 );
          }
        }

        else if( action == EditSymbolizerPanel.BAK_SYMB )
        {
          final int index = symbolizerTabFolder.getSelectionIndex();
          if( index > 0 )
          {
            final Symbolizer newOrderedObjects[] = new Symbolizer[m_rule.getSymbolizers().length];
            for( int counter5 = 0; counter5 < m_rule.getSymbolizers().length; counter5++ )
            {
              if( counter5 == index )
                newOrderedObjects[counter5] = m_rule.getSymbolizers()[counter5 - 1];
              else if( counter5 == (index - 1) )
                newOrderedObjects[counter5] = m_rule.getSymbolizers()[counter5 + 1];
              else
                newOrderedObjects[counter5] = m_rule.getSymbolizers()[counter5];
            }
            m_rule.setSymbolizers( newOrderedObjects );
            setFocusedSymbolizerItem( index - 1 );
            getUserStyle().fireStyleChanged();
            drawSymbolizerTabItems( m_rule, symbolizerTabFolder );
            symbolizerTabFolder.setSelection( index - 1 );
          }
        }
        editSymbolizerPanel.update( m_rule.getSymbolizers().length );
      }
    } );

    addSymbolizerPanel.addPanelListener( new PanelListener()
    {
      @Override
      public void valueChanged( final PanelEvent event )
      {
        final Symbolizer symbolizer = ((AddSymbolizerPanel) event.getSource()).getSelection();
        if( symbolizer != null )
        {
          m_rule.addSymbolizer( symbolizer );
          getUserStyle().fireStyleChanged();
          setFocusedSymbolizerItem( m_rule.getSymbolizers().length - 1 );
          editSymbolizerPanel.update( m_rule.getSymbolizers().length );
          drawSymbolizerTabItems( m_rule, symbolizerTabFolder );
          symbolizerTabFolder.setSelection( m_rule.getSymbolizers().length - 1 );
        }
      }
    } );

    // ***** Button Composite
    final Composite buttonComposite = new Composite( composite, SWT.NULL );
    buttonComposite.setLayout( new GridLayout( 1, true ) );
    final Button button = new Button( buttonComposite, SWT.NULL );
    button.setText( MessageBundle.STYLE_EDITOR_EDIT_FILTER );
    button.addSelectionListener( new SelectionListener()
    {
      @Override
      public void widgetSelected( final SelectionEvent e )
      {
        final Filter oldFilter = m_rule.getFilter();
        Filter clone = null;
        if( oldFilter != null )
          try
          {
            clone = oldFilter.clone();
          }
          catch( final CloneNotSupportedException ex )
          {
            ex.printStackTrace();
          }
        final FilterDialog dialog = new FilterDialog( composite.getShell(), m_featureType, getUserStyle(), m_rule.getFilter(), null, null, false );
        final int open = dialog.open();
        if( open == Window.OK )
        {
          final Filter filter = dialog.getFilter();
          m_rule.setFilter( filter );
          getUserStyle().fireStyleChanged();
        }
        if( open == Window.CANCEL )
        {
          m_rule.setFilter( clone );
          getUserStyle().fireStyleChanged();
        }
      }

      @Override
      public void widgetDefaultSelected( final SelectionEvent e )
      {
        widgetSelected( e );
      }
    } );

    // ******* DISPLAY ALL symbolizers
    drawSymbolizerTabItems( m_rule, symbolizerTabFolder );

    if( m_rule.getSymbolizers().length == 0 )
      symbolizerTabFolder.setVisible( false );
    symbolizerTabFolder.setSelection( m_focusedSymbolizerItem );

    composite.pack( true );

    return composite;
  }

  private String getRuleLabel( )
  {
    final String title = m_rule.getTitle();
    if( title != null )
      return title;

    final String name = m_rule.getName();
    if( name != null )
      return name;

    return "<No Name>";
  }

  void drawSymbolizerTabItems( final Rule rule, final TabFolder symbolizerTabFolder )
  {
    // remove all existing items from tab folder
    final TabItem[] items = symbolizerTabFolder.getItems();
    for( int i = 0; i < items.length; i++ )
    {
      items[i].dispose();
      items[i] = null;
    }

    if( rule.getSymbolizers().length == 0 )
    {
      // add dummy invisilbe placeholder
      new SymbolizerTabItemBuilder( m_toolkit, symbolizerTabFolder, null, m_style, m_featureType );
      symbolizerTabFolder.setVisible( false );
    }
    else
    {
      for( int j = 0; j < rule.getSymbolizers().length; j++ )
        new SymbolizerTabItemBuilder( m_toolkit, symbolizerTabFolder, rule.getSymbolizers()[j], m_style, m_featureType );

      symbolizerTabFolder.pack();
      symbolizerTabFolder.setSize( 224, 287 );
      symbolizerTabFolder.setVisible( true );
    }
  }

  protected IKalypsoStyle getUserStyle( )
  {
    return m_style;
  }

  public void setFocusedSymbolizerItem( final int focusedSymbolizerItem1 )
  {
    m_focusedSymbolizerItem = focusedSymbolizerItem1;
  }
}