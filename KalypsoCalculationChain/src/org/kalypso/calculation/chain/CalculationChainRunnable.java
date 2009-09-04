package org.kalypso.calculation.chain;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.kalypso.contribs.eclipse.jface.operation.ICoreRunnableWithProgress;
import org.kalypso.simulation.core.refactoring.ISimulationRunner;
import org.kalypso.simulation.core.refactoring.SimulationRunnerFactory;
import org.kalypso.simulation.core.simspec.Modeldata;
import org.kalypso.simulation.core.simspec.Modeldata.Input;
import org.kalypso.simulation.core.simspec.Modeldata.Output;

public class CalculationChainRunnable implements ICoreRunnableWithProgress
{

  public static enum CHAIN_STATUS
  {
    INIT,
    RUNNING,
    FINISHED
  }

  private final List<CalculationChainMemberJobSpecification> m_jobSpecificationList = new ArrayList<CalculationChainMemberJobSpecification>();

  private CHAIN_STATUS m_chainStatus;

  private final URL m_context;

  public CalculationChainRunnable( final URL context )
  {
    m_context = context;

    m_chainStatus = CHAIN_STATUS.INIT;
  }

  public CalculationChainRunnable( final List<CalculationChainMemberJobSpecification> jobSpecificationList, final URL context )
  {
    this( context );
    m_jobSpecificationList.addAll( jobSpecificationList );
  }

  public void initialize( )
  {
    if( m_chainStatus.compareTo( CHAIN_STATUS.RUNNING ) != 0 )
    {
      m_chainStatus = CHAIN_STATUS.INIT;
      m_jobSpecificationList.clear();
    }
  }

  public void addJob( final CalculationChainMemberJobSpecification jobSpecification )
  {
    m_jobSpecificationList.add( jobSpecification );
  }

  public void addJob( final int index, final CalculationChainMemberJobSpecification jobSpecification )
  {
    m_jobSpecificationList.add( index, jobSpecification );
  }

  @Override
  public IStatus execute( final IProgressMonitor monitor ) throws CoreException, InvocationTargetException, InterruptedException
  {
    m_chainStatus = CHAIN_STATUS.RUNNING;
    IStatus status = Status.OK_STATUS;
    try
    {
      for( final CalculationChainMemberJobSpecification job : m_jobSpecificationList )
      {
        if( status.isOK() )
        {
          System.out.println( String.format( "Starting calc job: %s", job.getCalculationTypeID() ) );

          final IPath workspace = job.getContainer();
          final IResource workspaceResource = ResourcesPlugin.getWorkspace().getRoot().findMember( workspace );

          URL context;
          if( m_context.toString().startsWith( "platform:/resource//" ) )
          {
            // local processing - project workspace
            context = workspaceResource.getLocationURI().toURL();
          }
          else
          {
            // wps remote "local" processing - m_context points to tmpDir
            context = m_context;
          }

          final Modeldata modeldata = job.getModeldata( m_context );

          final Map<String, Object> inputs = resolveInputs( modeldata.getInput() );
          final List<String> outputs = resolveOutputs( modeldata.getOutput() );

          final ISimulationRunner runner = SimulationRunnerFactory.createRunner( job.getCalculationTypeID(), modeldata, context );
          runner.run( inputs, outputs, monitor );

          workspaceResource.refreshLocal( IResource.DEPTH_INFINITE, new NullProgressMonitor() );
        }
      }
    }
    catch( final Exception e )
    {
      System.out.println( "ERROR: " + e.getLocalizedMessage() );
      e.printStackTrace();
      // ErrorDialog.openError(shell, "Error",
      // e.getLocalizedMessage(), Status.CANCEL_STATUS);
      status = Status.CANCEL_STATUS;
    }
    m_chainStatus = CHAIN_STATUS.FINISHED;
    return status;
  }

  private List<String> resolveOutputs( final List<Output> output )
  {
    final List<String> myOutputs = new ArrayList<String>();
    for( final Output o : output )
    {
      myOutputs.add( o.getId() );
    }

    return myOutputs;
  }

  private Map<String, Object> resolveInputs( final List<Input> input )
  {
    final Map<String, Object> myInputs = new HashMap<String, Object>();

    for( final Input i : input )
    {
      myInputs.put( i.getId(), i.getPath() );
    }

    return myInputs;
  }
}
