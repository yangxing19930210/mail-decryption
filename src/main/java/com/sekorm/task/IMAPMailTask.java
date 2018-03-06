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
public class IMAPMailTask {
	private final static Logger logger = LoggerFactory
			.getLogger(IMAPMailTask.class);
	
	@Autowired
	private RedisTemplateUtils redisTemplateUtils;
	@Value("#{configProperties['mail.userName']}")
	private String userName;
	@Value("#{configProperties['mail.passWord']}")
	private String passWord;
	private static Session session;
	
	private IMAPMailTask() {
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
	@Scheduled(cron = "0/5 * * * * ? ")
	public void ImapTask() {
		Store store = null;
		Folder folder = null;
		try {
			store = session.getStore("imap");
			store.connect(userName,passWord);
			folder = store.getFolder("INBOX");
			folder.open(Folder.READ_WRITE);
			Message[] messages = folder.getMessages();
			for (Message message : messages) {
				IMAPMessage msg = (IMAPMessage) message;
				Flags flags = message.getFlags();
				int messageNumber = message.getMessageNumber();
				if (flags.contains(Flags.Flag.SEEN)) {
					continue;
				} else {
					redisTemplateUtils.hSet("status",String.valueOf(messageNumber),"false");
					msg.setFlag(Flag.SEEN, true);
				}
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
