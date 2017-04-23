package com.example.gtxacer.sprouts1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends Activity {

    private static TextView player2;
    private static TextView player1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        player1 = (TextView) findViewById(R.id.textView3);
        player2 = (TextView) findViewById(R.id.textView5);
        setPlayer(1);

        final Button button1 = (Button) findViewById(R.id.button);
        button1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                TouchView.undo();
            }
        });

        final Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"PLAYER 1 WINS!",Toast.LENGTH_LONG).show();
                CountDownTimer countDownTimer = new CountDownTimer(5000, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {
                        //ACTIVITY MENU PRINCIPAL
                        Intent intent = new Intent(getApplicationContext(),Main2.class);
                        startActivity(intent);
                    }
                }.start();
            }
        });


    }

    public static void setPlayer(int a){
        if (a == 1) {
            player1.setTextColor(Color.RED);
            player2.setTextColor(Color.BLUE);
        } else {
            player2.setTextColor(Color.RED);
            player1.setTextColor(Color.BLUE);
        }
    }
}
