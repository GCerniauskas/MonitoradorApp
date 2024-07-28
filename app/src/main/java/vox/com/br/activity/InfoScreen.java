package vox.com.br.activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.time.Duration;

import vox.com.br.R;
import vox.com.br.dao.AppAbertoDao;
import vox.com.br.model.AppAberto;

public class InfoScreen extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_screen);

        // Resgatando o parâmetro passado da tela anterior
        Bundle extras = getIntent().getExtras();
        int indexDoAppAberto = extras.getInt("key");

        TextView tempoUsado = findViewById(R.id.info_screen_tempo_de_uso_alterar);
        TextView nomeDoApp = findViewById(R.id.info_screen_nome_do_app_alterar);
        TextView textViewQtdVezesAberta = findViewById(R.id.qtd_vezes_aberta_alterar);
        ListView listView = findViewById(R.id.info_screen_listView);

        // Criando uma instância do AppAbertoDao e resgatando informações para construção do UI
        AppAbertoDao dao = new AppAbertoDao();
        AppAberto appAberto = dao.getTodos().get(indexDoAppAberto);
        long duration = appAberto.getUsage_status().getSeconds();
        String durationFomated = String.format("%d:%02d:%02d", duration / 3600, (duration % 3600) / 60, (duration % 60));
        String qtdDeVezesAberta = String.valueOf(appAberto.getQuantidadeDeVezesAberto());


        tempoUsado.setText(durationFomated);
        nomeDoApp.setText(appAberto.getNome());
        textViewQtdVezesAberta.setText(qtdDeVezesAberta);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appAberto.getDigitado()));


    }


}
