package com.thierry.gastosautomaticos;

import android.app.*;
import android.content.*;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.*;
import android.widget.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    private FinanceDb db; private LinearLayout list; private TextView total, status; private NumberFormat br;
    @Override public void onCreate(Bundle b) { super.onCreate(b); db=new FinanceDb(this); br=NumberFormat.getCurrencyInstance(new Locale("pt","BR")); build(); refresh(); }
    @Override protected void onResume(){ super.onResume(); if(total!=null) refresh(); }

    private void build(){
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(24,34,24,20); root.setBackgroundColor(getColor(com.thierry.gastosautomaticos.R.color.bg));
        TextView title=tx("Gastos Automáticos",24,true); root.addView(title,new LinearLayout.LayoutParams(-1,WRAP,1));
        status=tx("Leitura de notificações: desativada",14,false); root.addView(status);
        Button access=button("Ativar leitura de notificações"); access.setOnClickListener(v->startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))); root.addView(access);
        LinearLayout card=new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setPadding(22,18,22,18); card.setBackgroundColor(getColor(com.thierry.gastosautomaticos.R.color.card));
        TextView month=tx("GASTOS DESTE MÊS",12,true); card.addView(month); total=tx("R$ 0,00",32,true); total.setTextColor(getColor(com.thierry.gastosautomaticos.R.color.primary)); card.addView(total);
        root.addView(card,new LinearLayout.LayoutParams(-1,WRAP));
        LinearLayout bar=new LinearLayout(this); bar.setOrientation(LinearLayout.HORIZONTAL); Button add=button("+ Lançamento manual"); add.setOnClickListener(v->manualDialog()); Button clear=button("Atualizar"); clear.setOnClickListener(v->refresh()); bar.addView(add,new LinearLayout.LayoutParams(0,WRAP,1)); bar.addView(clear,new LinearLayout.LayoutParams(0,WRAP,1)); root.addView(bar);
        TextView h=tx("Movimentações recentes",18,true); h.setPadding(0,24,0,12); root.addView(h);
        ScrollView sv=new ScrollView(this); list=new LinearLayout(this); list.setOrientation(LinearLayout.VERTICAL); sv.addView(list); root.addView(sv,new LinearLayout.LayoutParams(-1,0,3));
        TextView foot=tx("Privacidade: os dados ficam no aparelho.\nOpen Finance pode ser conectado depois via provedor autorizado.",12,false); foot.setTextColor(getColor(com.thierry.gastosautomaticos.R.color.muted)); foot.setPadding(0,14,0,0); root.addView(foot);
        setContentView(root);
    }

    private void refresh(){
        Calendar from=Calendar.getInstance(); from.set(Calendar.DAY_OF_MONTH,1); from.set(Calendar.HOUR_OF_DAY,0); from.set(Calendar.MINUTE,0); from.set(Calendar.SECOND,0); from.set(Calendar.MILLISECOND,0);
        Calendar to=(Calendar)from.clone(); to.add(Calendar.MONTH,1);
        total.setText(br.format(db.monthTotalCents(from,to)/100.0));
        list.removeAllViews(); SimpleDateFormat df=new SimpleDateFormat("dd/MM HH:mm",new Locale("pt","BR"));
        for(Transaction t:db.recent(50)){
            LinearLayout row=new LinearLayout(this); row.setPadding(0,14,0,14); row.setOrientation(LinearLayout.VERTICAL);
            TextView a=tx(t.merchant,16,true); TextView c=tx(t.category+"  •  "+t.source+"  •  "+df.format(new Date(t.timestamp)),12,false); TextView val=tx((t.amountCents/100)+","+String.format(Locale.US,"%02d",t.amountCents%100)+"",16,true); val.setText(br.format(t.amountCents/100.0));
            row.addView(a); row.addView(c); row.addView(val); list.addView(row); View d=new View(this); d.setBackgroundColor(getColor(com.thierry.gastosautomaticos.R.color.divider)); list.addView(d,new LinearLayout.LayoutParams(-1,1));
        }
        boolean enabled=false; try { String s=Settings.Secure.getString(getContentResolver(),"enabled_notification_listeners"); enabled=s!=null && s.contains(getPackageName()); }catch(Exception ignored){}
        status.setText(enabled?"Leitura de notificações: ativa":"Leitura de notificações: desativada"); status.setTextColor(enabled?getColor(com.thierry.gastosautomaticos.R.color.primary):getColor(com.thierry.gastosautomaticos.R.color.muted));
    }

    private void manualDialog(){
        LinearLayout box=new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setPadding(36,8,36,0);
        EditText value=new EditText(this); value.setHint("Valor (ex.: 49,90)"); value.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL); box.addView(value);
        EditText merchant=new EditText(this); merchant.setHint("Descrição / estabelecimento"); box.addView(merchant);
        String[] cats={"Outros","Alimentação","Mercado","Transporte","Saúde","Lazer","Casa","Transferência/Pix"}; Spinner cat=new Spinner(this); cat.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,cats)); box.addView(cat);
        new AlertDialog.Builder(this).setTitle("Novo lançamento").setView(box).setNegativeButton("Cancelar",null).setPositiveButton("Salvar",(d,w)->{
            try { String s=value.getText().toString().replace(".","").replace(",","."); long cents=Math.round(Double.parseDouble(s)*100); String m=merchant.getText().toString().trim(); if(m.isEmpty())m="Lançamento manual"; db.insert(new Transaction(0,cents,m,(String)cat.getSelectedItem(),System.currentTimeMillis(),"Manual",false),"manual|"+System.nanoTime()); refresh(); }catch(Exception ignored){}
        }).show();
    }
    private TextView tx(String s,float size,boolean bold){ TextView v=new TextView(this); v.setText(s); v.setTextSize(size); v.setTextColor(getColor(com.thierry.gastosautomaticos.R.color.text)); if(bold)v.setTypeface(Typeface.DEFAULT,Typeface.BOLD); return v; }
    private Button button(String s){ Button b=new Button(this); b.setText(s); return b; }
    private static final int WRAP=LinearLayout.LayoutParams.WRAP_CONTENT;
}
