package com.thierry.gastosautomaticos;

import android.app.Notification;
import android.content.pm.PackageManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.os.Bundle;
import java.security.MessageDigest;

public class TransactionNotificationListener extends NotificationListenerService {
    @Override public void onNotificationPosted(StatusBarNotification sbn) {
        try {
            Notification n = sbn.getNotification();
            Bundle e = n.extras;
            String title = e != null ? e.getString(Notification.EXTRA_TITLE, "") : "";
            CharSequence cs = e != null ? e.getCharSequence(Notification.EXTRA_TEXT) : null;
            String text = cs == null ? "" : cs.toString();
            TransactionParser.Parsed p = TransactionParser.parse(title, text);
            if (p == null) return;
            String source = appLabel(sbn.getPackageName());
            long ts = sbn.getPostTime() > 0 ? sbn.getPostTime() : System.currentTimeMillis();
            Transaction t = new Transaction(0, p.cents, p.merchant, p.category, ts, source, true);
            String fp = sha(sbn.getPackageName()+"|"+title+"|"+text+"|"+(ts/300000));
            new FinanceDb(this).insert(t, fp);
        } catch (Exception ignored) { }
    }
    private String appLabel(String pkg) {
        try { return getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(pkg,0)).toString(); }
        catch (PackageManager.NameNotFoundException e) { return pkg; }
    }
    private String sha(String s) throws Exception { MessageDigest md=MessageDigest.getInstance("SHA-256"); byte[] b=md.digest(s.getBytes("UTF-8")); StringBuilder x=new StringBuilder(); for(byte v:b)x.append(String.format("%02x",v)); return x.toString(); }
}
