package com.happier.crow.children.dao;

import com.jfinal.plugin.activerecord.Model;

public class Children extends Model<Children> {

	private static final long serialVersionUID = 1L;

	public static final Children dao = new Children().dao();

}
