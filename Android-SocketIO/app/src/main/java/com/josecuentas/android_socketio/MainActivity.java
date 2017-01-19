package com.josecuentas.android_socketio;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    Socket mSocket;
    String mMessage = "";
    TextView mTviMessage;
    EditText mEteMessage;
    ScrollView mSviContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        injectView();
        injectSocket();
    }

    private void injectView() {
        mTviMessage = (TextView) findViewById(R.id.tviMessage);
        mEteMessage = (EditText) findViewById(R.id.eteMessage);
        mSviContainer = (ScrollView) findViewById(R.id.sviContainer);

        findViewById(R.id.butSend).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                String message = mEteMessage.getText().toString();
                mSocket.emit("chat message", message);
                mEteMessage.setText("");
                //ScrollView position bottom end
                mSviContainer.fullScroll(View.FOCUS_DOWN);

            }
        });
    }

    private void injectSocket() {
        try {
            mSocket = IO.socket("http://169.254.26.0:3000/");
            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override public void call(Object... args) {

                }
            }).on("chat message", new Emitter.Listener() {
                @Override public void call(Object... args) {
                    mMessage += (String) args[0] + "\n";
                    Log.d(TAG, "call() called with: args = [" + mMessage + "]");
                    //ejecucion en la UI
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            mTviMessage.setText(mMessage);
                            mSviContainer.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }
            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override public void call(Object... args) {

                }
            });
            if (!mSocket.connected()) {
                mSocket.connect();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {

    }

}
