package com.happier.crow.parent.dao;

import com.jfinal.plugin.activerecord.Model;

public class Parent extends Model<Parent> {

	private static final long serialVersionUID = 1L;

	public final static Parent dao = new Parent().dao();

}
