package net.dreamlu.iot.mqtt.mica.util;

import org.jasypt.util.text.BasicTextEncryptor;

public class JasyptUtil {

   public static void main(String[] args) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        //加密所需的salt(盐)
        textEncryptor.setPassword("PFqEQomIp=");
        //要加密的数据（数据库的用户名或密码）
        String host = textEncryptor.encrypt("10.23.204.34");
        String username = textEncryptor.encrypt("root");
        String password = textEncryptor.encrypt("R00T@mysql");
        System.out.println("host:"+host);
        System.out.println("username:"+username);
        System.out.println("password:"+password);
   }

}
