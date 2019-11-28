package com.happier.crow.children.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.happier.crow.children.dao.Children;
import com.happier.crow.parent.dao.Parent;
import com.happier.crow.util.EncryptionUtils;
import com.jfinal.core.Controller;

public class ChildrenController extends Controller {

	private static final int FAILURE = 0;
	private static final int SUCCESS = 1;

	public void login() {
		String phone = getPara("phone");
		String password = getPara("password");
		password = EncryptionUtils.getMd5(password);
		Children children = Children.dao.findFirst("select * from children where phone=? and password=?", phone,
				password);
		if (children != null) {
			renderJson(children);
		} else {
			renderNull();
		}
	}

	public void register() {
		String phone = getPara("phone");
		String password = getPara("password");
		password = EncryptionUtils.getMd5(password);
		boolean result = new Children().set("phone", phone).set("password", password).save();
		if (result) {
			renderJson(SUCCESS);
		} else {
			renderJson(FAILURE);
		}
	}

	public void resetPassword() {
		String phone = getPara("phone");
		String password = getPara("password");
		password = EncryptionUtils.getMd5(password);
		Children children = Children.dao.findFirst("select * from children where phone=?", phone);
		if (children == null) {
			renderJson(FAILURE);
			return;
		}
		int id = children.getInt("cid");
		boolean result = Children.dao.findById(id).set("password", password).update();
		if (result) {
			renderJson(SUCCESS);
		} else {
			renderJson(FAILURE);
		}
	}

	public void setInfo() {
		int id = Integer.parseInt(getHeader("cid"));
		String name = getPara("name");
		int age = getParaToInt("age");
		int gender = getParaToInt("gender");
		String profile = getPara("profile");
		boolean result = Children.dao.findById(id).set("name", name).set("gender", gender).set("age", age)
				.set("profile", profile).update();
		if (result) {
			renderJson(SUCCESS);
		} else {
			renderJson(FAILURE);
		}
	}

	public void uploadHeaderImage() {
		int id = Integer.parseInt(getHeader("cid"));
		String path = "/header-image/" + System.currentTimeMillis() + ".jpg";
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

		boolean result = Children.dao.findById(id).set("iconPath", path).update();
		if (result) {
			renderJson(SUCCESS);
		} else {
			renderJson(FAILURE);
		}
	}

}
