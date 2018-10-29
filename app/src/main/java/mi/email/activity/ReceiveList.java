package mi.email.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
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

public class ReceiveList extends Activity {

    private static final String SAVE_INFORMATION = "save_information";

    private ListView listview;
    private int number;

    String Title;
    String Date;
    String From;
    String Content;
    String username;
    String password;

    ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();//定义一个List并且将其实例化
    SimpleAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        super.onCreate(savedInstanceState);

        setContentView(R.layout.listmenu);
        listview = (ListView) findViewById(R.id.my_list);
        listAdapter = new SimpleAdapter(ReceiveList.this, list, R.layout.item, new String[]{"title", "info"}, new int[]{
                R.id.title, R.id.info});
        listview.setAdapter(listAdapter);
        // Item长按事件。得到Item的值，然后传递给MailDetail的值
        listview.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long id) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.putExtra("ID", position);
                intent.setClass(ReceiveList.this, MailDetails.class);
                startActivity(intent);
                return true;
            }
        });
        MenuList();
    }

    public void MenuList() {
        Disposable disposable = Observable.create(e -> {
            // sharedpreference读取数据，用split（）方法，分开字符串。
            SharedPreferences pre = getSharedPreferences(SAVE_INFORMATION,
                    MODE_PRIVATE);
            String content = pre.getString("save", "");
            String[] Information = content.split(";");
            username = Information[0];
            password = Information[1];

            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props); // 取得pop3协议的邮件服务器
            Store store = null;
            store = session.getStore("imap");
            //store = session.getStore("pop3");
            store.connect("imap.exmail.qq.com", "liwei@satcatche.com", "Face12"); // 返回文件夹对象)
            Folder folder = null; // 设置仅读
            folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY); // 获取信息
            Message message[] = folder.getMessages();

            for (int i = 0; i < message.length; i++) {//通过for语句将读取到的邮件内容一个一个的在list中显示出来
                ResolveMail receivemail = new ResolveMail((MimeMessage) message[i]);

                Title = receivemail.getSubject();//得到邮件的标题
                Date = receivemail.getSentDate();//得到邮件的发送时间

                HashMap<String, String> map = new HashMap<String, String>();//定义一个Map.将获取的内容以键值的方式将内容展现
                map.put("title", Title);//显示邮件的标题
                map.put("info", Date);//显示邮件的信息
                list.add(map);
            }
            folder.close(true);//用好之后记得将floder和store进行关闭
            store.close();
            e.onNext(list);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> listAdapter.notifyDataSetChanged(), new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }
}
