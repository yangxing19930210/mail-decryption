package com.sekorm.util;

import java.io.File;
import java.util.HashMap;
import javax.mail.internet.MimeMessage;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

@Component
public class MailSendUtils {
	private final static Logger logger=LoggerFactory.getLogger(MailSendUtils.class);
	
	@Autowired
	private JavaMailSenderImpl javaMailSender;
	@Autowired
	private VelocityEngine velocityEngine;
	/**
	 * 发送邮件
	 * @param subject
	 * @param address
	 * @param imgPath
	 * @param hm
	 * @param attachmentPath
	 * @return
	 */
	public boolean sendMail(String subject,String address,HashMap<String, Object> hm,String imgPath,
			String[] attachmentPath){
		Boolean result=true;
		try{
			MimeMessage mailMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true, "utf-8");
				messageHelper.setTo(address);
				messageHelper.setFrom(javaMailSender.getUsername()+"@sekorm.com");
				messageHelper.setSubject(subject);
			String text = VelocityEngineUtils.mergeTemplateIntoString(
					velocityEngine, "mail.vm", "UTF-8", hm);
				messageHelper.setText(text, true);
			// 正文插入图片必须放在设置text的下面设置文件，否则出错
			// 从本地文件夹中获取所需图片
			FileSystemResource img1 = new FileSystemResource(new File(imgPath
					+ File.separator + "1.png"));
			FileSystemResource img2 = new FileSystemResource(new File(imgPath
					+ File.separator + "2.png"));
			FileSystemResource img3 = new FileSystemResource(new File(imgPath
					+ File.separator + "3.png"));
				messageHelper.addInline("img1", img1);
				messageHelper.addInline("img2", img2);
				messageHelper.addInline("img3", img3);
			if(attachmentPath!=null && attachmentPath.length>0)
			for(int i=0;i<attachmentPath.length;i++){
				FileSystemResource file = new FileSystemResource(attachmentPath[i]);
				messageHelper.addAttachment(file.getFilename(), file);  
			}
			javaMailSender.send(mailMessage);
		}catch(Exception e){
			result=false;
			logger.error("发送邮件失败！",e);
		}
		return result;
	}
	
}
