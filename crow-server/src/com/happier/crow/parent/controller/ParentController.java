package com.happier.crow.parent.controller;

import com.happier.crow.parent.dao.Parent;
import com.happier.crow.util.EncryptionUtils;
import com.jfinal.core.Controller;

public class ParentController extends Controller {

	private static final int REGISTER_FAILURE = 0;
	private static final int REGISTER_SUCCESS = 1;

	private static final int RESET_FAILURE = 0;
	private static final int RESET_SUCCESS = 1;

	public void login() {
		String phone = getPara("phone");
		String password = getPara("password");
		password = EncryptionUtils.getMd5(password);
		Parent parent = Parent.dao.findFirst("select * from parent where phone=? and password=?", phone, password);
		if (parent != null) {
			renderJson(parent);
		} else {
			renderNull();
		}
	}

	public void register() {
		String phone = getPara("phone");
		String password = getPara("password");
		String registerId = getPara("registerId");
		password = EncryptionUtils.getMd5(password);
		boolean result = new Parent().set("phone", phone).set("password", password).set("registerId", registerId)
				.save();
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
		Parent parent = Parent.dao.findFirst("select * from parent where phone=?", phone);
		if (parent == null) {
			renderJson(RESET_FAILURE);
			return;
		}
		int id = parent.getInt("pid");
		boolean result = Parent.dao.findById(id).set("password", password).update();
		if (result) {
			renderJson(RESET_SUCCESS);
		} else {
			renderJson(RESET_FAILURE);
		}
	}

}
