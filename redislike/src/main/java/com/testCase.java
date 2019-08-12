package com;

import redis.clients.jedis.Jedis;

/**
 * package:com
 * Description:TODO
 * @date:2019/8/11
 * @Author:weiwei
 **/
public class testCase {
    public static void main(String[] args) {

        Jedis clinet = new Jedis();
        long begin = System.nanoTime();
        for(int i = 0;i<10;i++){
            clinet.lpush("key11","o");
            //clinet.lrange("key11",1,1);
            clinet.hset("lits11","1","2");
            clinet.hget("list11","1");
        }
        long end = System.nanoTime();
        System.out.println(end - begin);
    }
}
