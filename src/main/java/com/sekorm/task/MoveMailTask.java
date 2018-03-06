package com.sekorm.task;

import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Flags.Flag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sekorm.util.RedisTemplateUtils;
import com.sun.mail.imap.IMAPMessage;
@Component
public class MoveMailTask {
	private final static Logger logger = LoggerFactory
			.getLogger(MoveMailTask.class);
	
	@Autowired
	private RedisTemplateUtils redisTemplateUtils;
	@Value("#{configProperties['mail.userName']}")
	private String userName;
	@Value("#{configProperties['mail.passWord']}")
	private String passWord;
	private static Session session;
	
	private MoveMailTask() {
		Properties props = new Properties();
		props.setProperty("mail.imap.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.setProperty("mail.imap.socketFactory.fallback", "true");
		props.setProperty("mail.imap.socketFactory.port", "993");
		props.setProperty("mail.store.protocol", "imap");
		props.setProperty("mail.imap.host", "mail.sekorm.com");
		props.setProperty("mail.imap.port", "993");
		props.setProperty("mail.imap.auth", "true");
		session = Session.getInstance(props);
	}
	
	@SuppressWarnings("static-access")
	@Scheduled(cron = " 0 0 3 * * ? ")
	public void ImapTask() {
		Store store = null;
		Folder folder = null;
		Folder dfolder= null;
		try {
			store = session.getStore("imap");
			store.connect(userName,passWord);
			folder = store.getFolder("INBOX");
			folder.open(Folder.READ_WRITE);
			dfolder = store.getFolder("已处理");
            dfolder.open(Folder.READ_WRITE);  
			Message[] messages = folder.getMessages();
			if (messages.length != 0) {  
	            folder.copyMessages(messages, dfolder);//复制到新文件夹  
	            folder.setFlags(messages, new Flags(Flags.Flag.DELETED), true);//删除源文件夹下的邮件  
	        }
			if(redisTemplateUtils.exists("status")){
				redisTemplateUtils.del("status");
			}
		} catch (Exception e) {
			logger.error("Imap处理失败", e);
		} finally {
			try {
				folder.close(true);
				store.close();
			} catch (MessagingException e) {
				logger.error("资源回收失败", e);
			}
		}
	}
}
