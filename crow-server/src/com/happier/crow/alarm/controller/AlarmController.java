package com.happier.crow.alarm.controller;

import java.util.List;

import com.alibaba.druid.support.logging.Log;
import com.google.gson.Gson;
import com.happier.crow.alarm.dao.Alarm;
import com.happier.crow.alarm.service.AlarmService;
import com.happier.crow.contact.dao.Contact;
import com.jfinal.core.Controller;
import com.jfinal.core.NotAction;

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
		Contact contact = Contact.dao.findFirst("select * from contact where adderStatus=? and adderId=? and remark=?", 1, cid, remark);
		int pid = Integer.parseInt(contact.getStr("addederId"));
		boolean result = new Alarm().set("type", type).set("time", time).set("state", 1).set("description", description).set("pid", pid).save();
		if (result) {
			renderJson(SUCCESS);
		} else {
			renderJson(FAILURE);
		}
		setAlarmParent(pid);
	}
	/**  
	* @Title: setAlarmParent
	* @Description: 收到子女端的设置后，服务器开始给父母端发送提醒通知
	* @param     设定文件  
	* @return void    返回类型  
	* @throws  
	*/
	@NotAction
	public void setAlarmParent(int pid) {
		List<Alarm> list = Alarm.dao.find("select * from alarm where pid=?", pid);
		System.out.println(list.toString());
		AlarmService service = new AlarmService();
		service.jSend_notification("1507bfd3f7a0cfd29bb", "这人的", new Gson().toJson(list));
	}
}
