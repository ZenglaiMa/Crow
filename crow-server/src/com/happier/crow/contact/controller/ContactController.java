package com.happier.crow.contact.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.happier.crow.util.DBUtil;
import com.jfinal.core.Controller;

public class ContactController extends Controller {
	private static final int ADDSUCCESS = 1; // 添加成功
	private static final int ADDFAILED = 0; // 添加失败
	private static final int INFODIDNTEXIST = 101; // 父母表中不存在要添加的父亲信息
	private static final int INFOREPEAT = 999; // 要添加的父母信息与现有父母信息重复
	private static final int ADDPARENTS = 11; // 执行添加操作
	private static final int CONTACTNULL = 102;

	/*
	 * public void showContacts() { int id = Integer.valueOf(getPara("id")); int
	 * adderStatus = Integer.valueOf(getPara("adderStatus")); // List<Children>
	 * parentContactList = new ArrayList<>(); List<Map<String, Object>> contactList
	 * = new ArrayList<>(); Connection con = null; PreparedStatement pstm; ResultSet
	 * rs1, rs2; try { con = DBUtil.getCon(); pstm = con.
	 * prepareStatement("select * from contact where adderId=? and adderStatus=?");
	 * pstm.setInt(1, id); pstm.setInt(2, 0); rs1 = pstm.executeQuery(); while
	 * (rs1.next()) { Map<String, Object> map = new HashMap<>(); int cid =
	 * rs1.getInt(4); map.put("remark", rs1.getString(5)); pstm =
	 * con.prepareStatement("select phone from children where cid=?");
	 * pstm.setInt(1, cid); rs2 = pstm.executeQuery(); while (rs2.next()) {
	 * map.put("phone", rs2.getString(1)); } contactList.add(map);
	 * 
	 * } renderJson(contactList); System.out.println(contactList.toString());
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } }
	 */

	public void showContacts() {
		int id = Integer.valueOf(getPara("id"));
		int adderStatus = Integer.valueOf(getPara("adderStatus"));
		List<Map<String, Object>> contactList = new ArrayList<>();
		Connection con = null;
		PreparedStatement pstm;
		ResultSet rs1, rs2;
		String sql = null;
		try {
			con = DBUtil.getCon();
			pstm = con.prepareStatement("select * from contact where adderId=? and adderStatus=?");
			pstm.setInt(1, id);
			pstm.setInt(2, adderStatus);
			rs1 = pstm.executeQuery();
			while (rs1.next()) {
				Map<String, Object> map = new HashMap<>();
				int id2 = rs1.getInt(4);
				map.put("remark", rs1.getString(5));
				if (adderStatus == 1) {
					sql = "select phone from parent where pid = ?";
				} else if (adderStatus == 0) {
					sql = "select phone from children where cid=?";
				}
				pstm = con.prepareStatement(sql);
				pstm.setInt(1, id2);
				rs2 = pstm.executeQuery();
				while (rs2.next()) {
					map.put("phone", rs2.getString(1));
				}
				contactList.add(map);

			}
			renderJson(contactList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showSos() {
		int id = Integer.valueOf(getPara("id"));
		int adderStatus = Integer.valueOf(getPara("adderStatus"));
		// List<Children> parentContactList = new ArrayList<>();
		List<Map<String, Object>> contactList = new ArrayList<>();
		Connection con = null;
		PreparedStatement pstm;
		ResultSet rs1, rs2;
		try {
			con = DBUtil.getCon();
			pstm = con.prepareStatement("select * from contact where adderId=? and adderStatus=?");
			pstm.setInt(1, id);
			pstm.setInt(2, 0);
			rs1 = pstm.executeQuery();
			while (rs1.next()) {
				Map<String, Object> map = new HashMap<>();
				int cid = rs1.getInt(4);
				map.put("remark", rs1.getString(5));
				pstm = con.prepareStatement("select phone from children where cid=?");
				pstm.setInt(1, cid);
				rs2 = pstm.executeQuery();
				while (rs2.next()) {
					map.put("phone", rs2.getString(1));
				}
				contactList.add(map);
			}
			pstm = con.prepareStatement("select * from contact where adderId=? and adderStatus=? and isIce=1");
			pstm.setInt(1, id);
			pstm.setInt(2, 0);
			rs1 = pstm.executeQuery();
			while (rs1.next()) {
				Map<String, Object> map = new HashMap<>();
				int cid = rs1.getInt(4);
				map.put("remark", rs1.getString(5));
				pstm = con.prepareStatement("select phone from children where cid=?");
				pstm.setInt(1, cid);
				rs2 = pstm.executeQuery();
				while (rs2.next()) {
					map.put("phone", rs2.getString(1));
				}
				contactList.add(map);

			}
			renderJson(contactList);
			System.out.println(contactList.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addContact() {
		int adderId = Integer.valueOf(getPara("adderId"));
		String remark = getPara("remark");
		String phone = getPara("phone");
		int isIce = Integer.valueOf(getPara("isIce"));
		int addederId = 0;
		int adderStatus = Integer.valueOf(getPara("adderStatus"));
		String sql;
		Connection con = null;
		PreparedStatement pstm;
		ResultSet rs;
		try {
			con = DBUtil.getCon();
			sql = "select cid from children where phone=?";
			if (adderStatus == 1) {
				sql = "select pid from parent where phone=?";
			}
			pstm = con.prepareStatement(sql);
			pstm.setString(1, phone);
			rs = pstm.executeQuery();
			if (rs.next()) {
				addederId = rs.getInt(1);
			} else {
				renderJson(0);
				return;
			}
			if (addederId != 0) {
				if (adderStatus == 0) {
					if (isIce == 1) {
						pstm = con.prepareStatement("select isIce from contact where adderId=?");
						pstm.setInt(1, adderId);
						rs = pstm.executeQuery();
						while (rs.next()) {
							if (rs.getInt(1) == 1) {
								renderJson(666);
								return;
							}
						}
					}
					pstm = con.prepareStatement("select addederId from contact where adderId=?");
					pstm.setInt(1, adderId);
					rs = pstm.executeQuery();
					while (rs.next()) {
						if (rs.getInt(1) == addederId) {
							renderJson(INFOREPEAT);
							return;
						}
					}
				}
				pstm = con.prepareStatement(
						"insert into contact(id,adderStatus,adderId,addederId,remark,isIce) values(?,?,?,?,?,?)");
				if (addederId != 0) {
					pstm = con.prepareStatement(
							"insert into contact(id,adderStatus,adderId,addederId,remark,isIce) values(?,?,?,?,?,?)");
					pstm.setInt(1, 0);
					pstm.setInt(2, adderStatus);
					pstm.setInt(3, adderId);
					pstm.setInt(4, addederId);
					pstm.setString(5, remark);
					pstm.setInt(6, isIce);
					pstm.executeUpdate();
					renderJson(ADDSUCCESS);
				} else {
					renderJson(ADDFAILED);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * public void checkParents() { int adderId =
	 * Integer.valueOf(getPara("adderId")); String phone = getPara("phone"); int
	 * addederId = 0; int adderStatus = 1; Connection con = null; PreparedStatement
	 * pstm; ResultSet rs; int pid = 0; try { con = DBUtil.getCon(); pstm =
	 * con.prepareStatement("select pid from parent where phone=?");
	 * pstm.setString(1, phone); rs = pstm.executeQuery(); if (rs.next()) { pid =
	 * rs.getInt(1); } else { renderJson(INFODIDNTEXIST);//
	 * 鍦ㄧ埗姣嶈〃涓湭鏌ヨ鍒拌娣诲姞鐨勭埗姣嶄俊鎭垯鎶ラ敊 return; } pstm = con.
	 * prepareStatement("select addederId from contact where adderId=? and adderStatus=?"
	 * ); pstm.setInt(1, adderId); pstm.setInt(2, 1); rs = pstm.executeQuery(); if
	 * (rs.next()) { if (rs.getInt(1) == pid) { renderJson(INFOREPEAT);//
	 * 瑕佹坊鍔犵殑鐖舵瘝淇℃伅涓庣幇鏈変俊鎭噸澶� return; } else if (rs.getInt(1) != pid) {
	 * renderJson(ADDFAILED); return; } } else { renderJson(ADDPARENTS); return; } }
	 * catch (Exception e) { e.printStackTrace(); } }
	 */

	public void checkParents() {
		int adderId = Integer.valueOf(getPara("adderId"));
		String phone = getPara("phone");
		String remark = getPara("remark");
		int addederId = 0;
		int adderStatus = 1;
		Connection con = null;
		PreparedStatement pstm;
		ResultSet rs;
		int pid = 0;
		try {
			con = DBUtil.getCon();
			pstm = con.prepareStatement("select pid from parent where phone=?");
			pstm.setString(1, phone);
			rs = pstm.executeQuery();
			if (rs.next()) {
				pid = rs.getInt(1);
			} else {
				renderJson(INFODIDNTEXIST);// 在父母表中未查询到要添加的父母信息则报错，101
				return;
			}
			pstm = con.prepareStatement("select addederId from contact where adderId=? and adderStatus=? and remark=?");
			pstm.setInt(1, adderId);
			pstm.setInt(2, 1);
			pstm.setString(3, remark);
			rs = pstm.executeQuery();
			if (rs.next()) {
				if (rs.getInt(1) == pid) {
					renderJson(INFOREPEAT);// 要添加的父母信息与现有信息重复，999
					return;
				}
			} else {
				renderJson(ADDPARENTS);// 执行添加操作,11
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void searchParent() {
		int cid = Integer.valueOf(getPara("cid"));
		Connection con = null;
		PreparedStatement pstm;
		List<Map<String, Object>> phones = new ArrayList<>();
		ResultSet rs;
		ResultSet rst;
		try {
			con = DBUtil.getCon();
			pstm = con.prepareStatement("select addederId from contact where adderId=? and adderStatus=1");
			pstm.setInt(1, cid);
			rs = pstm.executeQuery();
			while (rs.next()) {
				Map<String, Object> map = new HashMap<>();
				pstm = con.prepareStatement("select gender,phone from parent where pid=?");
				pstm.setInt(1, rs.getInt(1));
				rst = pstm.executeQuery();
				while (rst.next()) {
					if (rst.getInt("gender") == 0) {
						map.put("mphone", rst.getString("phone"));
					} else if (rst.getInt("gender") == 1) {
						map.put("fphone", rst.getString("phone"));
					}
				}
				phones.add(map);
			}
			if (phones != null) {
				renderJson(phones);
				System.out.println(phones.toString());
			}
<<<<<<< HEAD
			if (parentPhone != null) {
				renderJson(parentPhone);
				System.out.println(parentPhone.toString());
			}

=======
>>>>>>> 5cb4932c11172b15c7c21b01316ea6c63de16a65
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * public void checkParents(){ int adderId =
	 * Integer.valueOf(getPara("adderId")); String fatherPhone =
	 * getPara("fatherPhone"); String matherPhone = getPara("motherPhone"); int
	 * addederId=0; int adderStatus = Integer.valueOf(getPara("adderStatus"));
	 * Connection con = null; PreparedStatement pstm; ResultSet rs; int pid=0; try{
	 * con = DBUtil.getCon(); //鏌ヨ鑱旂郴浜轰腑鏄惁宸茬粡瀛樻湁鐖朵翰淇℃伅 pstm =
	 * con.prepareStatement("select pid from parent where phone=?");
	 * pstm.setString(1, fatherPhone); rs = pstm.executeQuery(); if(rs.next()){ pid
	 * = rs.getInt(1); }else{
	 * renderJson(FATHERINFODIDNTEXIST);//鍦ㄧ埗姣嶈〃涓湭鏌ヨ鍒拌娣诲姞鐨勭埗浜蹭俊鎭垯鎶ラ敊 return; }
	 * pstm = con.
	 * prepareStatement("select addederId from contact where adderId=? and adderStatus=?"
	 * ); pstm.setInt(1,adderId); pstm.setInt(2,1); rs = pstm.executeQuery();
	 * if(rs.next()){ if(rs.getInt(1) == pid){ renderJson(INFOREPEAT); }else{
	 * renderJson(FATHEREXIST); } }else{ renderJson(FATHERDIDNTEXIST); }
	 * 
	 * //鏌ヨ鑱旂郴浜轰腑鏄惁宸茬粡瀛樻湁姣嶄翰淇℃伅 pstm =
	 * con.prepareStatement("select pid from parent where phone=?");
	 * pstm.setString(1,matherPhone); rs = pstm.executeQuery(); if(rs.next()){ pid =
	 * rs.getInt(1); }else{
	 * renderJson(MOTHERINFODIDNTEXIST);//鍦ㄧ埗姣嶈〃涓湭鏌ヨ鍒拌娣诲姞鐨勬瘝浜蹭俊鎭垯鎶ラ敊 return; }
	 * pstm = con.
	 * prepareStatement("select addederId from contact where adderId=? and adderStatus=?"
	 * ); pstm.setInt(1,adderId); pstm.setInt(2,1); rs = pstm.executeQuery();
	 * if(rs.next()){ if(rs.getInt(1) == pid){ renderJson(INFOREPEAT); }else{
	 * renderJson(MOTHEREXIST); } }else{ renderJson(MOTHERDIDNTEXIST); }
	 * 
	 * }catch (Exception e) { e.printStackTrace(); } }
	 */

}
