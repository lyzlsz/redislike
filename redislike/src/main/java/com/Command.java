package com;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * package:com
 * Description:TODO
 *
 * @date:2019/8/9 0009
 * @Author:weiwei
 **/
public interface Command {
    void setArgs(List<Object> args);

    void run(OutputStream os) throws IOException;
}
