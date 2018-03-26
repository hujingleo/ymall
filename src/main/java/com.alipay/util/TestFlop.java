package com.alipay.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestFlop {
	public static Award lottery(List<Award> awards){
		if(awards.size()==0){
			System.out.println("奖品抽完了");
		}
        //总的概率区间
        float totalPro = 0f;
        //存储每个奖品新的概率区间
        List<Float> proSection = new ArrayList<Float>();
        proSection.add(0f);
        //遍历每个奖品，设置概率区间，总的概率区间为每个概率区间的总和
        for (Award award : awards) {
            //每个概率区间为奖品概率乘以1000（把三位小数转换为整）再乘以剩余奖品数量
            totalPro += award.count;
            proSection.add(totalPro);
        }
        //获取总的概率区间中的随机数
        Random random = new Random();
        float randomPro = (float)random.nextInt((int)totalPro);
        //判断取到的随机数在哪个奖品的概率区间中
        for (int i = 0,size = proSection.size(); i < size; i++) {
            if(randomPro >= proSection.get(i) 
                && randomPro < proSection.get(i + 1)){
                return awards.get(i);
            }
        }
        return null;
    }
	
//	public static void main(String[] args) {
//        List<Award> awards = new ArrayList<Award>();
//        int rounds=1;
//        awards.add(new Award("a1",2));
//        awards.add(new Award("a2",5));
//        awards.add(new Award("a3",50));
//        awards.add(new Award("a4",33));
//        awards.add(new Award("a5",10));
//        for (int i = 0; i < 366; i++) {
//        	Award entity=lottery(awards);
//        	String id =entity.getId();
//            System.out.println("第"+i+"次抽奖"+"抽到了：" + id);
//            int count = entity.getCount()-1;
//            entity.setCount(count);
//            System.out.println(entity.getId()+"余量为"+count);
//            if (count==0) {
//            	System.out.println("祛除余量为0的"+entity.getId());
//                awards.remove(entity);
//			}
//            if (awards.size()==0) {
//    			rounds++;
//    			System.out.println("开始补充第"+rounds+"轮奖品池");
//    	        awards.add(new Award("a1",2));
//    	        awards.add(new Award("a2",5));
//    	        awards.add(new Award("a3",50));
//    	        awards.add(new Award("a4",33));
//    	        awards.add(new Award("a5",10));
//    		}
//        }
//        for (Award award : awards) {
//			System.out.println(award.getId()+"最终余量为"+award.getCount());
//		}
//        System.out.println("经过"+rounds+"轮抽奖后奖品池剩下"+awards.size());
//       
//    }
}
