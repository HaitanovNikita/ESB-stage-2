/*
 * Created on 09-May-2007
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
package com.ibm.wmb.sample.util;

/**
 * @author Martin Boag
 * 
 * Encoding helper
 */
public class EncodingUtils {

	/*
	 * Base 64 Encode table "b64encChar" maps the int value of the 64 base
	 * values to their character codes
	 */
	private static char[] b64encChar = new char[64];
	static {
		int i = 0;
		// 0 - 25 == A - Z
		for (char upperChar = 'A'; upperChar <= 'Z'; upperChar++) {
			b64encChar[i++] = upperChar;
		}
		// 26 - 51 == a - z
		for (char lowerChar = 'a'; lowerChar <= 'z'; lowerChar++) {
			b64encChar[i++] = lowerChar;
		}
		// 52 - 62 == 0 - 9
		for (char numChar = '0'; numChar <= '9'; numChar++) {
			b64encChar[i++] = numChar;
		}
		// 63 == +
		b64encChar[i++] = '+';
		// 64 == /
		b64encChar[i++] = '/';
	}

	/**
	 *  
	 */
	public EncodingUtils() {
		super();
		// Empty constructor stub
	}

	/**
	 * Build a HTTP Basic Auth encoding header value string from the plain text
	 * user ID and password
	 * 
	 * @param plainUserID
	 * @param plainPwd
	 * @return
	 */
	public static String encBasicAuth(String plainUserID, String plainPwd) {
		StringBuffer encCredentials = new StringBuffer();
		encCredentials.append("Basic ");
		StringBuffer plainCredentials = new StringBuffer();
		plainCredentials.append(plainUserID);
		plainCredentials.append(':');
		plainCredentials.append(plainPwd);

		// Encode the plain text cridentials in Base 64
		int inputPTxtLen = plainCredentials.length();
		// Unpadded output data length
		int unPadEncTxtLen = (inputPTxtLen * 4 + 2) / 3;
		// Padded output data length, quartets
		int inputPTxtPos = 0;
		int outputEncCharsCount = 0;
		while (inputPTxtPos < inputPTxtLen) {
			// Take 3 bytes from input, or 0 pad
			int ipByte1 = plainCredentials.charAt(inputPTxtPos++) & 0xff;
			int ipByte2 = inputPTxtPos < inputPTxtLen ? plainCredentials
					.charAt(inputPTxtPos++) & 0xff : 0;
			int ipByte3 = inputPTxtPos < inputPTxtLen ? plainCredentials
					.charAt(inputPTxtPos++) & 0xff : 0;
			// Calculte the output quartet of 6 bit nibbles
			int opNib1 = ipByte1 >>> 2; // top 6 bits of byte 1
			int opNib2 = ((ipByte1 & 0x03) << 4) | (ipByte2 >>> 4); // bot 2
																	// bits of
																	// byte 1 &
																	// top 4
																	// bits of
																	// byte 2
			int opNib3 = ((ipByte2 & 0x0f) << 2) | (ipByte3 >>> 6); // bot 4
																	// bits of
																	// byte 2 &
																	// top 2
																	// bits o
																	// fbyte 3
			int opNib4 = ipByte3 & 0x3F; // bot 6 bits of byte 3
			// Lookup encoding of first 2 output nibbles which will have a value
			encCredentials.append(b64encChar[opNib1]);
			encCredentials.append(b64encChar[opNib2]);
			outputEncCharsCount += 2;
			// Lookup encoding of last two output nibbles or pad with pad char
			// '='
			char encChar = outputEncCharsCount < unPadEncTxtLen ? b64encChar[opNib3]
					: '=';
			encCredentials.append(encChar);
			outputEncCharsCount++;
			encChar = outputEncCharsCount < unPadEncTxtLen ? b64encChar[opNib4]
					: '=';
			encCredentials.append(encChar);
			outputEncCharsCount++;
		}
		//
		return encCredentials.toString();
	}

	public static void main(String[] args) throws Exception {
		if (args.length == 3) {
			System.out.println(args[0] + " : " + args[1] + " " + args[2]);
			if ("encode".equalsIgnoreCase(args[0])) {
				String encStr = EncodingUtils.encBasicAuth(args[1], args[2]);
				System.out.println("Authentication header value: " + encStr);
			} else if ("decode".equalsIgnoreCase(args[0])) {
				//byte decBA[] = EncodingUtils.decode(args[1]);
				System.err.println("Not implemented");
			}
			System.exit(0);
		}
		System.out
				.println("Usage: java EncodingUtils decode \"base64 String\" | encode \"userID\" \"password\"");
		System.exit(1);
	}

}
