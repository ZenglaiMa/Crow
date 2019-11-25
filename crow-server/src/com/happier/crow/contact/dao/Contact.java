package com.happier.crow.contact.dao;

import com.jfinal.plugin.activerecord.Model;

public class Contact extends Model<Contact> {

	private static final long serialVersionUID = 1L;

	public static final Contact dao = new Contact().dao();

}
