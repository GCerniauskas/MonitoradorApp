package vox.com.br.dao;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import vox.com.br.model.AppAberto;

public class AppAbertoDao {

    private final static List<AppAberto> apps = new ArrayList<>();
    private final static List<AppAberto> historico = new ArrayList<>();

    public void salvar(AppAberto appAberto) {
        int verificandoLista = apps.size();

        if (verificandoLista == 0
                && appAberto.getNome().contains("Launcher")
                || Objects.equals(appAberto.getNome(), "System UI") // se n é igual a System UI
                || Objects.equals(appAberto.getNome(), "Gboard") // se n é igual a Gboard
                || Objects.equals(appAberto.getNome(), "monitoradorApp") // se n é o nosso app
                || Objects.equals(appAberto.getNome(), "Google")  // se n é o Google
        ) {
            return;
        }

        // Aqui verificamos se a lista foi iniciada, se é igual zero quer dizer que o app está iniciando pela primeira vez
        if (verificandoLista == 0) {
            apps.add(appAberto);
        } else {
            int tamanhoAtualDaLista = apps.size();
            String nomeDoAppAtual = appAberto.getNome();
            String ultimoAppAberto = apps.get(tamanhoAtualDaLista - 1).getNome();

            // Verificando se o item já existe na lista, se existir eu apago ele e substituo pelo novo alterando sua quantidade de vezes aberto pra +1
            if (apps.contains(appAberto)) {
                int indexDoApp = apps.indexOf(appAberto);
                AppAberto appASerModificado = apps.get(indexDoApp);
                AppAberto appAbertoModificado = new AppAberto(appASerModificado, appASerModificado.getDigitado(), appAberto.getData(), appAberto.getCalendar(), appASerModificado.getQuantidadeDeVezesAberto() + 1);
                apps.set(indexDoApp, appAbertoModificado);
            } else if ( // Se o item não existe preciso verificar se não está colocando itens que não são um "app"
                    !apps.contains(appAberto) && // se não existir na lista
                            !Objects.equals(ultimoAppAberto, nomeDoAppAtual) // se não é igual a ultimo app Aberto
                            && !Objects.equals(nomeDoAppAtual, "System UI") // se n é igual a System UI
                            && !nomeDoAppAtual.contains("Launcher") // se não é algum tipo de Launcher do Android
                            && !Objects.equals(nomeDoAppAtual, "Gboard") // se n é igual a Gboard
                            && !Objects.equals(nomeDoAppAtual, "monitoradorApp") // se n é o nosso app
                            && !Objects.equals(nomeDoAppAtual, "Google")) { // se n é o Google
                apps.add(appAberto);
            }
        }
    }

    public void atualizarDigitados(AppAberto appAberto) {

        // Algoritmo para saber o index do app da lista apps
        int indexDoApp = IntStream.range(0, apps.size())
                .filter(i -> apps.get(i).getNome().equals(appAberto.getNome()))
                .findFirst()
                .orElse(-1);

        if (indexDoApp == -1) return;

        AppAberto _apps = apps.get(indexDoApp);
        List<String> appsListaDeDigitado = _apps.getDigitado();
        if (appAberto.getDigitado().get(0) != null) {
            appsListaDeDigitado.add(appAberto.getDigitado().get(0));
        } else {
            Log.e(TAG, "atualizarDigitados: algo deu errado aqui :/");
        }

        AppAberto appModificado = new AppAberto(_apps, appsListaDeDigitado);


        // Substituindo
        apps.set(indexDoApp, appModificado);

    }

    // Funções do Apps
    public List<AppAberto> getTodos() {
        return new ArrayList<>(apps);
    }

    public void atualizarHorario(@NonNull AppAberto ultimoAppAberto, AppAberto appAbertoAtual) {
        Duration duration = Duration.between(appAbertoAtual.getCalendar().toInstant(), ultimoAppAberto.getCalendar().toInstant());
        Log.e(TAG, "onAtualizarHorario: Horário 1: " + appAbertoAtual.getCalendar().toInstant() + " | Horário 2: " + ultimoAppAberto.getCalendar().toInstant());

        // Achando o Index do app
        int indexDoApp = IntStream.range(0, apps.size())
                .filter(i -> apps.get(i).getNome().equals(ultimoAppAberto.getNome()))
                .findFirst()
                .orElse(-1);

        // Se o app a ser alterado, tiver usage_status vou somar os dois, caso ao contrario só adicionar.
        if (apps.get(indexDoApp).getUsage_status() != null) {

            // Lista das duas durações e fazendo o calculo delas somadas
            List<Duration> durations = List.of(duration, apps.get(indexDoApp).getUsage_status());
            Duration sum = Duration.ZERO;
            for (Duration dur : durations) {
                sum = sum.plus(dur);
            }

            AppAberto appASerModificado = new AppAberto(ultimoAppAberto, sum);
            apps.set(indexDoApp, appASerModificado);
        } else {
            AppAberto appASerModificado = new AppAberto(ultimoAppAberto, duration);
            apps.set(indexDoApp, appASerModificado);
        }
    }

    // Funções do Historico
    public List<AppAberto> getTodosHistorico() {
        return new ArrayList<>(historico);
    }

    public void salvarNoHistorico(AppAberto appAberto) {
        historico.add(appAberto);
    }

}
