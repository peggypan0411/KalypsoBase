package org.kalypso.ogc.gml.loader;

import java.io.BufferedInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.kalypso.commons.java.util.PropertiesHelper;
import org.kalypso.core.util.pool.IPoolableObjectType;
import org.kalypso.loader.LoaderException;
import org.kalypso.ogc.gml.mapmodel.CommandableWorkspace;
import org.kalypso.ogc.gml.serialize.GmlSerializeException;
import org.kalypso.ogc.gml.serialize.GmlSerializer;
import org.kalypso.ogc.wfs.WFSClient;
import org.kalypso.ui.ImageProvider;
import org.kalypso.ui.KalypsoGisPlugin;
import org.kalypso.ui.internal.i18n.Messages;
import org.kalypsodeegree.KalypsoDeegreePlugin;
import org.kalypsodeegree.model.feature.FeatureVisitor;
import org.kalypsodeegree.model.feature.GMLWorkspace;
import org.kalypsodeegree_impl.model.feature.visitors.TransformVisitor;

/**
 * @author Kuepferle, v.Doemming
 */
public class WfsLoader extends WorkspaceLoader
{
  public final static String KEY_URL = "URL"; //$NON-NLS-1$

  public final static String KEY_FILTER = "FILTER"; //$NON-NLS-1$

  public final static String KEY_FEATURETYPE = "FEATURE"; //$NON-NLS-1$

  public final static String KEY_MAXFEATURE = "MAXFEATURE"; //$NON-NLS-1$

  public static final String KEY_FEATURETYPENAMESPACE = "FEATURETYPE_NAMESPACE"; //$NON-NLS-1$

  public static final int MAXFEATURE_UNBOUNDED = -1;

  public static final char KV_PAIR_SEPARATOR = '#';

  /**
   * Loads a WFS DataSource from the given URL
   * 
   * @param source
   *          the href-tag from the gmt-file 'ex: http://localhost:8080/deegreewfs#river' where river denotes the
   *          feature to be loaded
   * @param context
   *          the URL form the map context (here the path to the associated gmt file)
   */
  @Override
  protected CommandableWorkspace loadIntern( final IPoolableObjectType key, final IProgressMonitor monitor ) throws LoaderException
  {
    final String source = key.getLocation();
    final Properties sourceProps = PropertiesHelper.parseFromString( source, KV_PAIR_SEPARATOR );
    final String baseURLAsString = sourceProps.getProperty( KEY_URL );
    final String featureType = sourceProps.getProperty( KEY_FEATURETYPE );
    final String featureTypeNS = sourceProps.getProperty( KEY_FEATURETYPENAMESPACE );
    final String filter = sourceProps.getProperty( KEY_FILTER );
    final String maxFeature = sourceProps.getProperty( KEY_MAXFEATURE );

    final BufferedInputStream inputStream = null;
    final PrintStream ps = null;
    try
    {
      monitor.beginTask( "WFS laden", 1000 ); //$NON-NLS-1$

      final QName qNameFT;
      if( featureTypeNS != null && featureTypeNS.length() > 0 )
        qNameFT = new QName( featureTypeNS, featureType );
      else
        qNameFT = new QName( featureType );

      final WFSClient wfs = new WFSClient( new URL( baseURLAsString ) );
      final IStatus status = wfs.load();
      if( !status.isOK() )
        throw new LoaderException( status.getMessage(), status.getException() );

      final Integer maxFeatures = maxFeature == null || maxFeature.isEmpty() ? null : new Integer( maxFeature );
      final GMLWorkspace workspace = wfs.operationGetFeature( qNameFT, filter, maxFeatures );

      final String targetCRS = KalypsoDeegreePlugin.getDefault().getCoordinateSystem();
      workspace.accept( new TransformVisitor( targetCRS ), workspace.getRootFeature(), FeatureVisitor.DEPTH_INFINITE );

      return new CommandableWorkspace( workspace );
    }
    catch( final MalformedURLException e )
    {
      e.printStackTrace();

      // REMARK: we no not pass the exception to the next level her (hence the printStackTrace)
      // in order to have a nicer error dialog later (avoids the same line aperaring twice in the details-panel)
      final LoaderException loaderException = new LoaderException( e.getLocalizedMessage() );
      setStatus( loaderException.getStatus() );
      throw loaderException;
    }
    catch( final CoreException e )
    {
      e.printStackTrace();

      final String msg = String.format( Messages.getString( "WfsLoader.0" ), baseURLAsString ); //$NON-NLS-1$
      final MultiStatus multiStatus = new MultiStatus( KalypsoGisPlugin.getId(), -1, new IStatus[] { e.getStatus() }, msg, e );
      final LoaderException loaderException = new LoaderException( multiStatus );
      setStatus( multiStatus );
      throw loaderException;
    }
    finally
    {
      monitor.done();
      IOUtils.closeQuietly( ps );
      IOUtils.closeQuietly( inputStream );
    }
  }

  @Override
  public String getDescription( )
  {
    return "WFS Layer"; //$NON-NLS-1$
  }

  /**
   * @see org.kalypso.loader.ILoader#save(java.lang.String, java.net.URL, org.eclipse.core.runtime.IProgressMonitor, java.lang.Object)
   */
  @Override
  public void save( final IPoolableObjectType key, final IProgressMonitor monitor, final Object data )
  {
// final String source = key.getLocation();
// final URL context = key.getContext();

    // TODO implementation of a transactional WFS
    if( data instanceof CommandableWorkspace )
    {
      final Display display = new Display();
      final MessageDialog md = new MessageDialog( new Shell( display ), Messages.getString( "org.kalypso.ogc.gml.loader.WfsLoader.8" ), ImageProvider.IMAGE_STYLEEDITOR_SAVE.createImage(), Messages.getString( "org.kalypso.ogc.gml.loader.WfsLoader.9" ), MessageDialog.QUESTION, new String[] { //$NON-NLS-1$ //$NON-NLS-2$
      Messages.getString( "org.kalypso.ogc.gml.loader.WfsLoader.10" ), Messages.getString( "org.kalypso.ogc.gml.loader.WfsLoader.11" ) }, 0 ); //$NON-NLS-1$ //$NON-NLS-2$
      final int result = md.open();
      try
      {
        if( result == 0 )
        {
          final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
          final SaveAsDialog dialog = new SaveAsDialog( new Shell( display ) );
          dialog.open();
          final IPath targetPath = root.getLocation().append( dialog.getResult() );
          // IFile file = root.getFile( targetPath );
          final FileWriter fw = new FileWriter( targetPath.toFile().toString() );
          GmlSerializer.serializeWorkspace( fw, (GMLWorkspace)data );
          ResourcesPlugin.getWorkspace().getRoot().refreshLocal( IResource.DEPTH_INFINITE, monitor );
        }
        else if( result == 1 )
        {
          MessageDialog.openError( new Shell( display ), Messages.getString( "org.kalypso.ogc.gml.loader.WfsLoader.12" ), Messages.getString( "org.kalypso.ogc.gml.loader.WfsLoader.13" ) ); //$NON-NLS-1$ //$NON-NLS-2$
        }
      }
      catch( final IOException e )
      {
        e.printStackTrace();
      }
      catch( final GmlSerializeException e )
      {
        e.printStackTrace();
      }
      catch( final CoreException e )
      {
        e.printStackTrace();
      }
      finally
      {
        display.dispose();
      }
    }
  }

  /**
   * @see org.kalypso.loader.AbstractLoader#getResourcesInternal(org.kalypso.core.util.pool.IPoolableObjectType)
   */
  @Override
  public IResource[] getResourcesInternal( final IPoolableObjectType key )
  {
    return new IResource[0];
  }
}