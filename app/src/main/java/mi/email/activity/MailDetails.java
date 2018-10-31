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

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import mi.email.core.ResolveMail;
import mi.learn.com.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MailDetails extends Activity {
    private static final String SAVE_INFORMATION = "save_information";
    private static final String TAG = "MailDetails";
    private TextView text1;
    private TextView text2;
    private TextView text3;
    private TextView text4;
    private ReceiveList ml;

    private class MailContent {
        private String title;
        private String from;
        private String date;
        private String content;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public void receive() throws Exception {

        // sharedpreference读取数据，用split（）方法，分开字符串。
        SharedPreferences pre = getSharedPreferences(SAVE_INFORMATION, MODE_PRIVATE);
        String content = pre.getString("save", "");
        String[] Information = content.split(";");
        String username = Information[0];
        String password = Information[1];

        Intent intent = getIntent();
        Bundle i = intent.getExtras();

        int num = i.getInt("ID");//得到上一个文件传入的ID号

        Disposable disposable = Observable.create(new ObservableOnSubscribe<MailContent>() {
            @Override
            public void subscribe(ObservableEmitter<MailContent> e) throws Exception {
                Properties props = new Properties();
                Session session = Session.getDefaultInstance(props);
                // 取得pop3协议的邮件服务器
                Store store = session.getStore("imap");
                // 连接pop.qq.com邮件服务器
                store.connect("imap.exmail.qq.com", "liwei@satcatche.com", "Face12");
                // 返回文件夹对象
                Folder folder = store.getFolder("INBOX");
                // 设置仅读
                folder.open(Folder.READ_ONLY);

                // 获取信息
                //Message message[] = folder.getMessages();
                MimeMessage message = (MimeMessage) folder.getMessages()[num];
                MailContent mailContent = new MailContent();
                mailContent.setTitle(ResolveMail.getSubject(message));
                mailContent.setFrom(ResolveMail.getFrom(message));
                mailContent.setDate(ResolveMail.getSentDate(message));
                mailContent.setContent(ResolveMail.getMailContent(message));
                e.onNext(mailContent);
                folder.close(true);
                store.close();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mailContent -> {
                    //ResolveMail receiveMail = new ResolveMail(message);
                    text1.setText(mailContent.getTitle());
                    text2.setText(mailContent.getFrom());
                    text3.setText(mailContent.getDate());
                    text4.setText(mailContent.getContent());
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(TAG, "error: " + throwable.toString());
                        throwable.printStackTrace();
                    }
                });
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
