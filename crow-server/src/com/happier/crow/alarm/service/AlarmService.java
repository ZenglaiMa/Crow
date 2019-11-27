package com.happier.crow.alarm.service;

import java.util.Map;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.Notification;

public class AlarmService {
	private final static String appKey = "53b243ec12325d490a94a88e";
	private final static String masterSecret = "f83d38a8c192d4f793f98f44";
	/**
	 * 发送通知
	 * @param registrationId 设备标识
	 * @param alert 推送内容
	 */
	public void jSend_notification(String registrationId, String alert, String extrasparam){
		JPushClient jpushClient = new JPushClient(masterSecret, appKey, 3);
		PushPayload payload = send_N(registrationId, alert, extrasparam);
		try {
            PushResult result = jpushClient.sendPush(payload);
            System.out.println(result + "==========");
            
        } catch (APIConnectionException e) {
            System.out.println(e);
        } catch (APIRequestException e) {
        	System.out.println(e);
        	System.out.println("Error response from JPush server. Should review and fix it. "+ e);
        	System.out.println("HTTP Status: " + e.getStatus());
        	System.out.println("Error Code: " + e.getErrorCode());
        	System.out.println("Error Message: " + e.getErrorMessage());
        	System.out.println("Msg ID: " + e.getMsgId());
        }
	}
	public PushPayload send_N(String registrationId, String alert, String extrasparam){
		return PushPayload.newBuilder()
    			.setPlatform(Platform.all())//必填    推送平台设置
    			.setAudience(Audience.registrationId(registrationId))
//    			.setNotification(Notification.alert(alert))
    			.setNotification(Notification.newBuilder()
    					.addPlatformNotification(AndroidNotification.newBuilder()
    					.setAlert(alert)
    					.addExtra("extras",extrasparam)
    					.build()).build())
    			.setOptions(Options.newBuilder()
    					.setApnsProduction(false)
    					.build())
    			.build();
		}
}
