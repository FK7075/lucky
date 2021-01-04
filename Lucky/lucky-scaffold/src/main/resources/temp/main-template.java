package @:Package;

import com.lucky.boot.startup.LuckyApplication;
import com.lucky.framework.annotation.LuckyBootApplication;

/**
 * 启动类
 */
@LuckyBootApplication
public class @:MainName {

    public static void main( String[] args ) {
        LuckyApplication.run(@:MainName.class,args);
    }
}