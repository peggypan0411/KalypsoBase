package org.kalypso.service.wps.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import net.opengeospatial.ows.GetCapabilitiesType;
import net.opengeospatial.wps.RequestBaseType;

import org.kalypso.service.ogc.IOGCService;
import org.kalypso.service.ogc.RequestBean;
import org.kalypso.service.ogc.ResponseBean;
import org.kalypso.service.ogc.exception.OWSException;
import org.kalypso.service.wps.server.operations.CancelSimulation;
import org.kalypso.service.wps.server.operations.DescribeProcessOperation;
import org.kalypso.service.wps.server.operations.DisposeSimulation;
import org.kalypso.service.wps.server.operations.ExecuteOperation;
import org.kalypso.service.wps.server.operations.GetCapabilitiesOperation;
import org.kalypso.service.wps.server.operations.IOperation;
import org.kalypso.service.wps.utils.Debug;
import org.kalypso.service.wps.utils.MarshallUtilities;

/**
 * This class implements a WPS Service.
 * 
 * @author Holger Albert
 */
public class WPSService implements IOGCService
{
  /**
   * Stores the operations.
   */
  private HashMap<String, IOperation> m_operations = null;

  /**
   * @see org.kalypso.service.ogc.IOGCService#executeOperation(org.kalypso.service.ogc.RequestBean,
   *      org.kalypso.service.ogc.ResponseBean)
   */
  public void executeOperation( RequestBean request, ResponseBean responseBean ) throws OWSException
  {
    String parameterValue = null;
    if( request.isPost() && request.getBody() != null )
    {
      try
      {
        /* POST with XML. */
        String xml = request.getBody();

        /* Check if ALL parameter are available. */
        Object object = MarshallUtilities.unmarshall( xml );

        if( object instanceof RequestBaseType )
        {
          RequestBaseType baseRequest = (RequestBaseType) object;

          /* Need the XML attribute service. */
          String service = baseRequest.getService();
          if( service == null || service.length() == 0 )
          {
            Debug.println( "Missing attribute Service!" );
            throw new OWSException( OWSException.ExceptionCode.MISSING_PARAMETER_VALUE, "Attribute 'Service' is missing ...", "Service" );
          }

          /* Need the XML attribute version. */
          String version = baseRequest.getVersion();
          if( version == null || version.length() == 0 )
          {
            Debug.println( "Missing attribute Version!" );
            throw new OWSException( OWSException.ExceptionCode.MISSING_PARAMETER_VALUE, "Attribute 'Version' is missing ...", "Version" );
          }

          /* The type of the unmarshalled object has to be the right one. */
          if( baseRequest instanceof net.opengeospatial.wps.DescribeProcess )
            parameterValue = "DescribeProcess";
          else if( baseRequest instanceof net.opengeospatial.wps.Execute )
            parameterValue = "Execute";
          else
          {
            Debug.println( "Wrong request type!" );
            throw new OWSException( OWSException.ExceptionCode.NO_APPLICABLE_CODE, "Wrong request type ...", "" );
          }
        }
        else if( object instanceof JAXBElement )
        {
          // check type of contained value
          JAXBElement< ? > element = (JAXBElement< ? >) object;
          Object value = element.getValue();
          if( value instanceof GetCapabilitiesType )
            parameterValue = "GetCapabilities";

          // TODO What, if it is another type? Throw an exception?
        }

        // TODO What, if none of this matches? Throw an exception?
      }
      catch( JAXBException e )
      {
        throw new OWSException( OWSException.ExceptionCode.NO_APPLICABLE_CODE, e.getLocalizedMessage(), "" );
      }
    }
    else
    {
      /* GET or simple POST. */

      /* Get the REQUEST parameter. With it the operation which should be executed is determined. */
      parameterValue = request.getParameterValue( "Request" );
      if( parameterValue == null )
      {
        Debug.println( "Missing parameter Request!" );
        throw new OWSException( OWSException.ExceptionCode.MISSING_PARAMETER_VALUE, "Parameter 'Request' is missing ...", "Request" );
      }
    }

    /* Initialize everything. */
    init();

    /* Check, if the operation is available. */
    Debug.println( "Searching for operation \"" + parameterValue + "\" ..." );
    IOperation operation = m_operations.get( parameterValue );
    if( operation == null )
    {
      Debug.println( "Unsupported operation \"" + parameterValue + "\"!" );
      throw new OWSException( OWSException.ExceptionCode.INVALID_PARAMETER_VALUE, "Invalid operation '" + parameterValue + "' ...", "Request" );
    }

    /* Execute the operation. */
    Debug.println( "Found operation \"" + parameterValue + "\"." );
    StringBuffer buffer = operation.executeOperation( request );

    /* Handle the response. */
    if( buffer != null )
    {
      OutputStream outputStream = responseBean.getOutputStream();
      OutputStreamWriter writer = new OutputStreamWriter( outputStream );
      try
      {
        writer.write( buffer.toString() );
        writer.close();
        writer.close();
      }
      catch( IOException e )
      {
        Debug.println( "Sending the response failed: " + e.getLocalizedMessage() );
        throw new OWSException( OWSException.ExceptionCode.NO_APPLICABLE_CODE, "Sending the response failed: " + e.getLocalizedMessage(), "" );
      }
    }
  }

  /**
   * @see org.kalypso.service.ogc.IOGCService#responsibleFor(org.kalypso.service.ogc.RequestBean)
   */
  public boolean responsibleFor( RequestBean request )
  {
    if( request.isPost() )
    {
      /* Post request. */
      String body = request.getBody();

      /* If no body is present, the wps service is not responsible. */
      if( body == null )
        return false;

      try
      {
        /* Check if the wps service could be responsible for this input. */
        Debug.println( "Checking if the WPS service is responsible for this input ..." );
        Object object = MarshallUtilities.unmarshall( body );
        // requestBase can be of any known type, needs checking
        Debug.println( "Marshalling was successfull." );

        if( object instanceof RequestBaseType )
        {
          RequestBaseType requestBase = (RequestBaseType) object;
          if( !requestBase.getService().equals( "WPS" ) )
          {
            Debug.println( "No, the request was send to another service using the same binding classes." );
            return false;
          }

          Debug.println( "Yes, it seems it is responsible for the request." );
          return true;
        }
        else if( object instanceof JAXBElement )
        {
          // check type of contained value
          JAXBElement< ? > element = (JAXBElement< ? >) object;
          Object value = element.getValue();
          if( value instanceof GetCapabilitiesType )
          {
            Debug.println( "This is a GetCababilities request. This WPS feels responsible for it." );
            return true;
          }

          Debug.println( "This is a known binding class, but it is not a valid request for this service." );
          return false;
        }

        Debug.println( "No, the request was send to another service using the same binding classes." );
        return false;
      }
      catch( JAXBException e )
      {
        Debug.println( "No, marshalling has failed: " + e.getLocalizedMessage() );
        return false;
      }
    }
    else
    {
      /* Get request. */
      String parameterValue = request.getParameterValue( "Service" );

      if( parameterValue != null && parameterValue.equals( "WPS" ) )
        return true;

      return false;
    }
  }

  /**
   * @see org.kalypso.service.ogc.IOGCService#destroy()
   */
  public void destroy( )
  {
  }

  /**
   * Initializes the wps service.
   */
  private void init( )
  {
    m_operations = new HashMap<String, IOperation>();

    /* Mandatory operations. */
    m_operations.put( "DescribeProcess", new DescribeProcessOperation() );
    m_operations.put( "Execute", new ExecuteOperation() );
    m_operations.put( "GetCapabilities", new GetCapabilitiesOperation() );

    /* Not OGC conform operations. */
    m_operations.put( "CancelSimulation", new CancelSimulation() );
    m_operations.put( "DisposeSimulation", new DisposeSimulation() );
  }
}