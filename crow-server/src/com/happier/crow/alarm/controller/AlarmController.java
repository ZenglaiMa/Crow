package com.happier.crow.alarm.controller;

import com.alibaba.druid.support.logging.Log;
import com.happier.crow.alarm.dao.Alarm;
import com.happier.crow.alarm.service.AlarmService;
import com.happier.crow.contact.dao.Contact;
import com.jfinal.core.Controller;

public class AlarmController extends Controller {
	
	private static final int FAILURE = 0;
	private static final int SUCCESS = 1;
	/**  
	* @Title: setAlarm  
	* @Description: 子女端设置提醒
	* @param     设定文件  
	* @return void    返回类型  
	* @throws  
	*/
	public void setAlarm() {
		System.out.println("setAlarm");
		int cid = getParaToInt("cid");
		String type = getPara("type");
		String description = getPara("description");
		String remark = getPara("remark");
		String mOra = getPara("mOra");
		String hour = getPara("hour");
		String minute = getPara("minute");
		String time = mOra + "," + hour + "," + minute;
		System.out.println(remark);
		Contact contact = Contact.dao.findFirst("select * from contact where adderStatus=? and adderId=? and remark=?", 1, cid, remark);
		int pid = Integer.parseInt(contact.getStr("addederId"));
		boolean result = new Alarm().set("type", type).set("time", time).set("state", 1).set("description", description).set("pid", pid).save();
		if (result) {
			renderJson(SUCCESS);
		} else {
			renderJson(FAILURE);
		}
	}
	
	/**  
	* @Title: test  
	* @Description: 测试通知是否能发送成功，根据registerId发送的
	* @param     设定文件  
	* @return void    返回类型  
	* @throws  
	*/
	public void test() {
		AlarmService service = new AlarmService();
		service.jSend_notification("1507bfd3f7a0cfd29bb", "这人的");
	}
}
