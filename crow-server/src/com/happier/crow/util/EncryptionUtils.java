package com.happier.crow.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.tomcat.util.buf.HexUtils;

public class EncryptionUtils {

	/**
	 * @author: Seven
	 * @description: 得到某字符串的MD5值
	 */
	public static String getMd5(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return HexUtils.toHexString(md.digest(str.getBytes()));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

}
