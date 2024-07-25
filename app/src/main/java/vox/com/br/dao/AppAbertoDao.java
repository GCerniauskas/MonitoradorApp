package vox.com.br.dao;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import vox.com.br.model.AppAberto;

public class AppAbertoDao {

    private final static List<AppAberto> apps = new ArrayList<>();

    public void salvar(AppAberto appAberto) {
        int verificandoLista = apps.size();
        if (verificandoLista == 0) {
            apps.add(appAberto);
        }
        int tamanhoAtualDaLista = apps.size();
        String nomeDoAppAtual = appAberto.getNome();
        String ultimoAppAberto = apps.get(tamanhoAtualDaLista - 1).getNome();

        // Verificando se o item já existe na lista, se existir eu apago ele e substituo pelo novo
        if (apps.contains(appAberto)) {
            int indexDoApp = apps.indexOf(appAberto);
            AppAberto appASerModificado = apps.get(indexDoApp);
            AppAberto appAbertoModificado = new AppAberto(appAberto, appASerModificado.getQuantidadeDeVezesAberto() + 1);
            apps.set(indexDoApp, appAbertoModificado);

        }
        // Se o item não existe preciso verificar se não está colocando itens que não são um "app"
        else if (
                !apps.contains(appAberto) && // se não existir na lista
                !Objects.equals(ultimoAppAberto, nomeDoAppAtual) // se não é igual a ultimo app Aberto
                && !Objects.equals(nomeDoAppAtual, "System UI") // se n é igual a System UI
                && !Objects.equals(nomeDoAppAtual, "Pixel Launcher") // se n é igual a PixelLaucher
                && !Objects.equals(nomeDoAppAtual, "monitoradorApp") // se n é o nosso app
                && !Objects.equals(nomeDoAppAtual, "Google")) { // se n é o Google
            apps.add(appAberto);
        }

        // Verificar quando saiu do app e atualizar o horário.

//        // Se o app que está sendo adicionado agora é igual o que já tem no último item da lista
//        if (!Objects.equals(ultimoAppAberto, nomeDoAppAtual)
//                && !Objects.equals(nomeDoAppAtual, "System UI")
//                && !Objects.equals(nomeDoAppAtual, "Pixel Launcher")
//                && !Objects.equals(nomeDoAppAtual, "monitoradorApp")
//                && !Objects.equals(nomeDoAppAtual, "Google")) {
//            apps.add(appAberto);
//        }
    }

    public List<AppAberto> getTodos() {
        return new ArrayList<>(apps);
    }



    public AppAberto getAppById(int id) {
        return apps.get(id);
    }

    public void atualizarHorario(AppAberto ultimoAppAberto, AppAberto appAbertoAtual) {
        Date minutes = new Date(Duration.between(appAbertoAtual.getCalendar().toInstant(), ultimoAppAberto.getCalendar().toInstant()).toMinutes());
        AppAberto appASerModificado = new AppAberto(appAbertoAtual, minutes);
        int indexDoApp = apps.indexOf(appAbertoAtual);
        apps.set(indexDoApp, appASerModificado);
    }
}
