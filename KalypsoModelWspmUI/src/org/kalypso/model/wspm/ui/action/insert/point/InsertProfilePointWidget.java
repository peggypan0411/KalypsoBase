package org.kalypso.model.wspm.ui.action.insert.point;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.kalypso.commons.command.ICommandTarget;
import org.kalypso.commons.java.lang.Objects;
import org.kalypso.jts.JTSConverter;
import org.kalypso.model.wspm.core.gml.IProfileFeature;
import org.kalypso.model.wspm.core.profil.IProfil;
import org.kalypso.model.wspm.core.profil.IProfilListener;
import org.kalypso.model.wspm.core.profil.changes.ProfilChangeHint;
import org.kalypso.model.wspm.core.profil.wrappers.IProfileRecord;
import org.kalypso.model.wspm.core.profil.wrappers.Profiles;
import org.kalypso.model.wspm.ui.action.base.ProfilePainter;
import org.kalypso.model.wspm.ui.action.selection.AbstractProfileSelectionWidget;
import org.kalypso.model.wspm.ui.action.selection.SelectedProfilesMapPanelListener;
import org.kalypso.observation.result.IInterpolationHandler;
import org.kalypso.observation.result.TupleResult;
import org.kalypso.ogc.gml.map.IMapPanel;
import org.kalypso.ogc.gml.map.widgets.advanced.utils.SLDPainter;
import org.kalypsodeegree.KalypsoDeegreePlugin;
import org.kalypsodeegree.graphics.transformation.GeoTransform;
import org.kalypsodeegree.model.geometry.GM_Exception;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.linearref.LinearLocation;
import com.vividsolutions.jts.linearref.LocationIndexedLine;

/**
 * @author Dirk Kuch
 */
public class InsertProfilePointWidget extends AbstractProfileSelectionWidget
{
  private final SelectedProfilesMapPanelListener m_mapPanelListener = new SelectedProfilesMapPanelListener( this );

  public InsertProfilePointWidget( )
  {
    super( "", "" ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  @Override
  public void activate( final ICommandTarget commandPoster, final IMapPanel mapPanel )
  {

    super.activate( commandPoster, mapPanel );

    mapPanel.addSelectionChangedListener( m_mapPanelListener );
    onSelectionChange( m_mapPanelListener.doSelection( mapPanel.getSelection() ) );

    reset();

    /* Init the cursor. */
    final Cursor cursor = Cursor.getPredefinedCursor( Cursor.CROSSHAIR_CURSOR );
    mapPanel.setCursor( cursor );
  }

  @Override
  public void finish( )
  {
    getMapPanel().removeSelectionChangedListener( m_mapPanelListener );
    onSelectionChange( new IProfileFeature[] {} ); // purge profile change listener

    reset();
    repaintMap();

    super.finish();
  }

  @Override
  public void leftPressed( final Point p )
  {
    try
    {
      final IProfileFeature profileFeature = getProfile();
      if( Objects.isNull( profileFeature ) )
        return;

      if( isVertexPoint( profileFeature.getJtsLine(), m_snapPoint ) )
        return;

      final double breite = Profiles.getWidth( getProfile().getProfil(), m_snapPoint );

      final IProfil profile = profileFeature.getProfil();

      final IProfileRecord before = profile.findPreviousPoint( breite );
      final IProfileRecord next = profile.findNextPoint( breite );
      if( Objects.isNull( before, next ) )
        return;

      final double distance = (breite - before.getBreite()) / (next.getBreite() - before.getBreite());

      final TupleResult result = profile.getResult();
      final IProfileRecord record = profile.createProfilPoint();
      final IInterpolationHandler interpolation = result.getInterpolationHandler();

      final int index = result.indexOf( before.getRecord() );
      if( interpolation.doInterpolation( result, record, index, distance ) )
        result.add( index + 1, record.getRecord() );

      final Job job = new Job( "Active point changed" )
      {
        @Override
        protected IStatus run( final IProgressMonitor monitor )
        {
          profile.getSelection().setRange( record );

          return Status.OK_STATUS;
        }
      };
      job.setSystem( true );
      job.setUser( false );

      job.schedule();
    }
    catch( final GM_Exception e )
    {
      e.printStackTrace();
    }

  }

  private com.vividsolutions.jts.geom.Point m_snapPoint;

  @Override
  public void paint( final Graphics g )
  {
    final GeoTransform projection = getMapPanel().getProjection();
    final SLDPainter painter = new SLDPainter( projection, KalypsoDeegreePlugin.getDefault().getCoordinateSystem() );

    final IProfileFeature profile = getProfile();

    ProfilePainter.paintProfilePoints( g, painter, profile );
    ProfilePainter.paintProfilePointMarkers( g, painter, profile );

    doPaintSnapPoint( g, painter );

    paintTooltip( g );
  }

  private void doPaintSnapPoint( final Graphics g, final SLDPainter painter )
  {
    try
    {
      final IProfileFeature profile = getProfile();
      if( Objects.isNull( profile ) )
        return;

      final com.vividsolutions.jts.geom.Point position = getMousePosition();
      final LineString lineString = profile.getJtsLine();

      m_snapPoint = getSnapPoint( lineString, position );
      if( isVertexPoint( lineString, m_snapPoint ) )
      {
        painter.paint( g, getClass().getResource( "symbolization/selection.snap.vertex.point.sld" ), m_snapPoint.getCoordinate() ); //$NON-NLS-1$
      }
      else
      {
        painter.paint( g, getClass().getResource( "symbolization/selection.snap.point.sld" ), m_snapPoint.getCoordinate() ); //$NON-NLS-1$
      }

    }
    catch( final Exception e )
    {
      e.printStackTrace();
    }
  }

  private boolean isVertexPoint( final LineString lineString, final com.vividsolutions.jts.geom.Point snapPoint )
  {
    final Coordinate[] coordinates = lineString.getCoordinates();
    final Coordinate coordinate = snapPoint.getCoordinate();

    return ArrayUtils.contains( coordinates, coordinate );
  }

  @Override
  public String getToolTip( )
  {
    if( Objects.isNull( getProfile(), m_snapPoint ) )
      return null;

    try
    {
      final double width = Profiles.getWidth( getProfile().getProfil(), m_snapPoint );

      return String.format( "Profilpunkt Breite: %.2f m", width );

    }
    catch( final GM_Exception e )
    {
      e.printStackTrace();
    }
    catch( final IllegalStateException e )
    {
      // point is not on line
    }

    return super.getToolTip();
  }

  private com.vividsolutions.jts.geom.Point getSnapPoint( final LineString lineString, final com.vividsolutions.jts.geom.Point position )
  {
    final LocationIndexedLine lineIndex = new LocationIndexedLine( lineString );
    final LinearLocation location = lineIndex.project( position.getCoordinate() );
    location.snapToVertex( lineString, 0.2 );

    return JTSConverter.toPoint( lineIndex.extractPoint( location ) );

  }

  @Override
  public void keyReleased( final KeyEvent e )
  {
    final int keyCode = e.getKeyCode();
    switch( keyCode )
    {
      case KeyEvent.VK_ESCAPE:
        finish();
        break;
    }
  }

  private final IProfilListener m_listener = new IProfilListener()
  {
    @Override
    public void onProfilChanged( final ProfilChangeHint hint )
    {
      if( hint.isSelectionChanged() )
        repaintMap();
    }

    @Override
    public void onProblemMarkerChanged( final IProfil source )
    {
    }
  };

}