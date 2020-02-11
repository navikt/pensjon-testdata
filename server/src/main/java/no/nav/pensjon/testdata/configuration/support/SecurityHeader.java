package no.nav.pensjon.testdata.configuration.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import java.io.UnsupportedEncodingException;

public class SecurityHeader {

    /**
     * SecurityHeader.
     *
     * @author MA
     */

    private static final String PASSWORD_TYPE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText";
    private static final String SECURITY_URL = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    private static final String SOAP_ENC_URL = "http://schemas.xmlsoap.org/soap/encoding/";
    private static final String SOAP_ENV_URL = "http://schemas.xmlsoap.org/soap/envelope/";
    private static final String XML_SCHEMA_URL = "http://www.w3.org/2001/XMLSchema";
    private static final String XML_SCHEMA_INS_URL = "http://www.w3.org/2001/XMLSchema-instance";

    private static final Log LOG = LogFactory.getLog(SecurityHeader.class);

    private static final String WSSE = "wsse";

    /**
     * Creates a new instance of SecurityHeader.
     */
    protected SecurityHeader() {
    }


    /**
     * Creates and returns a new basic authentication based SOAP header.
     *
     * @param user     the username
     * @param password the password
     * @return SOAPElement new SOAP header with basic authentication embedded
     */
    public static SOAPElement createBasicAuth(String user, String password) {

        // Make sure we have the credentials
        if ((user == null) || (password == null)) {
            return null;
        }

        if (password.startsWith("{")) {
            password = decode(password);
        }

        // Now try to build the header
        SOAPElement header = null;
        try {
            SOAPFactory sFactory = SOAPFactory.newInstance();

            // Create header Element
            header = createHeaderElement(sFactory);

            // Create and add userName token Element
            Name userTokenName = sFactory.createName("UsernameToken", WSSE, SECURITY_URL);
            SOAPElement userToken = sFactory.createElement(userTokenName);

            // Populate token

            Name userElementName = sFactory.createName("Username", WSSE, SECURITY_URL);
            Name passwordElementName = sFactory.createName("Password", WSSE, SECURITY_URL);

            SOAPElement userElement = sFactory.createElement(userElementName);
            userElement.addTextNode(user);
            SOAPElement passwordElement = sFactory.createElement(passwordElementName);
            Name attrName = sFactory.createName("Type");
            passwordElement.addAttribute(attrName, PASSWORD_TYPE);
            passwordElement.addTextNode(password);

            userToken.addChildElement(userElement);
            userToken.addChildElement(passwordElement);

            header.addChildElement(userToken);
        } catch (Exception e) {
            LOG.error("Error building Basic Auth security header", e);
        }
        return header;
    }

    /**
     * Creates the SOAP header.
     *
     * @return header
     * @throws SOAPException Soap exception
     */
    private static SOAPElement createHeaderElement(SOAPFactory sFactory) throws SOAPException {
        Name headerName = sFactory.createName("Security", WSSE, SECURITY_URL);
        SOAPElement header = sFactory.createElement(headerName);
        header.addNamespaceDeclaration("soapenc", SOAP_ENC_URL);
        header.addNamespaceDeclaration("xsd", XML_SCHEMA_URL);
        header.addNamespaceDeclaration("xsi", XML_SCHEMA_INS_URL);
        Name mustName = sFactory.createName("mustUnderstand", "soapenv", SOAP_ENV_URL);
        header.addAttribute(mustName, "1");

        return header;
    }


    /**
     * Decodes an XOR-encoded string.
     *
     * @param password XOR-encoded password
     * @return The decoded version of the XOR-encoded password
     */
    public static String decode(String password) {
        String crypt = password.substring(1, 4);
        if (crypt.equalsIgnoreCase("xor")) {
            String codedPass = password.substring(5);
            byte[] decryptedBytes = xor(convertViewableToBytes(codedPass));
            return convertToString(decryptedBytes);
        } else {
            throw new RuntimeException("Unknown cryptmethod " + crypt + " for password " + password);
        }
    }

    /**
     * exclusive or on a byte array.
     *
     * @param abyte0 the bytes to xor
     * @return a result array
     */
    private static byte[] xor(byte[] abyte0) {
        byte[] abyte1 = null;
        if (abyte0 != null) {
            abyte1 = new byte[abyte0.length];
            for (int i = 0; i < abyte0.length; i++) {
                abyte1[i] = (byte) (95 ^ abyte0[i]);
            }
        }
        return abyte1;
    }

    /**
     * convert viewable to bytes.
     *
     * @param s viewables.
     * @return bytes
     */
    private static byte[] convertViewableToBytes(String s) {
        byte[] abyte0 = null;
        if (s != null) {
            if (s.length() == 0) {
                abyte0 = EMPTY_BYTE_ARRAY;
            } else {
                try {
                    abyte0 = base64Decode(convertToBytes(s));
                } catch (Exception ex) {
                    LOG.error("Failed to decode " + s + ".", ex);
                    abyte0 = null;
                }
            }
        }
        return abyte0;
    }

    /**
     * Convert bytes to string.
     *
     * @param abyte0 bytes
     * @return string
     */
    private static String convertToString(byte[] abyte0) {
        String s = null;
        if (abyte0 != null) {
            if (abyte0.length == 0) {
                s = "";
            } else {
                try {
                    s = new String(abyte0, "UTF8");
                } catch (UnsupportedEncodingException ex) {
                    // The byte array has been converted from a string with UTF8 encoding
                    // if the encoding was not supported, the previous convert operation would fail and
                    // return a null as the converted string
                    LOG.error("This exception cannot be thrown!", ex);
                    s = null;
                }
            }
        }
        return s;
    }

    /**
     * Convert string to bytes.
     *
     * @param s string
     * @return bytes
     */
    private static byte[] convertToBytes(String s) {
        byte[] abyte0 = null;
        if (s != null) {
            if (s.length() == 0) {
                abyte0 = EMPTY_BYTE_ARRAY;

            } else {
                try {
                    abyte0 = s.getBytes("UTF8");
                } catch (UnsupportedEncodingException ex) {
                    LOG.error("The UTF8 encoding is not supported.", ex);
                    abyte0 = null;
                }
            }
        }
        return abyte0;
    }

    /**
     * base 64 decode.
     *
     * @param abyte0 bytes
     * @return decoded bytes
     */
    private static byte[] base64Decode(byte[] abyte0) {
        int i;
        for (i = abyte0.length; abyte0[--i] == 61; ) {
            ;
        }
        byte[] abyte1 = new byte[(i + 1) - abyte0.length / 4];
        for (int j = 0; j < abyte0.length; j++) {
            abyte0[j] = BASE64_DECODE_MAP[abyte0[j]];
        }
        int k = abyte1.length - 2;
        int l = 0;
        int i1;
        for (i1 = 0; l < k; i1 += 4) {
            abyte1[l] = (byte) (abyte0[i1] << 2 & 255 | abyte0[i1 + 1] >>> 4 & 3);
            abyte1[l + 1] = (byte) (abyte0[i1 + 1] << 4 & 255 | abyte0[i1 + 2] >>> 2 & 15);
            abyte1[l + 2] = (byte) (abyte0[i1 + 2] << 6 & 255 | abyte0[i1 + 3] & 63);
            l += 3;
        }
        if (l < abyte1.length) {
            abyte1[l++] = (byte) (abyte0[i1] << 2 & 255 | abyte0[i1 + 1] >>> 4 & 3);
            if (l < abyte1.length) {
                abyte1[l] = (byte) (abyte0[i1 + 1] << 4 & 255 | abyte0[i1 + 2] >>> 2 & 15);
            }
        }
        return abyte1;
    }

    /**
     * An empty array.
     */
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private static final byte[] BASE64_ENCODE_MAP;
    private static final byte[] BASE64_DECODE_MAP;

    static {

        byte[] abyte0 = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89,
                90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118,
                119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
        BASE64_ENCODE_MAP = abyte0;
        BASE64_DECODE_MAP = new byte[128];
        for (int i = 0; i < BASE64_DECODE_MAP.length; i++) {
            BASE64_DECODE_MAP[i] = -1;
        }
        for (int j = 0; j < BASE64_ENCODE_MAP.length; j++) {
            BASE64_DECODE_MAP[BASE64_ENCODE_MAP[j]] = (byte) j;
        }
    }
}
