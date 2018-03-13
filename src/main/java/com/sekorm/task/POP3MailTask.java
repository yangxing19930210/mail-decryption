package com.sekorm.task;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sekorm.dao.LeaderMailDao;
import com.sekorm.dao.LogDao;
import com.sekorm.entity.LeaderMail;
import com.sekorm.entity.Log;
import com.sekorm.util.FilePathAllUtils;
import com.sekorm.util.MailSendUtils;
import com.sekorm.util.RedisTemplateUtils;
import com.sekorm.util.ZipAndRarUtils;

@Component
public class POP3MailTask {
	private final static Logger logger = LoggerFactory
			.getLogger(POP3MailTask.class);
	
	@Autowired
	private MailSendUtils mailSendUtils;
	@Autowired
	private RedisTemplateUtils redisTemplateUtils;
	@Autowired
	private LeaderMailDao leaderMailDao;
	@Autowired
    	private LogDao logDao;
	@Value("#{configProperties['mail.path']}")
	private String path;
	@Value("#{configProperties['mail.userName']}")
	private String userName;
	@Value("#{configProperties['mail.passWord']}")
	private String passWord;
	@Value("#{configProperties['mail.errorName']}")
	private String errorName;
	@Value("#{configProperties['img.path']}")
	private String imgPath;
	private static Session session;
	
	@SuppressWarnings("unused")
	private POP3MailTask() {
		Properties properties = new Properties();
		properties.put("mail.store.protocol", "pop3");
		properties.put("mail.pop3.host", "cas01.sekorm.com");
		properties.put("mail.pop3.port", "995");
		properties.put("mail.pop3.starttls.enable", "true");
		session = Session.getDefaultInstance(properties);
	}
	
	@SuppressWarnings("static-access")
	@Scheduled(cron = "0/10 * * * * ? ")
	public void Pop3Task() throws MessagingException, UnsupportedEncodingException {
		Store store = session.getStore("pop3s");
			store.connect("cas01.sekorm.com",userName,passWord);
		Folder folder = store.getFolder("INBOX");
			folder.open(Folder.READ_ONLY);
		Map<Object, Object> map = redisTemplateUtils.getMap("status");
		for (Entry<Object, Object> entry : map.entrySet()) {
			if ("false".equals(entry.getValue())) {
				Message message = folder.getMessage(Integer.valueOf(String
						.valueOf(entry.getKey())));
				try {
					if(userName.equals(getEmailNameSub(getFrom(message)))){
						redisTemplateUtils.hSet("status",entry.getKey(),"true");
						continue;
					}
					parseMessage(message);
					redisTemplateUtils.hSet("status",entry.getKey(),"true");
				} catch (Exception e) {
					HashMap<String, Object> hm = new HashMap<String, Object>();
					Log log=getLog(message,"3");
					logDao.insert(log);
					redisTemplateUtils.hSet("status",entry.getKey(),"true");
					hm.put("text","系统解密失败！请检查系统");
					mailSendUtils.sendMail("解密系统故障警告",errorName+"@sekorm.com", hm,
							imgPath,null);
					logger.error("系统故障",e);
					e.printStackTrace();
				}
			}
		}
		folder.close(true);
		store.close();
	}
	
	/**
	 * 解析邮件
	 * 
	 * @param messages
	 *            要解析的邮件列表
	 * @throws IOException
	 * @throws MessagingException
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 * @throws InterruptedException 
	 */
	private void parseMessage(Message message)
			throws UnsupportedEncodingException, FileNotFoundException,
			MessagingException, IOException, InterruptedException {
		HashMap<String, Object> hm = new HashMap<String, Object>();
		String[] allPath={};
		Integer check=checkMail(message);
		Log log=null;
		switch (check) {
		case 1:
			hm.put("text", "无解密的附件,请核实后重新申请解密!如有异常请联系IT5878！或RTX联系it部系统组电脑工程师！");
			log=getLog(message,"1");
			log.setAccessory("无附件");
			break;
		case 2:
			hm.put("text", "根据世强信息安全条例！您解密的时候需要同时把邮件抄送或发送给您的领导！如有异常请联系IT5878！或RTX联系it部系统组电脑工程师！");
			log=getLog(message,"2");
			if (!isContainAttachment(message)) {
				log.setAccessory("无附件");
			}else{
				log.setAccessory("有附件");
			}
			break;
		case 0:
			ZipAndRarUtils.delAllFile(path+File.separator);
			saveAttachment(message);
			hm.put("text", "解密后的文件不带小锁图标,如有解密异常请联系IT5878！或RTX联系it部系统组电脑工程师！");
			Thread.sleep(2000);
			allPath=FilePathAllUtils.FilePathAll(path);
			log=getLog(message,"0");
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < allPath.length; i++){
				sb.append(allPath[i]);
				sb.append(" ");
			}
			log.setAccessory(sb.toString());
			break;
		}
		mailSendUtils.sendMail("解密系统回复:"+getSubject(message),getFromEmail(getFrom(message)), hm,
				imgPath,allPath);
		//记录日志
		logDao.insert(log);
	}
	/**
	 * log记录
	 * @param message
	 * @param status
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 */
	@SuppressWarnings("unused")
	private Log getLog(Message message,String status) throws UnsupportedEncodingException, MessagingException{
		Log log=new Log();
		/*log.setSendName(getFrom(message));*/
		log.setSendName(getFrom(message));
		/*log.setAddresseName(getToOrCc(message,"to"));*/
		try{
			log.setAddresseName(getReceiveAddress((MimeMessage) message,"to"));
			log.setcCRName(getReceiveAddress((MimeMessage) message,"cc"));
			/*log.setcCRName(getToOrCc(message,"cc"));*/
		}catch(Exception e){
			log.setcCRName("无");
			logger.error("无抄送人");
		}
		log.setSubject(getSubject(message));
		log.setTime(getSentDate(message,"yyyy-MM-dd HH:mm:ss"));
		log.setStatus(status);
		return log;
	}
	
	/**
	 * 判断是否符合邮件解密标准
	 * 
	 * @param message
	 * @return result 0 成功 1 无附件  2抄送或接收人中不存在领导
	 * @throws IOException 
	 * @throws MessagingException 
	 */
	@SuppressWarnings("unused")
	private Integer checkMail(Message message) throws MessagingException, IOException {
		Integer result =0;
		try {
			if (!isContainAttachment(message)) {
				result = 1;
				return result;
			}
		} catch (MessagingException | IOException e) {
			e.printStackTrace();
			logger.error("附件读取异常", e);
		}
		String to = null;
		String cc = null;
		try {
			to = getReceiveAddress((MimeMessage) message,"to");
			cc = getReceiveAddress((MimeMessage) message,"cc");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("收件人或抄送人读取异常", e);
		}
		String[] email=null;
		if(cc==null){
			email=getEmailSub(to.split(","));
		}else{
			email=getEmailSub((to+","+cc).split(","));
		}
		HashMap<String,String> hm=new HashMap<String,String>();
		hm.put("mail",getFromEmail(getFrom(message)));
		List<LeaderMail> list=leaderMailDao.findByMail(hm);
		for(LeaderMail lm:list){
			for(int i=0;i<email.length;i++){
				if(lm.getLeaderMail().equals(email[i].toLowerCase())){
					return 0;
				}	
			}
		}
		result=2;
		return result;
	}
	
	/**
	 * 获得邮件主题
	 * 
	 * @param message
	 *            邮件内容
	 * @return 解码后的邮件主题
	 */
	private static String getSubject(Message message)
			throws UnsupportedEncodingException, MessagingException {
		String subject = message.getHeader("subject")[0];
		subject=subject.replaceAll("gb2312", "gbk").replaceAll("GB2312", "gbk");
		return MimeUtility.decodeText(subject);
	}
	
	/**
	 * 获得邮件发件人
	 * 
	 * @param message
	 *            邮件内容
	 * @return 姓名 <Email地址>
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	private static String getFrom(Message message) throws MessagingException,
			UnsupportedEncodingException {
		String from = "";
		Address[] froms = message.getFrom();
		if (froms.length < 1)
			throw new MessagingException("没有发件人!");
		InternetAddress address = (InternetAddress) froms[0];
		if(address.getEncodedPersonal()!=null)
			address.setEncodedPersonal(address.getEncodedPersonal().replaceAll("gb2312", "gbk").replaceAll("GB2312", "gbk"));
		String person = address.getPersonal();
		if (person != null) {
			person = MimeUtility.decodeText(person) + " ";
		} else {
			person = "";
		}
		from = person + "<" + address.getAddress() + ">";
		
		return from;
	}
	
	/**
	 * 根据收件人类型，获取邮件收件人、抄送和密送地址。如果收件人类型为空，则获得所有的收件人
	 * <p>
	 * Message.RecipientType.TO 收件人
	 * </p>
	 * <p>
	 * Message.RecipientType.CC 抄送
	 * </p>
	 * <p>
	 * Message.RecipientType.BCC 密送
	 * </p>
	 * 
	 * @param msg
	 *            邮件内容
	 * @param type
	 *            收件人类型
	 * @return 收件人1 <邮件地址1>, 收件人2 <邮件地址2>, ...
	 * @throws Exception 
	 */
	private static String getReceiveAddress(MimeMessage msg,
			String type) throws Exception {
		String mailaddr = "";
		String addtype = type.toUpperCase();
		InternetAddress[] address = null;
		if (addtype.equals("TO") || addtype.equals("CC")
				|| addtype.equals("BCC")) {
			if (addtype.equals("TO")) {
				address = (InternetAddress[]) msg
						.getRecipients(Message.RecipientType.TO);
			} else if (addtype.equals("CC")) {
				address = (InternetAddress[]) msg
						.getRecipients(Message.RecipientType.CC);
			} else {
				address = (InternetAddress[]) msg
						.getRecipients(Message.RecipientType.BCC);
			}
			if (address != null) {
				for (int i = 0; i < address.length; i++) {
					String email = address[i].getAddress();
					if (email == null)
						email = "";
					else {
						email = MimeUtility.decodeText(email);
					}
					if(address[i].getEncodedPersonal()!=null)
						address[i].setEncodedPersonal(address[i].getEncodedPersonal().replaceAll("gb2312", "gbk").replaceAll("GB2312", "gbk"));
					String personal = address[i].getPersonal();
					if (personal == null)
						personal = "";
					else {
						personal = MimeUtility.decodeText(personal);
					}
					String compositeto = personal + "<" + email + ">";
					mailaddr += "," + compositeto;
				}
				mailaddr = mailaddr.substring(1);
			}
		} else {
			throw new Exception("Error emailaddr type!");
		}
		return mailaddr;
	}
	/**
	 * 接收 and 抄送
	 * @param message
	 * @param type
	 * @return
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	private static String getToOrCc(Message message,String type) throws MessagingException, UnsupportedEncodingException{
		String to = message.getHeader(type)[0];
		to = to.replaceAll("gb2312", "gbk").replaceAll("GB2312", "gbk");
		return MimeUtility.decodeText(to);
	}

	/**
	 * 获得邮件发送时间
	 * 
	 * @param msg
	 *            邮件内容
	 * @return yyyyMMdd
	 * @throws MessagingException
	 */
	private static String getSentDate(Message msg, String pattern)
			throws MessagingException {
		Date receivedDate = msg.getSentDate();
		if (receivedDate == null)
			return "";
		if (pattern == null || "".equals(pattern))
			pattern = "yyyyMMdd";
		return new SimpleDateFormat(pattern).format(receivedDate);
	}

	/**
	 * 判断邮件中是否包含附件
	 * 
	 * @param msg
	 *            邮件内容
	 * @return 邮件中存在附件返回true，不存在返回false
	 * @throws MessagingException
	 * @throws IOException
	 */
	private static boolean isContainAttachment(Part part)
			throws MessagingException, IOException {
		boolean attachflag = false;
		if (part.isMimeType("multipart/mixed")) {
			Multipart mp = (Multipart) part.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				BodyPart mpart = mp.getBodyPart(i);
				String disposition = mpart.getDisposition();
				if ((disposition != null)
						&& ((disposition.equals(Part.ATTACHMENT)) || (disposition
								.equals(Part.INLINE))))
					attachflag = true;
				else if (mpart.isMimeType("multipart/mixed")) {
					attachflag = isContainAttachment((Part) mpart);
				} else {
					String contype = mpart.getContentType();
					if (contype.toLowerCase().indexOf("application") != -1)
						attachflag = true;
					if (contype.toLowerCase().indexOf("name") != -1)
						attachflag = true;
				}
			}
		} else if (part.isMimeType("message/rfc822")) {
			attachflag = isContainAttachment((Part) part.getContent());
		}
		return attachflag;
	}

	/**
	 * 判断邮件是否已读
	 * 
	 * @param msg
	 *            邮件内容
	 * @return 如果邮件已读返回true,否则返回false
	 * @throws MessagingException
	 */
	private static boolean isSeen(MimeMessage msg) throws MessagingException {
		return msg.getFlags().contains(Flags.Flag.SEEN);
	}

	/**
	 * 判断邮件是否需要阅读回执
	 * 
	 * @param msg
	 *            邮件内容
	 * @return 需要回执返回true,否则返回false
	 * @throws MessagingException
	 */
	private static boolean isReplySign(MimeMessage msg)
			throws MessagingException {
		boolean replySign = false;
		String[] headers = msg.getHeader("Disposition-Notification-To");
		if (headers != null)
			replySign = true;
		return replySign;
	}

	/**
	 * 获得邮件的优先级
	 * 
	 * @param msg
	 *            邮件内容
	 * @return 1(High):紧急 3:普通(Normal) 5:低(Low)
	 * @throws MessagingException
	 */
	private static String getPriority(MimeMessage msg) throws MessagingException {
		String priority = "普通";
		String[] headers = msg.getHeader("X-Priority");
		if (headers != null) {
			String headerPriority = headers[0];
			if (headerPriority.indexOf("1") != -1
					|| headerPriority.indexOf("High") != -1)
				priority = "紧急";
			else if (headerPriority.indexOf("5") != -1
					|| headerPriority.indexOf("Low") != -1)
				priority = "低";
			else
				priority = "普通";
		}
		return priority;
	}

	/**
	 * 获得邮件文本内容
	 * 
	 * @param part
	 *            邮件体
	 * @param content
	 *            存储邮件文本内容的字符串
	 * @throws MessagingException
	 * @throws IOException
	 */
	private static void getMailTextContent(Part part, StringBuffer content)
			throws MessagingException, IOException {
		// 如果是文本类型的附件，通过getContent方法可以取到文本内容，但这不是我们需要的结果，所以在这里要做判断
		boolean isContainTextAttach = part.getContentType().indexOf("name") > 0;
		if (part.isMimeType("text/*") && !isContainTextAttach) {
			content.append(part.getContent().toString());
		} else if (part.isMimeType("message/rfc822")) {
			getMailTextContent((Part) part.getContent(), content);
		} else if (part.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) part.getContent();
			int partCount = multipart.getCount();
			for (int i = 0; i < partCount; i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				getMailTextContent(bodyPart, content);
			}
		}
	}

	/**
	 * 保存附件
	 * 
	 * @param part
	 *            邮件中多个组合体中的其中一个组合体
	 * @param destDir
	 *            附件保存目录
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void saveAttachment(Part part)
			throws UnsupportedEncodingException, MessagingException,
			FileNotFoundException, IOException {
		String fileName = "";
		if (part.isMimeType("multipart/mixed")) {
			Multipart mp = (Multipart) part.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				BodyPart mpart = mp.getBodyPart(i);
				String disposition = mpart.getDisposition();
				if ((disposition != null)
						&& ((disposition.equals(Part.ATTACHMENT)) || (disposition
								.equals(Part.INLINE)))) {
					fileName = mpart.getFileName();
					if (fileName.toLowerCase().indexOf("gb2312") != -1) {
						fileName=fileName.replaceAll("gb2312", "gbk").replaceAll("GB2312", "gbk");
						fileName = MimeUtility.decodeText(fileName);
					}
					saveFile(fileName, mpart.getInputStream());
				} else if (mpart.isMimeType("multipart/mixed")) {
					saveAttachment(mpart);
				} else {
					fileName = mpart.getFileName();
					if ((fileName != null)
							&& (fileName.toLowerCase().indexOf("gb2312") != -1)) {
						fileName=fileName.replaceAll("gb2312", "gbk").replaceAll("GB2312", "gbk");
						fileName = MimeUtility.decodeText(fileName);
						saveFile(fileName, mpart.getInputStream());
					}
				}
			}
		} else if (part.isMimeType("message/rfc822")) {
			saveAttachment((Part) part.getContent());
		}
	}
	
	/**
	 * 读取输入流中的数据保存至指定目录
	 * 
	 * @param in
	 *            输入流
	 * @param fileName
	 *            文件名
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void saveFile(String fileName, InputStream in)
			throws FileNotFoundException, IOException {
		if ((fileName != null)
				&& ((fileName.toLowerCase().indexOf("utf-8") != -1) || (fileName
						.toLowerCase().indexOf("gb2312") != -1))) {
			fileName = MimeUtility.decodeText(fileName);
		}
		String osName = System.getProperty("os.name");
		String storedir=path;
		File f = new File(storedir);
		if (!f.exists() && !f.isDirectory()) {
			f.mkdirs();// 创建多级目录
		}
		String separator = "";
		if (osName == null)
			osName = "";
		if (osName.toLowerCase().indexOf("win") != -1) {
			separator = "\\";
			if (storedir == null || storedir.equals(""))
				storedir = "c:\\tmp";
		} else {
			separator = "/";
			storedir = "/tmp";
		}
		File storefile = new File(storedir + separator + fileName);
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(storefile));
			bis = new BufferedInputStream(in);
			int c;
			byte[] bytes = new byte[1024];
			while ((c = bis.read(bytes)) != -1) {
				bos.write(bytes, 0, c);
				bos.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bos.close();
			bis.close();
		}
		if ("zip".equals(fileName.substring(fileName.lastIndexOf(".") + 1))
			|| "rar".equals(fileName.substring(fileName.lastIndexOf(".") + 1))) {
			try {
				ZipAndRarUtils.decryptionZipAndRar(storedir + separator + fileName,storedir + separator);
			} catch (Exception e) {
				logger.info("压缩包解密失败",e);
			}
		}
	}

	/**
	 * 文本解码
	 * 
	 * @param encodeText
	 *            解码MimeUtility.encodeText(String text)方法编码后的文本
	 * @return 解码后的文本
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings("unused")
	private static String decodeText(String encodeText)
			throws UnsupportedEncodingException {
		if (encodeText == null || "".equals(encodeText)) {
			return "";
		} else {
			return MimeUtility.decodeText(encodeText);
		}
	}
	/**
	 * 获取email截取的地址
	 * xxx
	 * @param s
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String getEmailNameSub(String s) {
		try{
				int start = s.indexOf("<");
				int end = s.indexOf("@");
				s=s.substring(start + 1, end).toLowerCase();
		}catch(Exception e){
			logger.info("转换异常");
		}
		return s;
	}
	
	/**
	 * xxx@sekorm.com
	 * @param s
	 * @return
	 */
	private static String[] getEmailSub(String[] s) {
		try{
			for (int i = 0; i < s.length; i++) {
				int start = s[i].indexOf("<");
				int end = s[i].indexOf(">");
				s[i] = s[i].substring(start + 1, end);
			}
		}catch(Exception e){
			logger.info("转换异常");
		}
		return s;
	}
	/**
	 * xxx@sekorm.com
	 * @param s
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String getFromEmail(String s){
		try{
			int start = s.indexOf("<");
			int end = s.indexOf(">");
			s=s.substring(start + 1, end);
		}catch(Exception e){
			logger.info("转换异常");
		}
		return s;
	}
}
