package mi.email.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import android.app.Activity;
import android.os.Bundle;

/**
 * ÿ���յ����ʼ� ��һ��ReciveMail����
 * 
 **/

public class ResolveMail {// �����ʼ�

	private MimeMessage mineMsg = null;
	private StringBuffer mailContent = new StringBuffer();// �ʼ�����
	private static final String dataFormat = "yy-MM-dd HH:mm";// ʱ��

	/**
	 * ���캯��
	 * 
	 * @param mimeMessage
	 */

	public ResolveMail(MimeMessage mimeMessage) {
		this.mineMsg = mimeMessage;
	}

	// MimeMessage�趨
	public void setMimeMessage(MimeMessage mimeMessage) {
		this.mineMsg = mimeMessage;
	}

	/**
	 * ��������˵��������ʼ���ַ
	 * 
	 * @throws MessagingException
	 */

	public static String getFrom(MimeMessage message) throws MessagingException {

		InternetAddress address[] = (InternetAddress[]) message.getFrom();
		String addr = address[0].getAddress();
		String name = address[0].getPersonal();

		if (addr == null) {

			addr = "";
		}
		if (name == null) {

			name = "";
		}

		String nameAddr = name + "<" + addr + ">";
		return nameAddr;

	}

	/**
	 * �������ͣ���ȡ�ʼ���ַ "TO"--�ռ��˵�ַ "CC"--�����˵�ַ "BCC"--�����˵�ַ
	 * 
	 * @return
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getMailAddress(String Type) throws Exception {
		String mailAddr = "";
		String addType = Type.toUpperCase();
		InternetAddress[] address = null;
		if (addType.equals("TO")) {// 1.д�������ʱ����������Ҫ����ϸ��������ĸ�ʽ��ʲô�������������ͣ�

			address = (InternetAddress[]) mineMsg
					.getRecipients(Message.RecipientType.TO);
		} else if (addType.equals("CC")) {
			address = (InternetAddress[]) mineMsg
					.getRecipients(Message.RecipientType.CC);
		} else if (addType.equals("BBC")) {
			address = (InternetAddress[]) mineMsg
					.getRecipients(Message.RecipientType.BCC);

		} else {
			System.out.println("error type!");
			throw new Exception("Error emailaddr type!");
		}

		if (address != null) {// ����ʼ���ַ��Ϊ��
			for (int i = 0; i < address.length; i++) {

				String mailaddress = address[i].getAddress();// 2.���������ԭ������Ҫ�����˽�
																// ,��ϸ�˽�"mailaddress"��"mailAddr"
																// getAddress()
				if (mailaddress != null) {
					mailaddress = MimeUtility.decodeText(mailaddress);// 3.MimeUtility.decodeText()���������ɣ�
				} else {
					mailaddress = "";
				}

				String name = address[i].getPersonal();
				if (name != null) {
					name = MimeUtility.decodeText(name);
				} else {
					name = " ";
				}
				mailAddr = name + "<" + mailaddress + ">";
			}

		}
		return mailAddr;

	}

	/**
	 * ȡ���ʼ�����
	 * 
	 * @return String
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */

	public static String getSubject(MimeMessage message) throws UnsupportedEncodingException,
			MessagingException {
		String subject = "";
		subject = MimeUtility.decodeText(message.getSubject());
		if (subject == null) {
			subject = "";

		}
		return subject;
	}

	/**
	 * ȡ���ʼ�����
	 * 
	 * @throws MessagingException
	 */
	public static String getSentDate(MimeMessage message) throws MessagingException {
		Date sentdata = message.getSentDate();
		if (sentdata != null) {
			SimpleDateFormat format = new SimpleDateFormat(dataFormat, Locale.CHINA);
			return format.format(sentdata);
		} else {
			return "�����";
		}
	}

	public void setMailContent(StringBuffer mailContent) {
		this.mailContent = mailContent;
	}

	/**
	 * �����ʼ�����
	 * 
	 * @param part
	 * @throws MessagingException
	 * @throws IOException
	 * @throws Exception
	 */
	public static String getMailContent(Part part) throws MessagingException,
			IOException {
		StringBuilder sb = new StringBuilder();
		String contentType = part.getContentType();// ��ȡ����
		//int nameIndex = contentType.indexOf("name");// �õ���name��Ӧ�ĵ�nameIndex
		//if (nameIndex != -1) {// ΪʲônameIndex�����-1������أ� connName = true; }
			boolean connName = false;// ���boolean����������ʲô���أ�
			if (part.isMimeType("text/plain") && !connName) {// isMimeType��Ĳ����ǣ�
				sb.append((String) part.getContent());// part.getContent()
			} else if (part.isMimeType("text/html") && !connName) {
				sb.append((String) part.getContent());
			} else if (part.isMimeType("multipart/*")) {
				Multipart multipart = (Multipart) part.getContent();// Multipart������ʲô��
				int counts = multipart.getCount();// getCount()��ʲô��˼��
				for (int i = 0; i < counts; i++) {
					sb.append(getMailContent(multipart.getBodyPart(i)));// getBodyPart()
				}
			} else if (part.isMimeType("message/rfc822")) {
				sb.append(getMailContent((Part) part.getContent()));
			}
		//}
		return sb.toString();
	}
}
