package com.happier.crow.alarm.dao;

import com.jfinal.plugin.activerecord.Model;

public class Alarm extends Model<Alarm> {

	private static final long serialVersionUID = 1L;

	public static final Alarm dao = new Alarm().dao();

}
