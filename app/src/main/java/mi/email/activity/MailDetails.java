package mi.email.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

import mi.email.core.ResolveMail;
import mi.learn.com.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class MailDetails extends Activity {
	private static final String SAVE_INFORMATION = "save_information";
	private TextView text1;
	private TextView text2;
	private TextView text3;
	private TextView text4;
	private ReceiveList ml;

	public void receive() throws Exception {

		// sharedpreference��ȡ���ݣ���split�����������ֿ��ַ�����
		SharedPreferences pre = getSharedPreferences(SAVE_INFORMATION,MODE_PRIVATE);
		String content = pre.getString("save", "");
		String[] Information = content.split(";");
		String username = Information[0];
		String password = Information[1];

	    Intent intent = getIntent();//�õ���һ���ļ������ID��
		Bundle i = intent.getExtras();
		
		int num = i.getInt("ID");//���õ���ID�Ŵ��ݸ�����num
		
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props);
		// ȡ��pop3Э����ʼ�������
		Store store = session.getStore("pop3");
		// ����pop.qq.com�ʼ�������
		store.connect("pop.sina.com", username, password);
		// �����ļ��ж���
		Folder folder = store.getFolder("INBOX");
		// ���ý���
		folder.open(Folder.READ_ONLY);

		// ��ȡ��Ϣ
		Message message[] = folder.getMessages();
		ResolveMail receivemail = new ResolveMail((MimeMessage) message[num]);
		text1.setText(receivemail.getSubject());
		text2.setText(receivemail.getFrom());
		text3.setText(receivemail.getSentDate());
		text4.setText((CharSequence) message[num].getContent().toString());

		folder.close(true);
		store.close();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main2);

		text1 = (TextView) findViewById(R.id.text1);
		text2 = (TextView) findViewById(R.id.text2);
		text3 = (TextView) findViewById(R.id.text3);
		text4 = (TextView) findViewById(R.id.text4);

		try {
			receive();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
