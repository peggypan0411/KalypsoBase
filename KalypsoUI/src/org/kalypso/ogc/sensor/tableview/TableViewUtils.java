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
package org.kalypso.ogc.sensor.tableview;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IStatus;
import org.kalypso.commons.bind.JaxbUtilities;
import org.kalypso.commons.java.util.StringUtilities;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.tableview.rules.RenderingRule;
import org.kalypso.ogc.sensor.tableview.rules.RulesFactory;
import org.kalypso.ogc.sensor.template.ObsView;
import org.kalypso.ogc.sensor.template.ObsViewItem;
import org.kalypso.ogc.sensor.template.ObsViewUtils;
import org.kalypso.template.obstableview.ObjectFactory;
import org.kalypso.template.obstableview.Obstableview;
import org.kalypso.template.obstableview.Obstableview.Rules;
import org.kalypso.template.obstableview.TypeColumn;
import org.kalypso.template.obstableview.TypeObservation;
import org.kalypso.template.obstableview.TypeRenderingRule;
import org.kalypso.ui.internal.i18n.Messages;
import org.xml.sax.InputSource;

/**
 * Observation Table Template Handling made easy
 * 
 * @author schlienger
 */
public final class TableViewUtils
{
  public final static String OTT_FILE_EXTENSION = "ott"; //$NON-NLS-1$

  private final static ObjectFactory OTT_OF = new ObjectFactory();

  private final static JAXBContext OTT_JC = JaxbUtilities.createQuiet( ObjectFactory.class );

  /**
   * Not to be instanciated
   */
  private TableViewUtils( )
  {
    // empty
  }

  /**
   * Loads the xml template from the given reader. Closes the reader.
   * 
   * @return table view template
   * @throws JAXBException
   */
  public static Obstableview loadTableTemplateXML( final Reader reader ) throws JAXBException
  {
    try
    {
      return loadTableTemplateXML( new InputSource( reader ) );
    }
    finally
    {
      IOUtils.closeQuietly( reader );
    }
  }

  /**
   * Loads the xml template from the given stream. Closes the stream.
   * 
   * @return table view template
   */
  public static Obstableview loadTableTemplateXML( final InputStream ins ) throws JAXBException
  {
    try
    {
      return loadTableTemplateXML( new InputSource( ins ) );
    }
    finally
    {
      IOUtils.closeQuietly( ins );
    }
  }

  /**
   * Loads the xml template from the given inputsource
   * 
   * @return table view template
   */
  public static Obstableview loadTableTemplateXML( final InputSource ins ) throws JAXBException
  {
    return (Obstableview)OTT_JC.createUnmarshaller().unmarshal( ins );
  }

  /**
   * Saves the given template (binding). Closes the stream.
   */
  public static void saveTableTemplateXML( final Obstableview xml, final BufferedWriter outTable ) throws JAXBException
  {
    try
    {
      final Marshaller m = JaxbUtilities.createMarshaller( OTT_JC );
      m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
      m.marshal( xml, outTable );
    }
    finally
    {
      IOUtils.closeQuietly( outTable );
    }
  }

  /**
   * Saves the given template (binding). Closes the writer.
   */
  public static void saveTableTemplateXML( final Obstableview xml, final OutputStreamWriter writer ) throws JAXBException
  {
    try
    {
      final Marshaller m = JaxbUtilities.createMarshaller( OTT_JC );
      m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
      m.setProperty( Marshaller.JAXB_ENCODING, writer.getEncoding() );
      m.marshal( xml, writer );
    }
    finally
    {
      IOUtils.closeQuietly( writer );
    }
  }

  /**
   * Builds the xml binding object using the given the table view template
   * 
   * @param context
   *          If non-<code>null</code>, all data-pathes (href to observations) will be resolved as relative pathes
   *          against this context. Only the relative pathes will be written into the xml.<br>
   *          Set to <code>null</code>, if the pathes should be written as absolute pathes.
   * @return xml binding object (ready for marshalling for instance)
   */
  public static Obstableview buildTableTemplateXML( final TableView template, final IContainer context )
  {
    final Obstableview xmlTemplate = OTT_OF.createObstableview();

    xmlTemplate.setFeatures( StringUtils.join( template.getEnabledFeatures(), ';' ) );

    xmlTemplate.setAlphaSort( template.isAlphaSort() );

    // rendering rules
    final Rules xmlRulesType = OTT_OF.createObstableviewRules();
    xmlTemplate.setRules( xmlRulesType );
    final List<TypeRenderingRule> xmlRules = xmlRulesType.getRenderingrule();

    // only set timezone if not default one
    final TimeZone timezone = template.getTimezone();
    if( timezone != null )
      xmlTemplate.setTimezone( timezone.getID() );

    final List<RenderingRule> rules = template.getRules().getRules();
    for( final RenderingRule rule : rules )
    {
      final TypeRenderingRule xmlRule = OTT_OF.createTypeRenderingRule();
      xmlRule.setMask( rule.getMask() );
      if( rule.getForegroundColor() != null )
        xmlRule.setForegroundcolor( StringUtilities.colorToString( rule.getForegroundColor() ) );
      if( rule.getBackgroundColor() != null )
        xmlRule.setBackgroundcolor( StringUtilities.colorToString( rule.getBackgroundColor() ) );
      if( rule.getFont() != null )
        xmlRule.setFont( StringUtilities.fontToString( rule.getFont() ) );
      xmlRule.setTooltip( rule.getTooltipText() );

      xmlRules.add( xmlRule );
    }

    // themes
    final List<TypeObservation> xmlObsList = xmlTemplate.getObservation();

    int colCount = 0;

    final Map<IObservation, ArrayList<ObsViewItem>> map = ObsView.mapItems( template.getItems() );

    for( final Entry<IObservation, ArrayList<ObsViewItem>> entry : map.entrySet() )
    {
      final IObservation obs = entry.getKey();
      if( obs == null )
        continue;

      final TypeObservation xmlObs = OTT_OF.createTypeObservation();

      final String href = obs.getHref();
      final String xmlHref = ObsViewUtils.makeRelativ( context, href );
      xmlObs.setHref( xmlHref );

      xmlObs.setLinktype( "zml" ); //$NON-NLS-1$

      xmlObsList.add( xmlObs );

      // columns
      final List<TypeColumn> xmlColumns = xmlObs.getColumn();

      final List<ObsViewItem> columns = entry.getValue();
      for( final ObsViewItem obsViewItem : columns )
      {
        final TableViewColumn col = (TableViewColumn)obsViewItem;

        colCount++;
        final TypeColumn xmlCol = OTT_OF.createTypeColumn();
        xmlCol.setAxis( col.getValueAxis().getName() );
        xmlCol.setEditable( col.isEditable() );
        xmlCol.setId( "c" + String.valueOf( colCount ) ); //$NON-NLS-1$
        xmlCol.setName( col.getName() );
        xmlCol.setWidth( col.getWidth() );
        xmlCol.setFormat( col.getFormat() );

        xmlColumns.add( xmlCol );
      }
    }

    return xmlTemplate;
  }

  public static IStatus applyXMLTemplate( final TableView view, final Obstableview xml, final URL context, final boolean synchron, final String ignoreHref )
  {
    view.removeAllItems();

    // features-list is optional
    if( xml.getFeatures() != null )
    {
      view.clearFeatures();

      final String[] featureNames = xml.getFeatures().split( ";" ); //$NON-NLS-1$
      for( final String element : featureNames )
        view.setFeatureEnabled( element, true );
    }

    final Boolean alphaSort = xml.isAlphaSort();
    final boolean as = alphaSort == null ? false : alphaSort.booleanValue();
    view.setAlphaSort( as );

    // timezone is optional
    final String xmlTz = xml.getTimezone();
    if( xmlTz != null && xmlTz.length() > 0 )
    {
      final TimeZone timeZone = TimeZone.getTimeZone( xmlTz );
      view.setTimezone( timeZone );
    }

    final Rules trules = xml.getRules();
    if( trules != null )
    {
      // clear the rules since we get ones from the xml
      view.getRules().removeAllRules();

      for( final Object element : trules.getRenderingrule() )
        view.getRules().addRule( RulesFactory.createRenderingRule( (TypeRenderingRule)element ) );
    }

    final List<IStatus> stati = new ArrayList<>();

    final List<TypeObservation> list = xml.getObservation();
    final TypeObservation[] tobs = list.toArray( new TypeObservation[list.size()] );
    for( int i = 0; i < tobs.length; i++ )
    {
      // check, if href is ok
      final String href = tobs[i].getHref();

      // Hack: elemente, die durch token-replace nicht richtig aufgel�st werden einfach �bergehen
      if( ignoreHref != null && href.indexOf( ignoreHref ) != -1 )
      {
        Logger.getLogger( TableViewUtils.class.getName() ).warning( Messages.getString( "org.kalypso.ogc.sensor.tableview.TableViewUtils.4" ) + href ); //$NON-NLS-1$
        continue;
      }

      final TableViewColumnXMLLoader loader = new TableViewColumnXMLLoader( view, tobs[i], context, synchron, i );
      stati.add( loader.getResult() );
    }

    return StatusUtilities.createStatus( stati, Messages.getString( "org.kalypso.ogc.sensor.tableview.TableViewUtils.5" ) ); //$NON-NLS-1$
  }

  public static IObservation[] getObservations( final TableViewColumn[] tableViewColumns )
  {
    final Set<IObservation> map = new HashSet<>();
    for( final TableViewColumn col : tableViewColumns )
      map.add( col.getObservation() );

    return map.toArray( new IObservation[map.size()] );
  }
}