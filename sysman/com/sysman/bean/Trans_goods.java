package com.sysman.bean;

import java.util.List;

import com.base.bean.IDBean;

public class Trans_goods extends IDBean {
	public String no;  //编号
	public String name; //名称
	public String goods_use; //用途
	public String goods_type; //类型
	public String kind; 
	public String blemish;
	public double TRUST_PERCENT;
	public double TRANS_PERCENT;
	public double TRUST_PRICE;
	public List sys_extend;  //扩展信息
	public String create_organ_id ;
	public String create_user_id;

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGoods_use() {
		return goods_use;
	}

	public void setGoods_use(String goods_use) {
		this.goods_use = goods_use;
	}

	public String getGoods_type() {
		return goods_type;
	}

	public void setGoods_type(String goods_type) {
		this.goods_type = goods_type;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getBlemish() {
		return blemish;
	}

	public void setBlemish(String blemish) {
		this.blemish = blemish;
	}

	public double getTRUST_PERCENT() {
		return TRUST_PERCENT;
	}

	public void setTRUST_PERCENT(double tRUST_PERCENT) {
		TRUST_PERCENT = tRUST_PERCENT;
	}

	public double getTRANS_PERCENT() {
		return TRANS_PERCENT;
	}

	public void setTRANS_PERCENT(double tRANS_PERCENT) {
		TRANS_PERCENT = tRANS_PERCENT;
	}

	public double getTRUST_PRICE() {
		return TRUST_PRICE;
	}

	public void setTRUST_PRICE(double tRUST_PRICE) {
		TRUST_PRICE = tRUST_PRICE;
	}

	// public String

}
