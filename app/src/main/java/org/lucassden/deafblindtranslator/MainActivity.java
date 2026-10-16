package org.lucassden.deafblindtranslator;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.View;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {
 private EditText bottom, top;
 private TextToSpeech tts;
 private boolean sync=false;
 private static final int SPEECH=41;
 @Override public void onCreate(Bundle b) {
  super.onCreate(b);
  getWindow().setStatusBarColor(Color.rgb(20,36,55));
  getWindow().setNavigationBarColor(Color.rgb(20,36,55));
  tts=new TextToSpeech(this,s->{if(s==TextToSpeech.SUCCESS)tts.setLanguage(new Locale("ru","RU"));});
  LinearLayout root=new LinearLayout(this); root.setOrientation(1); root.setPadding(dp(10),dp(6),dp(10),dp(6)); root.setBackgroundColor(Color.rgb(245,247,250));
  TextView title=new TextView(this); title.setText("GP-translator"); title.setTextSize(21); title.setTypeface(null,Typeface.BOLD); title.setGravity(Gravity.CENTER); title.setTextColor(Color.rgb(20,36,55)); root.addView(title,new LinearLayout.LayoutParams(-1,dp(40)));
  TextView sub=new TextView(this); sub.setText("Общение голосом и текстом"); sub.setGravity(Gravity.CENTER); root.addView(sub,new LinearLayout.LayoutParams(-1,dp(30)));
  top=editor("Поле собеседника",true); root.addView(top,new LinearLayout.LayoutParams(-1,0,1));
  View line=new View(this); line.setBackgroundColor(Color.LTGRAY); root.addView(line,new LinearLayout.LayoutParams(-1,dp(2)));
  bottom=editor("Введите или надиктуйте сообщение",false); root.addView(bottom,new LinearLayout.LayoutParams(-1,0,1));
  LinearLayout row=new LinearLayout(this);
  Button voice=new Button(this); voice.setText("Говорить"); voice.setOnClickListener(v->listen()); row.addView(voice,new LinearLayout.LayoutParams(0,dp(52),1));
  Button speak=new Button(this); speak.setText("Озвучить"); speak.setOnClickListener(v->say()); row.addView(speak,new LinearLayout.LayoutParams(0,dp(52),1));
  Button clear=new Button(this); clear.setText("Очистить"); clear.setOnClickListener(v->bottom.setText("")); row.addView(clear,new LinearLayout.LayoutParams(0,dp(52),1)); root.addView(row);
  TextView foot=new TextView(this); foot.setText("Верхняя сторона 180° · нижняя 0°"); foot.setGravity(Gravity.CENTER); foot.setTextSize(11); root.addView(foot,new LinearLayout.LayoutParams(-1,dp(24)));
  setContentView(root);
  TextWatcher w=new TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int c,int f){} public void onTextChanged(CharSequence s,int a,int before,int count){if(!sync){sync=true; EditText other=(s==null?bottom:(bottom.hasFocus()?top:bottom)); if(other!=null&&!other.getText().toString().contentEquals(s)){other.setText(s);other.setSelection(other.length());} sync=false;}} public void afterTextChanged(Editable e){}};
  bottom.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int c,int f){} public void onTextChanged(CharSequence s,int a,int b,int c){if(!sync){sync=true;top.setText(s);top.setSelection(top.length());sync=false;}} public void afterTextChanged(Editable e){}});
  top.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int c,int f){} public void onTextChanged(CharSequence s,int a,int b,int c){if(!sync){sync=true;bottom.setText(s);bottom.setSelection(bottom.length());sync=false;}} public void afterTextChanged(Editable e){}});
 }
 private EditText editor(String hint,boolean rotate){EditText e=new EditText(this);e.setHint(hint);e.setTextSize(23);e.setGravity(Gravity.CENTER);e.setPadding(dp(10),dp(8),dp(10),dp(8));e.setBackgroundColor(Color.WHITE);e.setInputType( android.text.InputType.TYPE_CLASS_TEXT|android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES|android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE);e.setMinLines(3);if(rotate)e.setRotation(180);return e;}
 private void listen(){Intent i=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);i.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ru-RU");i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Говорите по-русски");try{startActivityForResult(i,SPEECH);}catch(Exception e){Toast.makeText(this,"Проверьте наличие сервиса распознавания речи",Toast.LENGTH_LONG).show();}}
 @Override protected void onActivityResult(int r,int c,Intent d){super.onActivityResult(r,c,d);if(r==SPEECH&&c==RESULT_OK&&d!=null){ArrayList<String>a=d.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);if(a!=null&&!a.isEmpty())bottom.setText(bottom.length()==0?a.get(0):bottom.getText()+" "+a.get(0));}}
 private void say(){String s=bottom.getText().toString().trim();if(s.isEmpty()){Toast.makeText(this,"Сначала введите текст",Toast.LENGTH_SHORT).show();return;}if(tts!=null)tts.speak(s,TextToSpeech.QUEUE_FLUSH,null,"gp_message");}
 private int dp(int x){return (int)(x*getResources().getDisplayMetrics().density+0.5f);}
 @Override protected void onDestroy(){if(tts!=null){tts.stop();tts.shutdown();}super.onDestroy();}
}