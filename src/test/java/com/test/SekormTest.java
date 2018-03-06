package com.test;
/*
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sekorm.util.MailSendUtils;
import com.sekorm.util.RedisTemplateUtils;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.imap.IMAPStore;

@RunWith(SpringJUnit4ClassRunner.class) 
@ContextConfiguration(locations = {"classpath*:applicationContext.xml", "classpath*:applicationContext-mvc.xml"})
public class SekormTest extends AbstractJUnit4SpringContextTests{
	@Autowired
	private RedisTemplateUtils redisTemplateUtils;
	@Autowired
	private MailSendUtils mailSendUtils;
	//@Test
	public void test(){
		//redisTemplateUtils.hSet("test","test111","test111");
		Map<Object, Object> map=redisTemplateUtils.getMap("test");
		System.out.println(map.size());
	}
	//@Test
	public void sendMail(){
		HashMap<String, Object> hm=new HashMap<String, Object>();
		hm.put("text","test");
		String[] attachmentPath={"C:\\Users\\noah_yang\\Desktop\\计算机网络技术基础.pdf"};
		mailSendUtils.sendMail("test","noah_yang@sekorm.com",hm, "E:\\workspace\\mail-decryption\\src\\main\\webapp\\img",attachmentPath);
	}
	
	//@Test
	public void mail() throws MessagingException{
		 // 准备连接服务器的会话信息 
		 Properties properties = new Properties();
         properties.put("mail.store.protocol", "pop3");
         properties.put("mail.pop3.host", "cas01.sekorm.com");
         properties.put("mail.pop3.port", "995");
         properties.put("mail.pop3.starttls.enable", "true");
         Session emailSession = Session.getDefaultInstance(properties);
         Store store = emailSession.getStore("pop3s");
         store.connect("cas01.sekorm.com","noah_yang", "A2017#0925"); 
         
        // 获得收件箱 
        Folder folder = store.getFolder("INBOX"); 
        folder.open(Folder.READ_ONLY); //打开收件箱 
         
        // 获得收件箱中的邮件总数 
        System.out.println("邮件总数: " + folder.getMessageCount()); 
         
        // 得到收件箱中的所有邮件,并解析 
        Message[] messages = folder.getMessages(); 
         
        //释放资源 
        folder.close(true); 
        store.close(); 
	}
	
	//@Test
	public void iamp() throws MessagingException, IOException{
		  Properties props = new Properties(); 
		  props.setProperty("mail.imap.socketFactory.class","javax.net.ssl.SSLSocketFactory");
		  props.setProperty("mail.imap.socketFactory.fallback", "true");
          props.setProperty("mail.imap.socketFactory.port","993");  
          props.setProperty("mail.store.protocol","imap");    
          props.setProperty("mail.imap.host","mail.sekorm.com");    
          props.setProperty("mail.imap.port", "993");    
          props.setProperty("mail.imap.auth", "true");
	        // 创建Session实例对象 
	        Session session = Session.getInstance(props); 
	         
	        // 创建IMAP协议的Store对象 
	        Store store = session.getStore("imap"); 
	         
	        // 连接邮件服务器 
	        store.connect("noah_yang", "A2017#0925"); 
	         
	        // 获得收件箱 
	        Folder folder = store.getFolder("INBOX"); 
	        // 以读写模式打开收件箱 
	        folder.open(Folder.READ_WRITE); 
	         
	        // 获得收件箱的邮件列表 
	        Message[] messages = folder.getMessages(); 
	         
	        // 打印不同状态的邮件数量 
	        System.out.println("收件箱中共" + messages.length + "封邮件!"); 
	        System.out.println("收件箱中共" + folder.getUnreadMessageCount() + "封未读邮件!"); 
	        System.out.println("收件箱中共" + folder.getNewMessageCount() + "封新邮件!"); 
	        System.out.println("收件箱中共" + folder.getDeletedMessageCount() + "封已删除邮件!"); 
	         
	        System.out.println("------------------------开始解析邮件----------------------------------"); 
	         
	        
	      //获取未读邮件  
            // 解析邮件 
	        for (Message message : messages) { 
	            IMAPMessage msg = (IMAPMessage) message; 
	            Flags flags = message.getFlags();      
                if (flags.contains(Flags.Flag.SEEN)) {
                	 System.out.println("这是一封已读邮件");  
                	continue;
                } else {      
                	 int messageNumber = message.getMessageNumber();  	      
                     msg.setFlag(Flag.SEEN, true); 
                    System.out.println("未读邮件"+messageNumber);      
                }      
	        } 
	         
	        // 关闭资源 
	        folder.close(false); 
	        store.close(); 
	}
	
	public static void main(String[] args) {
		String s=Class.class.getClass().getResource("/").getPath();
		System.out.println(s);
	}
}*/
