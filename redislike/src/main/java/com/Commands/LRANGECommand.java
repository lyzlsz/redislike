package com.Commands;

import com.Command;
import com.Database;
import com.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * package:com.Commands
 * Description:TODO
 *
 * @date:2019/8/9 0009
 * @Author:weiwei
 **/
public class LRANGECommand implements Command {
    private static final Logger logger = LoggerFactory.getLogger(LRANGECommand.class);
    private List<Object> args;

    @Override
    public void setArgs(List<Object> args) {
        this.args = args;
    }

    @Override
    public void run(OutputStream os) throws IOException {
        String key = new String((byte[]) args.get(0));
        int start = Integer.parseInt(new String((byte[]) args.get(1)));
        int end = Integer.parseInt(new String((byte[]) args.get(2)));
        logger.debug("运行的是 lrange 命令：{} {} {}", key, start,end);

        List<String> list = Database.getList(key);
        if (end < 0) {
            end = list.size() + end;
        }
        List<String> result = list.subList(start, end + 1);
        logger.debug("共有 {} 个数据",list.size());

        try {
            Protocol.writeArray(os, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
