package br.com.taok.bizu.service;

import br.com.taok.bizu.model.Bem;
import br.com.taok.bizu.model.Candidatura;
import br.com.taok.bizu.model.Cassacao;
import br.com.taok.bizu.model.Municipio;
import br.com.taok.bizu.tse.model.EleicoesCSV;
import br.com.taok.bizu.tse.service.coleta.LeitorCSV;
import io.quarkus.panache.common.Sort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Dependent
public class CandidaturaService {

    Logger log = LoggerFactory.getLogger(CandidaturaService.class);

    @Inject
    LeitorCSV leitorCSV;

    public void coletaEleicaoGeralViaCSV(Integer anoEleicao){
        EleicoesCSV eleicoesCSV = EleicoesCSV.encontraPorAnoEleitoral(anoEleicao);
        List<Candidatura> candidaturas = leitorCSV.lerCSV(eleicoesCSV.pathCandidatos("CE"), eleicoesCSV.getAnoEleicao());
        List<Bem> bens = leitorCSV.lerCSVBem(eleicoesCSV.pathCandidatosBens("CE"));
        List<Cassacao> cassacoes = leitorCSV.lerCSVCassacao(eleicoesCSV.pathCandidatosCassacao("CE"));

        candidaturas.parallelStream().forEach(candidatura -> {
            candidatura.setUrlFoto(eleicoesCSV.getUrlFotoPorCandidato(candidatura));
            bens.stream().filter(b -> b.getCodigoCandidato().equals(candidatura.getCodigoCandidato()))
                    .forEach(candidatura::adicionaBem);

            cassacoes.stream()
                    .filter(cassacao -> cassacao.getCodigoCandidato().equals(candidatura.getCodigoCandidato()))
                    .forEach(candidatura::adicionarCassacao);
        });

        Candidatura.persist(candidaturas);
        log.info("Finalizando importação, total importado={} ", candidaturas.size());
    }

    public List<Candidatura> candidaturas(CandidaturaFilter candidaturaFilter, int page){

        return candidaturaFilter
                .getListagemFiltrada(page).stream()
                    .sorted(Comparator.comparing(Candidatura::valorTotalDeBens).reversed())
                    .collect(Collectors.toList());
    }

    public List<String> municipios(){
        return Candidatura.findAll(Sort.by("municipioEleicao"))
                .project(Municipio.class)
                .stream()
                    .filter(m -> m.getMunicipioEleicao() != null)
                    .map(Municipio::getMunicipioEleicao)
                    .distinct()
                    .collect(Collectors.toList());
    }
}
