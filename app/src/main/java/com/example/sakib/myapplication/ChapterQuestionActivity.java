package com.example.sakib.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sakib.myapplication.models.Adapters.QuestionAdapter;
import com.example.sakib.myapplication.models.ExamHistory;
import com.example.sakib.myapplication.models.QuestionClient;
import com.example.sakib.myapplication.models.Questions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.os.Build.VERSION_CODES.M;
import static com.example.sakib.myapplication.R.color.black;
import static com.example.sakib.myapplication.R.color.red;

public class ChapterQuestionActivity extends AppCompatActivity {
    private static final long START_TIME_IN_MILLIS = 60000;
    private TextView mTextViewCountDown;
    private static CountDownTimer mCountDownTimer;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 300);
    private ProgressBar spinner;
    private String part1="";
    private String part2="";
    private String tabuName="";


    String apiStr="";
    ListView questionList;
    Button buttonSubmit;
    String []answers;
    List<Questions> questions=new ArrayList<Questions>();
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_question);

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            apiStr = extras.getString("apiStr");
        }

 //       Toast.makeText(ChapterQuestionActivity.this,apiStr, Toast.LENGTH_SHORT).show();

        questionList = (ListView) findViewById(R.id.question_pagination_list);
        mTextViewCountDown = (TextView) findViewById(R.id.text_view_countdown);
        mTextViewCountDown.setBackgroundColor(R.color.black);
        mTextViewCountDown.setVisibility(View.GONE);

        final Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://missiondmc.ml/")
                //    .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        QuestionClient questionClient = retrofit.create(QuestionClient.class);

        Call<List<Questions>> call_cq;

        if(apiStr.contains("DAILY")){

            call_cq = questionClient.cqSubChap("daily",apiStr);
            tabuName="C";
        }
        else {

            if (apiStr.contains("med") || apiStr.contains("den")) {
                apiStr = returnUrlX(apiStr);
                if(apiStr.contains("med"))
                    tabuName="XM";
                else
                    tabuName="XD";
            }
            String[] parts = apiStr.split("/");
            part1 = parts[0]; // 004
            part2 = parts[1]; // 034556

            if (part1.contains("Bio1") || part1.contains("Bio2") || part1.contains("Ph1") || part1.contains("Ph2") || part1.contains("Ch1")
                    || part1.contains("Ch2") || part1.contains("gKnow") || part1.contains("English")) {
                tabuName="C";
                call_cq = questionClient.cqSubChap(part1, part2);
            } else {
                call_cq = questionClient.xqYearMVD(part1, part2);
            }
        }
        buttonSubmit = (Button) findViewById(R.id.subBut);
        buttonSubmit.setVisibility(View.GONE);

        call_cq.enqueue(new Callback<List<Questions>>() {
            @Override
            public void onResponse(Call<List<Questions>> call, Response<List<Questions>> response) {
                System.out.println("hcud");

                questions = response.body();

                answers=new String[questions.size()];

                questionList.setAdapter(new QuestionAdapter(ChapterQuestionActivity.this, questions, answers));

                questionList.setOnItemClickListener(new ListView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Questions questions=(Questions) adapterView.getItemAtPosition(i);


                  //      Toast.makeText(ChapterQuestionActivity.this, questions.getQuestion(), Toast.LENGTH_SHORT).show();

                    }
                });

                spinner.setVisibility(View.GONE);
                buttonSubmit.setVisibility(View.VISIBLE);
                mTextViewCountDown.setVisibility(View.VISIBLE);
                int nx= questions.size();

                startTimer(nx);




                buttonSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mCountDownTimer.cancel();

                        float total=0;

//                        Toast.makeText(ChapterQuestionActivity.this, answers[0]+answers[1]+answers[2],Toast.LENGTH_SHORT).show();
                        for(int ii=0; ii<questions.size();ii++)
                        {

                            System.out.println(questions.size()+" ybo "+answers.length);
                            String ans= answers[ii];
                            System.out.println("wohowo"+Integer.toString(ii+1)+ans+"wohowo"+questions.get(ii).getCorrectAns());
//                            answers[ii]=answers[ii]+"( haha )"+ "lol(DE)";
                            if(ans!=null) {
                                //ans = answers[ii].substring(answers[ii].indexOf("(") + 1, answers[ii].indexOf(")"));
                                //answers[ii]=ans;
                                Toast.makeText(ChapterQuestionActivity.this, ans, Toast.LENGTH_SHORT).show();
                                if (questions.get(ii).getCorrectAns().contains(ans))
                                    total += 1;
                                else
                                    total -= 0.25;
                                System.out.println(ans + "yo" + Float.toString(total) + " " + questions.get(ii).getCorrectAns() + " yobabes");
                            }
                        }

                        FirebaseAuth mAuth;
                        mAuth = FirebaseAuth.getInstance();
                        FirebaseUser user = mAuth.getCurrentUser();
                        ExamHistory examHistory;

                        if(tabuName.contains("C"))
                            examHistory= new ExamHistory(user.getUid(), user.getDisplayName(), questions.get(0).getQuestionId(), part2, "C", total);
                        else
                            examHistory= new ExamHistory(user.getUid(), user.getDisplayName(), questions.get(0).getQuestionId(), part1, "X", total);

                        sendExamHistory(examHistory);

                        Toast.makeText(ChapterQuestionActivity.this, Float.toString(total), Toast.LENGTH_SHORT).show();
                        Bundle bundle=new Bundle();
                        String listSerializedToJson = new Gson().toJson(questions);
                    //    bundle.putString("questions",listSerializedToJson);
                        List<Questions> ques=questions;
                      //  bundle.putParcelableArrayList("que", (ArrayList<? extends Parcelable>) ques);
                        bundle.putString("que",listSerializedToJson);
                        bundle.putStringArray("ans",answers);
                        bundle.putFloat("res",total);

                        launchActivityResult(bundle);
                    }
                });

            }

            @Override
            public void onFailure(Call<List<Questions>> call, Throwable t) {
                Toast.makeText(ChapterQuestionActivity.this, "error :(", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void sendExamHistory(ExamHistory ehisotry){
        Retrofit.Builder builder2 = new Retrofit.Builder()
                .baseUrl("http://missiondmc.ml/")
                //    .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit2 = builder2.build();
        QuestionClient eClient = retrofit2.create(QuestionClient.class);
        Call<ExamHistory> call= eClient.post_e_history(ehisotry);

        call.enqueue(new Callback<ExamHistory>() {
            @Override
            public void onResponse(Call<ExamHistory> call, Response<ExamHistory> response) {
                Toast.makeText(ChapterQuestionActivity.this, "yes! :)", Toast.LENGTH_SHORT).show();
                System.out.print("FUFU"+ response.body());
            }

            @Override
            public void onFailure(Call<ExamHistory> call, Throwable t) {
                Toast.makeText(ChapterQuestionActivity.this, "Already Exists", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void launchActivityResult(Bundle bundle) {
        Intent intent = new Intent(this, ResultActivity.class);
       // intent.putExtra("Result",valueA);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    private String returnUrlX(String s) {
        if (s.contains("med")) {
            if (s.contains("২০১৬-২০১৭"))
                return "2016/M";
            else if (s.contains("২০১৭-২০১৮")) {
                return "2017/M";
            } else if (s.contains("২০১৫-২০১৬")) {
                return "2015/M";
            } else if (s.contains("২০১৪-২০১৫")) {
                return "2014/M";
            } else if (s.contains("২০১৩-২০১৪")) {
                return "2013/M";
            } else if (s.contains("২০১২-২০১৩")) {
                return "2012/M";
            } else if (s.contains("২০১১-২০১২")) {
                return "2011/M";
            } else if (s.contains("২০১০-২০১১")) {
                return "2010/M";
            } else if (s.contains("২০০৯-২০১০")) {
                return "2009/M";
            } else if (s.contains("২০০৮-২০০৯")) {
                return "2008/M";
            } else if (s.contains("২০০৭-২০০৮")) {
                return "2007/M";
            } else if (s.contains("২০০৬-২০০৭")) {
                return "2006/M";
            } else if (s.contains("২০০৫-২০০৬")) {
                return "2005/M";
            } else if (s.contains("২০০৪-২০০৫")) {
                return "2004/M";
            } else if (s.contains("২০০৩-২০০৪")) {
                return "2003/M";
            } else if (s.contains("২০০২-২০০৩")) {
                return "2002/M";
            } else if (s.contains("২০০১-২০০২")) {
                return "2001/M";
            } else if (s.contains("২০০০-২০০১")) {
                return "2000/M";
            } else if (s.contains("১৯৯৯-২০০০")) {
                return "1999/M";
            } else if (s.contains("১৯৯৮-১৯৯৯")) {
                return "1998/M";
            } else if (s.contains("১৯৯৭-১৯৯৮")) {
                return "1997/M";
            } else if (s.contains("১৯৯৬-১৯৯৭")) {
                return "1996/M";
            } else if (s.contains("১৯৯৫-১৯৯৬")) {
                return "1995/M";
            } else if (s.contains("১৯৯৪-১৯৯৫")) {
                return "1994/M";
            } else if (s.contains("১৯৯৩-১৯৯৪")) {
                return "1993/M";
            } else if (s.contains("১৯৯২-১৯৯৩")) {
                return "1992/M";
            } else if (s.contains("১৯৯১-১৯৯২")) {
                return "1991/M";
            } else if (s.contains("১৯৯০-১৯৯১")) {
                return "1990/M";
            } else if (s.contains("১৯৯০-১৯৯১(১)")) {
                return "1990-1/M";
            } else if (s.contains("১৯৮৯-১৯৯০")) {
                return "1989/M";
            } else if (s.contains("১৯৮৮-১৯৮৯")) {
                return "1988/M";
            }

        } else if (s.contains("den")) {
            if (s.contains("২০১৬-২০১৭"))
                return "2016/D";
            else if (s.contains("২০১০-২০১১")) {
                return "2010/D";
            } else if (s.contains("২০০৯-২০১০")) {
                return "2009/D";
            } else if (s.contains("২০০৮-২০০৯")) {
                return "2008/D";
            } else if (s.contains("২০০৭-২০০৮")) {
                return "2007/D";
            } else if (s.contains("২০০৬-২০০৭")) {
                return "2006/D";
            } else if (s.contains("২০০৫-২০০৬")) {
                return "2005/D";
            } else if (s.contains("২০০৪-২০০৫")) {
                return "2004/D";
            } else if (s.contains("২০০৩-২০০৪")) {
                return "2003/D";
            } else if (s.contains("২০০২-২০০৩")) {
                return "2002/D";
            } else if (s.contains("২০০১-২০০২")) {
                return "2001/D";
            } else if (s.contains("২০০০-২০০১")) {
                return "2000/D";
            } else if (s.contains("১৯৯৯-২০০০")) {
                return "1999/D";
            } else if (s.contains("১৯৯৮-১৯৯৯")) {
                return "1998/D";
            } else if (s.contains("১৯৯৭-১৯৯৮")) {
                return "1997/D";
            } else if (s.contains("১৯৯৬-১৯৯৭")) {
                return "1996/D";
            } else if (s.contains("১৯৯৫-১৯৯৬")) {
                return "1995/D";
            }


        }
        return "";
    }

    private void startTimer(int n) {
        int x=40000*n;

        mCountDownTimer = new CountDownTimer(x,1000) {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                Toast.makeText(ChapterQuestionActivity.this, "start"+mTimeLeftInMillis, Toast.LENGTH_SHORT).show();
                updateCountDownText();
                if (millisUntilFinished<=10000) {
                    mTextViewCountDown.setBackgroundColor(red);
                    toneGen1.startTone(300);

                }
            }

            @Override
            public void onFinish() {
                toneGen1.stopTone();
                buttonSubmit.performClick();
            }
        }.start();

    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        Toast.makeText(ChapterQuestionActivity.this, "asf"+timeLeftFormatted, Toast.LENGTH_SHORT).show();
        mTextViewCountDown.setText(timeLeftFormatted);
     //   toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
    }




}
