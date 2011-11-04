//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.04.06 at 10:56:49 AM MESZ 
//

package org.kalypso.ftp.service.schema;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for FtpServiceType complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FtpServiceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="port" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="userFile" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FtpServiceType", propOrder = { "port", "userFile" })
public class FtpServiceType
{

  @XmlElement(required = true, defaultValue = "21")
  protected BigInteger port;

  @XmlElement(required = true)
  protected String userFile;

  /**
   * Gets the value of the port property.
   * 
   * @return possible object is {@link BigInteger }
   */
  public BigInteger getPort( )
  {
    return port;
  }

  /**
   * Sets the value of the port property.
   * 
   * @param value
   *          allowed object is {@link BigInteger }
   */
  public void setPort( final BigInteger value )
  {
    port = value;
  }

  /**
   * Gets the value of the userFile property.
   * 
   * @return possible object is {@link String }
   */
  public String getUserFile( )
  {
    return userFile;
  }

  /**
   * Sets the value of the userFile property.
   * 
   * @param value
   *          allowed object is {@link String }
   */
  public void setUserFile( final String value )
  {
    userFile = value;
  }

}
