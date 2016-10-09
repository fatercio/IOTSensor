package layout.UserActivity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;

import core.models.JSONResponse;
import nunes.diogo.asdtaskmanagerapp.R;
import core.base.BaseActivity;
import core.service.ServicesControl;

public class MainActivity extends BaseActivity {

    //Usuário utiliziado para a validação
    int min ;
    int max ;
    JSONResponse JSON;
    Thread APIThreadInit;
    Thread APIThread;

    Boolean checkSensor = true;
    MainActivity main;
    int sensorValue;
    int status;
    String temperatura;
    String LimiteSugerido;
    String limiteDefinido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        main = this;

        //Componentes
        final Button btnParar = (Button)findViewById(R.id.btnParar);
        final Button btnDefinir = (Button)findViewById(R.id.btnDefinir);
        final CheckBox ckMonitor = (CheckBox)findViewById(R.id.ckMonitor);
        final TextView textViewTemperatura = (TextView) findViewById(R.id.txtTemperatura);
        final TextView txtLimiteSugerido = (TextView) findViewById(R.id.txtLimiteSugerido);
        final TextView txtLimiteDefinido= (TextView) findViewById(R.id.txtLimiteDefinido);
        final EditText txtTemperaturaDefinida = (EditText) findViewById(R.id.txtTemperaturaDefinida);

        APIThreadInit = new Thread(new Runnable() {
            public void run() {

                while (true) {
                   //Acesso ao serviço
                    try {

                        JSON = new ServicesControl().new SensorFeedAPI().Get();
                        if (validFeed(JSON)) {
                            temperatura = JSON.lastFeed.field1 !=null? JSON.lastFeed.field1.toString(): "0";
                            status = JSON.lastFeed.field2 !=null? Integer.parseInt(JSON.lastFeed.field2): 0;
                            limiteDefinido = JSON.lastFeed.field3 !=null? JSON.lastFeed.field3.toString():"0";
                            LimiteSugerido = JSON.lastFeed.field4 !=null? JSON.lastFeed.field4.toString():"0";

                            if (status == 1) {
                                main.runOnUiThread(new Runnable() {
                                    public void run() {
                                        main.ShowMessage("Alerta: Equipamento Ligado. Status code: " + status,true);
                                        textViewTemperatura.setText(temperatura);
                                        txtLimiteSugerido.setText(LimiteSugerido);
                                        txtTemperaturaDefinida.setEnabled(true);
                                        txtLimiteDefinido.setText(limiteDefinido);
                                        btnDefinir.setEnabled(true);
                                        ckMonitor.setChecked(true);
                                    }
                                });
                            }else if (status == 0){
                                main.runOnUiThread(new Runnable() {
                                    public void run() {
                                        main.ShowMessage("Alerta: Equipamento Desligado. Status code: " + status,true);
                                        textViewTemperatura.setText(temperatura);
                                        txtLimiteSugerido.setText(LimiteSugerido);
                                        txtLimiteDefinido.setText(limiteDefinido);
                                        ckMonitor.setChecked(false);

                                    }
                                });
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        APIThreadInit.start();

        //Activity corrente - para ser usado dentro da Thread de login
        final MainActivity thatActivity = this;

        //Click do botão parar
        btnParar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnParar.setVisibility(View.VISIBLE);

                checkSensor = false;

                APIThreadInit.stop();
                APIThreadInit.interrupt();
            }});

                //Click do botão logar
        ckMonitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                APIThread = new Thread(new Runnable() {
                    public void run() {
                        //Acesso ao serviço
                        try {
                            if(ckMonitor.isChecked()){
                               // ckMonitor.setChecked(true);
                                JSON = new ServicesControl().new SensorFeedAPI().Post("power=on");
                            }else {
                               // ckMonitor.setChecked(false);
                                JSON = new ServicesControl().new SensorFeedAPI().Post("power=off");
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            Thread.sleep(5000);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                });
                APIThread.start();

            }
        });

        btnDefinir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!IsConexaoAtiva())
                        return;


                        APIThread = new Thread(new Runnable() {
                            public void run() {
                                    try {
                                            JSON = new ServicesControl().new SensorFeedAPI().Post("limit="+ txtTemperaturaDefinida.getText());
                                        main.runOnUiThread(new Runnable() {
                                            public void run() {
                                                txtTemperaturaDefinida.setText("");
                                            }
                                        });


                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        Thread.sleep(5000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                               }

                        });

                    //Inicia a Thread da API
                    APIThread.start();


                }catch (Exception ex)
                {
                    ShowMessage(ex.getMessage());
                }
            }});

    }
    private boolean validFeed(JSONResponse JSON) {
        boolean valid = false;
        if(JSON.lastFeed != null && JSON.lastFeed.field1 !=null && JSON.lastFeed.field2 != null && JSON.lastFeed .field3 !=null && JSON.lastFeed.field4 !=null){
            valid = true;
        }
        return valid;
    }
}