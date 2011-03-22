package com.ambientideas.encrypt;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import junit.framework.Assert;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Strings;
import org.junit.BeforeClass;
import org.junit.Test;

import sun.misc.BASE64Encoder;

/**
 * Test the BouncyCastle SHA512 implementation
 */
public class TestBCSimpleDigest
{
    public static String DATA = "Four score and seven years ago.";
    public static byte[] KEY = "thisisak".getBytes();
    public static String SHA1_DIGEST_KNOWN_RESULT = "PC0ImQNAvA9Nf8/n5qhJqEtUD1A=";
    public static String WHIRLPOOL_DIGEST_KNOWN_RESULT = "Vp47q36YdKmVYHgVZ5MpLPJWbc829DPJ5TevTrt5Bu6IyZ7OcCyc/dNte1rt5KqrG7yAOBttItL2\n"
        +"PO026bGh0w==";
    
    @SuppressWarnings("restriction")
    public static BASE64Encoder b64e = new sun.misc.BASE64Encoder();
    
    @BeforeClass
    public static void loadBCProvider() {
        //Register Bouncy Castle JCE provider at the end of the list
        Security.addProvider(new BouncyCastleProvider());
    }
    
	@SuppressWarnings("restriction")
    @Test
    public void testBCHashDirectAPI() throws Exception {
	    byte[] utf8ByteArrayData = Strings.toUTF8ByteArray(DATA);
	    
	    Digest digest = new SHA1Digest();
        byte[] hashResultBytes = new byte[digest.getDigestSize()];
        digest.update(utf8ByteArrayData, 0, utf8ByteArrayData.length);
        digest.doFinal(hashResultBytes, 0);
        String hashResult = new String(b64e.encode(hashResultBytes));
        
        //System.out.println("Hash result: " + hashResult);
        Assert.assertEquals(SHA1_DIGEST_KNOWN_RESULT, hashResult);
    }
	
	/**
	 * Ask for SHA-1 with BouncyCastle at end of provider list. Will get
	 * the core JCE implementation instead.
	 */
	@SuppressWarnings("restriction")
    @Test
	public void testBCHashJCEAPI() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //Ask for a algorithm that both SUN and BC have and prove it is BC by the class name
        MessageDigest sha = MessageDigest.getInstance("SHA1");
        
        Assert.assertEquals("SHA1 Message Digest from SUN, <initialized>\n", sha.toString());
        Assert.assertEquals("SHA1", sha.getAlgorithm().toString());
        Assert.assertEquals("SUN version 1.6", sha.getProvider().toString());
        
        byte[] digestResultBytes = sha.digest(DATA.getBytes("UTF8"));
        String digestResultB64String = b64e.encode(digestResultBytes);
        
        //System.out.println("Hash result: " + digestResultB64String);
        Assert.assertEquals(SHA1_DIGEST_KNOWN_RESULT, digestResultB64String);
	}
	
    /**
     * Ask for SHA-1 with BouncyCastle at end of provider list. Will get
     * the core JCE implementation instead.
     */
    @SuppressWarnings("restriction")
    @Test
    public void testBCHashWhirlpoolJCEAPI() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //Register Bouncy Castle JCE provider at the end of the list
        //Security.addProvider(new BouncyCastleProvider());
        
        //Ask for a algorithm that only BC has and prove it is BC by the class name
        MessageDigest sha = MessageDigest.getInstance("Whirlpool");
        
        Assert.assertEquals("Whirlpool Message Digest from BC, <initialized>\n", sha.toString());
        Assert.assertEquals("Whirlpool", sha.getAlgorithm().toString());
        Assert.assertEquals("BC version 1.45", sha.getProvider().toString());
        
        byte[] digestResultBytes = sha.digest(DATA.getBytes("UTF8"));
        String digestResultB64String = b64e.encode(digestResultBytes);
        
        //System.out.println("Hash result: " + digestResultB64String);
        Assert.assertEquals(WHIRLPOOL_DIGEST_KNOWN_RESULT, digestResultB64String);
    }
	
	/**
     * Ask for SHA-1 with BouncyCastle at end of provider list. Will get
     * the core JCE implementation instead.
     */
    @SuppressWarnings("restriction")
    @Test
    public void testBCHashBCProviderJCEAPI() throws NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchProviderException {
        //Ask for a algorithm that both SUN and BC have and prove it is BC by the class name
        MessageDigest sha = MessageDigest.getInstance("SHA1", "BC");
        
        Assert.assertEquals("SHA-1 Message Digest from BC, <initialized>\n", sha.toString());
        Assert.assertEquals("SHA-1", sha.getAlgorithm().toString());
        Assert.assertEquals("BC version 1.45", sha.getProvider().toString());
        
        byte[] digestResultBytes = sha.digest(DATA.getBytes("UTF8"));
        String digestResultB64String = b64e.encode(digestResultBytes);
        
        //System.out.println("Hash result: " + digestResultB64String);
        Assert.assertEquals(SHA1_DIGEST_KNOWN_RESULT, digestResultB64String);
    }
    
    @Test(expected=NoSuchAlgorithmException.class)
    public void testBCAlgorithmNotFound() throws NoSuchAlgorithmException {
        MessageDigest sha = MessageDigest.getInstance("SHA9999");
    }
}
