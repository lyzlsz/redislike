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
public class HGETCommand implements Command {
    private static final Logger logger = LoggerFactory.getLogger(HGETCommand.class);
    private List<Object> args;

    @Override
    public void setArgs(List<Object> args) {
        this.args = args;
    }

    @Override
    public void run(OutputStream os) throws IOException {
        String key = new String((byte[]) args.get(0));
        String field = new String((byte[]) args.get(1));
        logger.debug("运行的是 hget 命令: {} {} ",key,field);

        Map<String, String> hash = Database.getHashes(key);
        String value = hash.get(field);
        if (value != null) {
            Protocol.writeBulkString(os, value);
        } else {
            Protocol.writenull(os);
        }
        logger.debug("共有 {} 个数据",hash.size());

    }
}
