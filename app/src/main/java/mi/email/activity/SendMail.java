package mi.email.activity;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import mi.learn.com.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sun.mail.util.MailSSLSocketFactory;

public class SendMail extends Activity {
	private Button btnClick;
	private EditText txtToAddress;
	private EditText txtSubject;
	private EditText txtContent;
	private static final String SAVE_INFORMATION = "save_information";
	String username;
	String password;

	public void SendMail() throws MessagingException, IOException {
		// 用sharedpreference来获取数值
		SharedPreferences pre = getSharedPreferences(SAVE_INFORMATION,
				MODE_PRIVATE);
		String content = pre.getString("save", "");
		String[] Information = content.split(";");
		username = Information[0];
		password = Information[1];

		// 该部分有待完善！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.sina.com");// 存储发送邮件服务器的信息
		props.put("mail.smtp.auth", "true");// 同时通过验证

		MailSSLSocketFactory msf = null;
		try {
			msf = new MailSSLSocketFactory();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		msf.setTrustAllHosts(true);
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.ssl.socketFactory", msf);

		// 基本的邮件会话
		Session session = Session.getInstance(props);
		session.setDebug(true);// 设置调试标志
		// 构造信息体
		MimeMessage message = new MimeMessage(session);

		// 发件地址
		Address fromAddress = null;
		// fromAddress = new InternetAddress("sarah_susan@sina.com");
		fromAddress = new InternetAddress(username);
		message.setFrom(fromAddress);

		// 收件地址
		Address toAddress = null;
		toAddress = new InternetAddress(txtToAddress.getText().toString());
		message.addRecipient(Message.RecipientType.TO, toAddress);

		// 解析邮件内容

		message.setSubject(txtSubject.getText().toString());// 设置信件的标题
		message.setText(txtContent.getText().toString());// 设置信件内容
		message.saveChanges(); // implicit with send()//存储有信息

		// send e-mail message

		Transport transport = null;
		transport = session.getTransport("smtp");
		transport.connect("smtp.sina.com", username, password);

		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
		System.out.println("邮件发送成功！");

	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.send_email);

		txtToAddress = (EditText) findViewById(R.id.txtToAddress);
		txtSubject = (EditText) findViewById(R.id.txtSubject);
		txtContent = (EditText) findViewById(R.id.txtContent);

		txtToAddress.setText("自己的邮箱@qq.com");
		txtSubject.setText("Hello~");
		txtContent.setText("你好，我在做程序呢~");

		btnClick = (Button) findViewById(R.id.btnSEND);
		btnClick.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				try {
					//发送邮件
					SendMail();
					//Toast显示
					Toast toast=Toast.makeText(getApplicationContext(), "邮件发送成功！",Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER,0,0);
					toast.show();
					//界面跳转
					Intent intent =new Intent();
					intent.setClass(SendMail.this, ReceiveAndSend.class);
					startActivity(intent);
				} catch (MessagingException e) {

					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

	}

}