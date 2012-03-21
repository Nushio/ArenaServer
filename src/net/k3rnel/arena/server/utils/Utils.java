package net.k3rnel.arena.server.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.ibatis.session.SqlSession;

public class Utils {

    public static String stackTraceString(Throwable throwable){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
    
    public String genUUID(SqlSession session){
        UtilsMapper uMapper = session.getMapper(UtilsMapper.class);
        return uMapper.genUUID();
        
    }
}
