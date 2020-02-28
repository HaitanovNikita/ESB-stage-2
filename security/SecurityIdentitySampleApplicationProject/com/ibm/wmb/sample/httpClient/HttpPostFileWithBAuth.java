/**
 * Created on    : 9 May 2007
 *
 * Author        : Martin Boag
 *
 * Sample program for use with WebSphere Message Broker Product
 *  ProgIds: 5724-J06 5724-J05 5724-J04 5697-J09 5655-M74 5655-M75 5648-C63
 * Copyright 2007 by International Business Machines Corp.
 * All Rights Reserved * Licensed Materials - Property of IBM
 *
 * This sample program is provided AS IS and may be used, executed,
 * copied and modified without royalty payment by customer
 *
 * (a) for its own instruction and study,
 * (b) in order to develop applications designed to run with an IBM
 *     WebSphere product, either for customer's own internal use or for
 *     redistribution by customer, as part of such an application, in
 *     customer's own products.
 *
 * Copyright 2007 by International Business Machines Corp.
 */

package com.ibm.wmb.sample.httpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.ibm.wmb.sample.util.EncodingUtils;

/**
 * @author Martin Boag
 * 
 * HttpPostFileWithBAuth - using standard Java packages
 * java.net.HttpURLConnection
 * 
 */
public class HttpPostFileWithBAuth {

    /**
     * 
     */
    public HttpPostFileWithBAuth() {
        // Constructor stub
    }

    /**
     * Performs a single HTTP Post with preemptive BasicAuth
     * 
     * @param args
     */
    public static void main(String[] args) {
        if (args.length > 6 || args.length < 4) {
            System.out
                    .println("Usage: java BasicAuthHttpPost <URLHost> <URLPort> <URLPathSuffix> <XML file> [<UserId> <Password> ]");
            System.exit(1);
        }

        // Get target URL
        String strURLHost = args[0];
        String strURLPort = args[1];
        String strURLPathSuffix = args[2];
        String strURL = "http://" + strURLHost + ":" + strURLPort
                + strURLPathSuffix;
        // Get file to be posted
        String strXMLFilename = args[3];
        File fileInput = new File(strXMLFilename);

        // Optional Basic Auth
        String strAthHdval = null;
        if (args.length == 6) {
            // Get the Base64 encoded authentication header value
            strAthHdval = EncodingUtils.encBasicAuth(args[4], args[5]);
        }

        try {
            // Build the POST request
            URL url = new URL(strURL);
            HttpURLConnection connect = (HttpURLConnection) url
                    .openConnection();
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);
            // Set the content type property as UFT-8 encoded XML
            connect
                    .setRequestProperty("ContentType",
                            "text/xml; charset=UTF-8");
            if (strAthHdval != null) {
                System.out.println("Send basic auth header for " + args[4]);
                // Specify Basic Auth preemtively - ie always send it without
                // chalenge
                connect.setRequestProperty("Authorization", strAthHdval);
            }
            // Specify SOAPAction etc...
            connect.setRequestProperty("SOAPAction", "");

            // Request Input
            OutputStream reqOS = connect.getOutputStream();
            InputStream is = new FileInputStream(fileInput);
            byte[] buf = new byte[1024];
            int c;
            while ((c = is.read(buf)) > -1) {
                reqOS.write(buf, 0, c);
            }
            System.out.println("POST: " + url.toString());
            connect.connect();
            System.out.println("Result: ");
            BufferedReader bis = new BufferedReader(new InputStreamReader(
                    connect.getInputStream()));
            String line;
            while ((line = bis.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            System.err.println("Failed: " + e.toString());
            e.printStackTrace();
        }
    }
}
