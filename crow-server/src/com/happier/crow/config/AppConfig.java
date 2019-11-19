package com.happier.crow.config;

import com.happier.crow.alarm.controller.AlarmController;
import com.happier.crow.alarm.dao.Alarm;
import com.happier.crow.children.controller.ChildrenController;
import com.happier.crow.children.dao.Children;
import com.happier.crow.contact.controller.ContactController;
import com.happier.crow.contact.dao.Contact;
import com.happier.crow.image.controller.ImageController;
import com.happier.crow.image.dao.Image;
import com.happier.crow.parent.controller.ParentController;
import com.happier.crow.parent.dao.Parent;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Engine;

public class AppConfig extends JFinalConfig {

	@Override
	public void configConstant(Constants me) {
		me.setDevMode(true);
	}

	@Override
	public void configRoute(Routes me) {
		me.add("alarm", AlarmController.class);
		me.add("children", ChildrenController.class);
		me.add("contact", ContactController.class);
		me.add("image", ImageController.class);
		me.add("parent", ParentController.class);
	}

	@Override
	public void configEngine(Engine me) {
	}

	@Override
	public void configPlugin(Plugins me) {
		DruidPlugin dp = new DruidPlugin("jdbc:mysql://localhost:3306/crow?useUnicode=true&characterEncoding=utf-8",
				"root", "");
		me.add(dp);

		ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
		arp.addMapping("alarm", Alarm.class);
		arp.addMapping("children", Children.class);
		arp.addMapping("contact", Contact.class);
		arp.addMapping("image", Image.class);
		arp.addMapping("parent", Parent.class);
		arp.setDialect(new MysqlDialect());
		me.add(arp);
	}

	@Override
	public void configInterceptor(Interceptors me) {
	}

	@Override
	public void configHandler(Handlers me) {
	}

}
