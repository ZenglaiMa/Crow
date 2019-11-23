package com.happier.crow.parent.controller;

import com.happier.crow.parent.dao.Parent;
import com.jfinal.core.Controller;

public class ParentController extends Controller {

	public void login() {
		String phone = getPara("phone");
		String password = getPara("password");
		Parent parent = Parent.dao.findFirst("select * from parent where phone=? and password=?", phone, password);
		if (parent != null) {
			renderJson(parent);
		} else {
			renderNull();
		}
	}

}
