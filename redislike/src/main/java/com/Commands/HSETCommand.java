package com.Commands;

import com.Command;
import com.Database;
import com.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * package:com.Commands
 * Description:TODO
 *
 * @date:2019/8/9 0009
 * @Author:weiwei
 **/
public class HSETCommand implements Command {
    private static final Logger logger = LoggerFactory.getLogger(HSETCommand.class);
    private List<Object> args;

    @Override
    public void setArgs(List<Object> args) {
        this.args = args;
    }

    @Override
    public void run(OutputStream os) throws IOException {
        String key = new String((byte[]) args.get(0));
        String filed = new String((byte[]) args.get(1));
        String value = new String((byte[]) args.get(2));
        logger.debug("运行的是 hset 命令: {} {} {}",key,filed,value);

        Map<String, String> hash = Database.getHashes(key);
        boolean isUpdate = hash.containsKey(filed);
        hash.put(filed, value);
        if (isUpdate) {
            Protocol.writeInteger(os, 0);
        } else {
            Protocol.writeInteger(os, 1);
        }
        logger.debug("插入后数据共有 {} 个",hash.size());



    }
}
