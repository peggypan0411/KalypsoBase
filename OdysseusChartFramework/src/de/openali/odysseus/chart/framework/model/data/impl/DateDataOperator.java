package de.openali.odysseus.chart.framework.model.data.impl;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import de.openali.odysseus.chart.framework.exception.MalformedValueException;
import de.openali.odysseus.chart.framework.logging.impl.Logger;
import de.openali.odysseus.chart.framework.model.data.IDataRange;

public class DateDataOperator extends AbstractDataOperator<Date>
{

  private final CalendarFormat m_dateFormat;

  private static final String REGEX_DURATION = "(NOW|TODAY)(([+-])P([1-9]+[0-9]*Y)?([1-9]+[0-9]*M)?([1-9]+[0-9]*D)?(T([1-9]+[0-9]*H)?([1-9]+[0-9]*M)?([1-9]+[0-9]*S)?)?)?";

  public DateDataOperator( final Comparator<Date> comparator, final String formatString )
  {
    super( comparator );
    m_dateFormat = new CalendarFormat( formatString );
  }

  @Override
  public Long logicalToNumeric( final Date logVal )
  {
    if( logVal == null )
      return null;
    return logVal.getTime();

  }

  @Override
  public Date numericToLogical( final Number numVal )
  {
    if( numVal == null )
      return null;
    else if( numVal.longValue() < 0 )
      return null;
    return new Date( numVal.longValue() );
  }

  @Override
  public String logicalToString( final Date value )
  {
    final SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss" );
    System.out.println( sdf.format( value.getTime() ) );
    return sdf.format( value.getTime() );
  }

  @Override
  public String getFormatHint( )
  {
    return "yyyy-MM-dd (HH:mm)";
  }

  /**
   * @see org.kalypso.chart.framework.model.data.IDataOperator#getFormat(org.kalypso.chart.framework.model.data.IDataRange)
   */
  @Override
  public Format getFormat( final IDataRange<Number> range )
  {
    return m_dateFormat;
  }

  /**
   * TODO: This is a copy of CalendarStringParser; rewrite String2Calendar mapping using xml data types
   */
  @Override
  public Date stringToLogical( final String value ) throws MalformedValueException
  {
    if( value == null )
      return null;
    if( value.trim().equals( "" ) )
      return null;

    // erst mal versuchen, ein korrektes Datum zu parsen
    final Calendar cal = Calendar.getInstance();
    // cal.set( Calendar.DST_OFFSET, 0);
    // cal.set( Calendar.ZONE_OFFSET, 0);
    cal.setTimeZone( TimeZone.getTimeZone( "GMT" ) );

    SimpleDateFormat sdf = null;
    Date date = null;
    // TODO: hier ist es wohl sinnvoller, verschieden DateFormatStrings in einem Array zu halten und dann zu durchlaufen

    try
    {
      sdf = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss" );
      date = sdf.parse( value );
      // cal=Calendar.getInstance();
      if( date != null )
        cal.setTime( date );

    }
    catch( final ParseException e )
    {
      // Nochmal mit anderem String probieren
      try
      {
        sdf = new SimpleDateFormat( "yyyy-MM-dd" );
        date = sdf.parse( value );
        // cal=Calendar.getInstance();
        if( date != null )
          cal.setTime( date );
      }
      catch( final ParseException e2 )
      {
        // wir probieren mal, das �ber die anderer Syntax an das Datum zu kommen

        // Suche nach reul�rem Ausdruck f�r Duration
        if( value.matches( REGEX_DURATION ) )
        {
          // Calendar-Objekt mit "jetzt" initialisieren
          // cal=Calendar.getInstance();

          if( value.startsWith( "TODAY" ) )
          {
            cal.set( Calendar.HOUR_OF_DAY, 0 );
            cal.set( Calendar.MINUTE, 0 );
            cal.set( Calendar.SECOND, 0 );
            cal.set( Calendar.MILLISECOND, 0 );
          }

          // DurationPart auswerten
          if( (value.startsWith( "NOW" ) && (value.length() > 3)) || (value.startsWith( "TODAY" ) && (value.length() > 5)) )
          {

            final Pattern pattern = Pattern.compile( REGEX_DURATION );
            final Matcher matcher = pattern.matcher( value );

            /*
             * hier werden die ausgelesenen Werte f�r einzelnen Zeiteinheiten gespeichert; enh�lt Paare wie zB
             * (Calendar.YEAR, "5Y") erst an sp�terer Stelle werden die Werte in Integers umgewandelt
             */
            final Map<Integer, String> durationMap = new HashMap<Integer, String>();

            // Matcher initiualisieren
            matcher.matches();

            // Suche nach DurationDirection
            final String dir = matcher.group( 3 );

            // Suche nach Y, M, D, H, M, S
            final String year = matcher.group( 4 );
            durationMap.put( Calendar.YEAR, year );

            final String month = matcher.group( 5 );
            durationMap.put( Calendar.MONTH, month );

            final String day = matcher.group( 6 );
            durationMap.put( Calendar.DAY_OF_MONTH, day );

            final String hour = matcher.group( 8 );
            durationMap.put( Calendar.HOUR_OF_DAY, hour );

            final String min = matcher.group( 9 );
            durationMap.put( Calendar.MINUTE, min );

            final String sec = matcher.group( 10 );
            durationMap.put( Calendar.SECOND, sec );

            final Set<Entry<Integer, String>> entries = durationMap.entrySet();
            for( final Entry<Integer, String> entry : entries )
            {
              final String durationForUnitStr = entry.getValue();
              if( StringUtils.isNotEmpty( durationForUnitStr ) )
              {
                int durationForUnit = Integer.parseInt( durationForUnitStr.substring( 0, (durationForUnitStr.length() - 1) ) );
                // Falls die Duration negativ ist, muss der Wert negiert werden
                if( dir.equals( "-" ) ) //$NON-NLS-1$
                  durationForUnit *= -1;

                cal.add( entry.getKey(), durationForUnit );
              }
            }
          }
        }

        else
        {
          Logger.logError( Logger.TOPIC_LOG_GENERAL, "Unable to parse date: " + value );
          throw new MalformedValueException();
        }
      }
    }

    return date;
  }

}
