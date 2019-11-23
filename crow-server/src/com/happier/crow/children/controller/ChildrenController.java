package com.happier.crow.children.controller;

import com.happier.crow.children.dao.Children;
import com.jfinal.core.Controller;

public class ChildrenController extends Controller {

	public void login() {
		String phone = getPara("phone");
		String password = getPara("password");
		Children children = Children.dao.findFirst("select * from children where phone=? and password=?", phone,
				password);
		if (children != null) {
			renderJson(children);
		} else {
			renderNull();
		}
	}

}
