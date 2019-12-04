package com.happier.crow.image.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.happier.crow.contact.dao.Contact;
import com.happier.crow.image.dao.Image;
import com.jfinal.core.Controller;

public class ImageController extends Controller {

	private static final int PARENT_STATUS = 0;
	private static final int CHILDREN_STATUS = 1;

	private static final int FAILURE = 0;
	private static final int SUCCESS = 1;

	public void responseChildrenRequest() {
		List<String> paths = new ArrayList<>();
		List<Image> images = null;
		int cid = getParaToInt("cid");
		images = Image.dao.find("select path from image where uploaderStatus=? and uploaderId=?", CHILDREN_STATUS, cid);
		for (Image image : images) {
			paths.add(image.getStr("path"));
		}

		List<Contact> contacts = Contact.dao.find("select addederId from contact where adderStatus=? and adderId=?",
				CHILDREN_STATUS, cid);
		for (Contact contact : contacts) {
			images = Image.dao.find("select path from image where uploaderStatus=? and uploaderId=?", PARENT_STATUS,
					contact.getStr("addederId"));
			for (Image image : images) {
				paths.add(image.getStr("path"));
			}
		}

		renderJson(paths);
	}

	public void responseParentRequest() {
		List<String> paths = new ArrayList<>();
		List<Image> images = null;
		int pid = getParaToInt("pid");
		images = Image.dao.find("select path from image where uploaderStatus=? and uploaderId=?", PARENT_STATUS, pid);
		for (Image image : images) {
			paths.add(image.getStr("path"));
		}

		List<Contact> contacts = Contact.dao.find("select addederId from contact where adderStatus=? and adderId=?",
				PARENT_STATUS, pid);
		for (Contact contact : contacts) {
			images = Image.dao.find("select path from image where uploaderStatus=? and uploaderId=?", CHILDREN_STATUS,
					contact.getStr("addederId"));
			for (Image image : images) {
				paths.add(image.getStr("path"));
			}
		}

		renderJson(paths);
	}

	public void uploadPhoto() {
		int id = Integer.parseInt(getHeader("id"));
		int status = Integer.parseInt(getHeader("status"));
		String path = "/photo/" + System.currentTimeMillis() + ".jpg";
		try {
			InputStream is = getRequest().getInputStream();
			String realPath = getRequest().getServletContext().getRealPath(path);
			FileOutputStream fos = new FileOutputStream(realPath);
			byte[] buffer = new byte[512];
			int len;
			while ((len = is.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			fos.flush();
			fos.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		boolean result = false;
		if (status == PARENT_STATUS) {
			result = new Image().set("uploaderStatus", status).set("uploaderId", id).set("path", path).save();
		} else if (status == CHILDREN_STATUS) {
			result = new Image().set("uploaderStatus", status).set("uploaderId", id).set("path", path).save();
		}
		if (result) {
			renderJson(SUCCESS);
			return;
		} else {
			renderJson(FAILURE);
			return;
		}
	}

}
