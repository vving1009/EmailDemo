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

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.operators.observable.ObservableReplay;
import io.reactivex.schedulers.Schedulers;
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
	String username = "liwei@satcatche.com";
	String password = "Face12";

	public void SendMail() throws MessagingException, IOException {

        Disposable disposable = Observable.create(e -> {
            // 用sharedpreference来获取数值
            /*SharedPreferences pre = getSharedPreferences(SAVE_INFORMATION,
                    MODE_PRIVATE);
            String content = pre.getString("save", "");
            String[] Information = content.split(";");
            username = Information[0];
            password = Information[1];

            // 该部分有待完善！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.exmail.qq.com");// 存储发送邮件服务器的信息
            props.put("mail.smtp.auth", "true");// 同时通过验证

            MailSSLSocketFactory msf = null;
            msf = new MailSSLSocketFactory();
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
            System.out.println("邮件发送成功！");*/

            //-------------------------------

            Properties props = new Properties();
            // 开启debug调试
            props.setProperty("mail.debug", "true");
            // 发送服务器需要身份验证
            props.setProperty("mail.smtp.auth", "true");
            // 设置邮件服务器主机名
//            props.setProperty("mail.host", "smtp.163.com");
            // 设置邮件服务器主机名
            props.setProperty("mail.host", "smtp.exmail.qq.com");
            // 发送邮件协议名称
            props.setProperty("mail.transport.protocol", "smtp");
            MailSSLSocketFactory msf = new MailSSLSocketFactory();
            msf.setTrustAllHosts(true);
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.ssl.socketFactory", msf);

            // 设置环境信息
            Session session = Session.getInstance(props);

            // 创建邮件对象
            Message msg = new MimeMessage(session);

            msg.setSubject("AndroidMail测试");
            // 设置邮件内容
            msg.setText("这是一封由大当家发送的邮件！");
            // 设置发件人
            msg.setFrom(new InternetAddress("liwei@satcatche.com"));
            Transport transport = session.getTransport();
            // 连接邮件服务器
            transport.connect("liwei@satcatche.com", "Face12");
            // 发送邮件
            transport.sendMessage(msg, new Address[]{new InternetAddress("liwei@satcatche.com")});
            // 关闭连接
            transport.close();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
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