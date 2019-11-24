package com.happier.crow.children.controller;

import com.happier.crow.children.dao.Children;
import com.happier.crow.util.EncryptionUtils;
import com.jfinal.core.Controller;

public class ChildrenController extends Controller {

	private static final int REGISTER_FAILURE = 0;
	private static final int REGISTER_SUCCESS = 1;

	private static final int RESET_FAILURE = 0;
	private static final int RESET_SUCCESS = 1;

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
			renderJson(REGISTER_SUCCESS);
		} else {
			renderJson(REGISTER_FAILURE);
		}
	}

	public void resetPassword() {
		String phone = getPara("phone");
		String password = getPara("password");
		password = EncryptionUtils.getMd5(password);
		Children children = Children.dao.findFirst("select * from children where phone=?", phone);
		if (children == null) {
			renderJson(RESET_FAILURE);
			return;
		}
		int id = children.getInt("cid");
		boolean result = Children.dao.findById(id).set("password", password).update();
		if (result) {
			renderJson(RESET_SUCCESS);
		} else {
			renderJson(RESET_FAILURE);
		}
	}

}
