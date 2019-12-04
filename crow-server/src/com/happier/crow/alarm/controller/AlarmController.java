package com.happier.crow.alarm.controller;

import java.util.List;

import com.google.gson.Gson;
import com.happier.crow.alarm.dao.Alarm;
import com.happier.crow.alarm.service.AlarmService;
import com.happier.crow.contact.dao.Contact;
import com.happier.crow.parent.dao.Parent;
import com.jfinal.core.Controller;
import com.jfinal.core.NotAction;

public class AlarmController extends Controller {
	
	private static final int FAILURE = 0;
	private static final int SUCCESS = 1;
	/**  
	* @Title: setAlarm  
	* @Description: 子女端为父母设置提醒
	* @param     设定文件  
	* @return void    返回类型  
	* @throws  
	*/
	public void setAlarmChildren() {
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
		sendAlarm2Parent(pid);
	}
	
	/**  
	* @Title: sendAlarm2Parent  
	* @Description: 收到子女端的设置后，服务器开始给父母端发送提醒通知
	* @param @param pid 父母端的id
	* @return void    返回类型  
	* @throws  
	*/
	@NotAction
	public void sendAlarm2Parent(int pid) {
		List<Alarm> list = Alarm.dao.find("select * from alarm where pid=?", pid);
		AlarmService service = new AlarmService();
		String registerId = getRegisterId(pid);
		service.jSend_notification(registerId, "收到了您的子女设置的一条提醒", new Gson().toJson(list));
	}
	/**  
	* @Title: getRegisterId  
	* @Description: 查询registerId
	* @param @param 父母端的id
	* @return String 返回registerId
	* @throws  
	*/
	@NotAction
	public String getRegisterId(int pid) {
		Parent parent = Parent.dao.findById(pid);
		System.out.println(parent.getStr("registerId"));
		return parent.getStr("registerId");
	}
	/**  
	* @Title: setAlarmParent  
	* @Description: 父母端自己设置提醒
	* @param     设定文件  
	* @return void    返回类型  
	* @throws  
	*/
	public void setAlarmParent() {
		System.out.println("setAlarmParent");
		int pid = getParaToInt("pid");
		String type = getPara("type");
		String description = getPara("description");
		String mOra = getPara("mOra");
		String hour = getPara("hour");
		String minute = getPara("minute");
		String time = mOra + "," + hour + "," + minute;
		boolean result = new Alarm().set("type", type).set("time", time).set("state", 1).set("description", description).set("pid", pid).save();
		if (result) {
			renderJson(SUCCESS);
		} else {
			renderJson(FAILURE);
		}
	}
	/**
	* @Title: getAllAlarm  
	* @Description: 父母端查询自己所以的提醒
	* @param @param pid
	* @return void    返回类型  
	* @throws  
	*/
	public void getAlarmParent() {
		System.out.println("getAlarmParent");
		int pid = getParaToInt("pid");
		List<Alarm> list = Alarm.dao.find("select * from alarm where pid=?", pid);
		System.out.println(new Gson().toJson(list));
		renderJson(new Gson().toJson(list));
	}
	/**  
	* @Title: changeState  
	* @Description: 更改闹钟状态
	* @param     设定文件  
	* @return void    返回类型  
	* @throws  
	*/
	public void changeState() {
		System.out.println("changeState");
		int id = getParaToInt("id");
		int state = getParaToInt("state");
		boolean result = Alarm.dao.findById(id).set("state", state).update();
	}
}
