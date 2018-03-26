package com.alipay.util;

public class Award {
	 /**编号*/
    public String id;
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	/**概率（0.1代表10%，最多3位小数，即千分之一级）*/
//    public float probability;
    /**数量（该类奖品剩余数量）*/
    public int count;

    public Award(String id,int count) {
        super();
        this.id = id;
//        this.probability = probability;
        this.count = count;
    }

    public Award(){

    }
}
