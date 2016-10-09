package core.base;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import core.utils.ConexaoInternet;
import nunes.diogo.asdtaskmanagerapp.R;

/**
 * Created by Diogo on 23/07/2016.
 */
public class BaseActivity extends AppCompatActivity
{
    //Dispara uma mensagem na tela
    public void ShowMessage(String mensagem){

        Toast.makeText(getApplicationContext(),mensagem,Toast.LENGTH_LONG).show();
    }

    //Dispara uma mensagem na tela
    public void ShowMessage(String mensagem, Boolean isBaseContext){

        Toast.makeText(isBaseContext ? getBaseContext() : getApplicationContext(),mensagem,Toast.LENGTH_LONG).show();
    }

    //Verifica a conexão com a internet
    public Boolean IsConexaoAtiva()
    {
        if(!new ConexaoInternet(this).IsAtiva()){
           ShowMessage( getResources().getString(R.string.conexaoInativa));
           return false;
        }
        else return true;
    }
}
