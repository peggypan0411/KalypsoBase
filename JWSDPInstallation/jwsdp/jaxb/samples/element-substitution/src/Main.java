/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

// import java content classes generated by binding compiler
import org.example.*;

/*
 * $Id$
 *
 * Copyright 2003 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
 
public class Main {
    
    // This sample application demonstrates how to modify a java content
    // tree and marshal it back to a xml data
    
    public static void main( String[] args ) {
        try {
            // create a JAXBContext capable of handling classes generated into
            // the org.example package
            JAXBContext jc = JAXBContext.newInstance( "org.example" );
            
            // create an Unmarshaller
            Unmarshaller u = jc.createUnmarshaller();
            
            // unmarshal a po instance document into a tree of Java content
            // objects composed of classes from the primer.po package.
            Folder folder = 
                (Folder)u.unmarshal( new FileInputStream( "folder.xml" ) );

            System.out.println("Processing headers...");
            ObjectFactory of = new ObjectFactory();
	    java.util.Iterator iter = folder.getDocument().listIterator();
	    while (iter.hasNext()) {
		Document doc = (Document)iter.next();
		Header hdr = doc.getHeaderE();
		if (hdr instanceof OrderHeaderE) {
	           OrderHeaderE ohe= (OrderHeaderE)hdr;
	           System.out.println("OrderHeader info:" + 
				      ohe.getOrderInfo());
                } else if (hdr instanceof InvoiceHeaderE) {
	           InvoiceHeaderE ihe= (InvoiceHeaderE)hdr;
	           System.out.println("InvoiceHeader info:" + 
				      ihe.getInvoiceInfo());
	           OrderHeaderE ohe=of.createOrderHeaderE();
		   ohe.setOrderInfo("OVERRIDE: used to be a invoice header");
	           ohe.setGeneralComment("No");
	           doc.setHeaderE(ohe);
                } else if (hdr instanceof BidHeaderE ) {
	           BidHeaderE bhe= (BidHeaderE)hdr;
	           System.out.println("BidHeader info:" + 
				      bhe.getBidInfo());
	           OrderHeaderE ohe=of.createOrderHeaderE();
		   ohe.setOrderInfo("OVERRIDE: used to be a bid header");
	           ohe.setGeneralComment("No");
	           doc.setHeaderE(ohe);
                }
            }

            // create a Marshaller and marshal to a file
            Marshaller m = jc.createMarshaller();
            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            m.marshal( folder, System.out );
            
        } catch( JAXBException je ) {
            je.printStackTrace();
        } catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }
}
