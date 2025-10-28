package br.com.meuGasto.finControl.service;

import br.com.meuGasto.finControl.entity.PlanejamentoMensal;
import br.com.meuGasto.finControl.entity.PlanejamentoCategoria;
import br.com.meuGasto.finControl.repository.PlanejamentoMensalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
public class PlanejamentoService {

    private final PlanejamentoMensalRepository planejamentoRepository;

    public PlanejamentoService(PlanejamentoMensalRepository planejamentoRepository) {
        this.planejamentoRepository = planejamentoRepository;
    }

    @Transactional
    public PlanejamentoMensal criar(PlanejamentoMensal planejamento) {
        // Garante que a data está configurada corretamente
        if (planejamento.getData() == null && planejamento.getMesAno() != null) {
            planejamento.setData(planejamento.getMesAno().atDay(1));
        }
        planejamento.getCategorias().forEach(cat -> cat.setPlanejamentoMensal(planejamento));
        return planejamentoRepository.save(planejamento);
    }

    @Transactional
    public PlanejamentoMensal atualizar(Long id, PlanejamentoMensal planejamento) {
        if (!planejamentoRepository.existsById(id)) {
            throw new RuntimeException("Planejamento não encontrado");
        }
        planejamento.setId(id);
        planejamento.getCategorias().forEach(cat -> cat.setPlanejamentoMensal(planejamento));
        return planejamentoRepository.save(planejamento);
    }

    public Optional<PlanejamentoMensal> buscarPorId(Long id) {
        return planejamentoRepository.findById(id);
    }

    public Optional<PlanejamentoMensal> buscarPorMesAno(YearMonth mesAno) {
        LocalDate primeiroDia = mesAno.atDay(1);
        LocalDate ultimoDia = mesAno.atEndOfMonth();
        return planejamentoRepository.findByDataBetween(primeiroDia, ultimoDia);
    }

    public List<PlanejamentoMensal> listarTodos() {
        return planejamentoRepository.findAll();
    }

    @Transactional
    public void excluir(Long id) {
        planejamentoRepository.deleteById(id);
    }

    @Transactional
    public void adicionarCategoria(Long planejamentoId, PlanejamentoCategoria categoria) {
        PlanejamentoMensal planejamento = planejamentoRepository.findById(planejamentoId)
            .orElseThrow(() -> new RuntimeException("Planejamento não encontrado"));

        categoria.setPlanejamentoMensal(planejamento);
        planejamento.addCategoria(categoria);
        planejamentoRepository.save(planejamento);
    }

    @Transactional
    public void removerCategoria(Long planejamentoId, Long categoriaId) {
        PlanejamentoMensal planejamento = planejamentoRepository.findById(planejamentoId)
            .orElseThrow(() -> new RuntimeException("Planejamento não encontrado"));

        planejamento.getCategorias().removeIf(cat -> cat.getId().equals(categoriaId));
        planejamentoRepository.save(planejamento);
    }

    /**
     * Vincula um gasto a um PlanejamentoMensal do mesmo mês (mes/ano). Se não existir
     * um planejamento para o mês do gasto, cria um novo planejamento e adiciona o gasto.
     * Este método atualiza ambos os lados da relação e persiste o planejamento.
     */
    @Transactional
    public void vincularGastoAoPlanejamento(java.time.YearMonth mesAno, br.com.meuGasto.finControl.entity.Gasto gasto) {
        // buscar planejamento existente para o mês
        Optional<PlanejamentoMensal> opt = buscarPorMesAno(mesAno);
        PlanejamentoMensal planejamento;
        if (opt.isPresent()) {
            planejamento = opt.get();
        } else {
            planejamento = new PlanejamentoMensal();
            planejamento.setMesAno(mesAno);
            planejamento.setData(mesAno.atDay(1));
            // criar persistente
            planejamento = criar(planejamento);
        }

        // vincular gasto ao planejamento
        planejamento.getGastos().add(gasto);
        gasto.setPlanejamentoMensal(planejamento);

        // persistir alterações
        planejamentoRepository.save(planejamento);
    }
}
