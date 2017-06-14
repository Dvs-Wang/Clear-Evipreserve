package Method;

/**
 * Created by WangMingming on 2017/3/10.
 */

import org.bitcoinj.core.Sha256Hash;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;


/**
 *
 * @author Qixuan.Chen
 */
public class SendMail {

    public static final String HOST = "";//To fill the smtp host of your email
    public static final String PROTOCOL = "smtp";
    public static final int PORT = 25;
    public static final String FROM = "";//To fill your email name
    public static final String PWD = "";//To fill the password

    /**
     * 获取Session
     * @return
     */
    private static Session getSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", HOST);//设置服务器地址
        props.put("mail.store.protocol" , PROTOCOL);//设置协议
        props.put("mail.smtp.port", PORT);//设置端口
        props.put("mail.smtp.auth" , true);

        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM, PWD);
            }
        };
        Session session = Session.getDefaultInstance(props , authenticator);


        return session;
    }

    public static void send(String toEmail , String content) {
        Session session = getSession();
        try {
            // Instantiate a message
            Message msg = new MimeMessage(session);
            //Set message attributes
            msg.setFrom(new InternetAddress(FROM));
            InternetAddress[] address = {new InternetAddress(toEmail)};
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject("账号激活邮件");
            msg.setSentDate(new Date());
            msg.setContent(content , "text/html;charset=utf-8");
            //Send the message
            Transport.send(msg);
        }
        catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    public static void processRegister(String email,String ValidateCode){
        ///邮件的内容
        StringBuffer sb = new StringBuffer("欢迎用户" + email + "注册本服务！</br>");
        sb.append("您的邮件激活码如下：");
        sb.append(ValidateCode);
        sb.append("</br>");
        sb.append("请妥善保管，由客户端输入，2小时内有效！");
        //发送邮件
        send(email, sb.toString());
        System.out.println("发送邮件");

    }


}