package com.happier.crow.parent.controller;

import com.happier.crow.parent.dao.Parent;
import com.happier.crow.util.EncryptionUtils;
import com.jfinal.core.Controller;

public class ParentController extends Controller {

	private static final int FAILURE = 0;
	private static final int SUCCESS = 1;

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
			renderJson(SUCCESS);
		} else {
			renderJson(FAILURE);
		}
	}

	public void resetPassword() {
		String phone = getPara("phone");
		String password = getPara("password");
		password = EncryptionUtils.getMd5(password);
		Parent parent = Parent.dao.findFirst("select * from parent where phone=?", phone);
		if (parent == null) {
			renderJson(FAILURE);
			return;
		}
		int id = parent.getInt("pid");
		boolean result = Parent.dao.findById(id).set("password", password).update();
		if (result) {
			renderJson(SUCCESS);
		} else {
			renderJson(FAILURE);
		}
	}

	public void getInfo() {
		int pid = getParaToInt("pid");
		Parent parent = Parent.dao.findById(pid);
		renderJson(parent);
	}

	public void setInfo() {
		int id = getParaToInt("pid");
		String name = getPara("name");
		int gender = getParaToInt("gender");
		int age = getParaToInt("age");
		String province = getPara("province");
		String city = getPara("city");
		String area = getPara("area");
		String detailAddress = getPara("detailAddress");

		boolean result = Parent.dao.findById(id).set("name", name).set("gender", gender).set("age", age)
				.set("province", province).set("city", city).set("area", area).set("detailAddress", detailAddress)
				.update();
		if (result) {
			renderJson(SUCCESS);
		} else {
			renderJson(FAILURE);
		}
	}
}
