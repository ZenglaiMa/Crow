package com.happier.crow.image.dao;

import com.jfinal.plugin.activerecord.Model;

public class Image extends Model<Image> {

	private static final long serialVersionUID = 1L;

	public final static Image dao = new Image().dao();

}
