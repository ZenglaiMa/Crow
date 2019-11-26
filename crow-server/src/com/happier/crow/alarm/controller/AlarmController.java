package com.happier.crow.alarm.controller;

import com.happier.crow.alarm.service.AlarmService;
import com.jfinal.core.Controller;

public class AlarmController extends Controller {
	
	public void test() {
		System.out.println("hhhhh");
		AlarmService service = new AlarmService();
		service.jSend_notification("1507bfd3f7a0cfd29bb", "这人的");
	}
}
