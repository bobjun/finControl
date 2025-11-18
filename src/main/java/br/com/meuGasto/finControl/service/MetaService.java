package br.com.meuGasto.finControl.service;

import br.com.meuGasto.finControl.entity.Meta;
import br.com.meuGasto.finControl.repository.MetaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MetaService {
    private static final Logger log = LoggerFactory.getLogger(MetaService.class);

    private final MetaRepository metaRepository;

    public Meta salvar(Meta meta) {
        if (meta.getDataInicio() == null) {
            meta.setDataInicio(LocalDateTime.now());
        }
        if (meta.getValorAtual() == null) {
            meta.setValorAtual(BigDecimal.ZERO);
        }
        Meta salvo = metaRepository.save(meta);
        return salvo;
    }

    @Transactional(readOnly = true)
    public List<Meta> listarTodos() {
        return metaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Meta> buscarPorId(Long id) {
        return metaRepository.findById(id);
    }

    public Meta atualizar(Long id, Meta metaAtualizado) {
        return metaRepository.findById(id)
                .map(metaExistente -> {
                    metaExistente.setDescricao(metaAtualizado.getDescricao());
                    metaExistente.setValorObjetivo(metaAtualizado.getValorObjetivo());
                    metaExistente.setValorAtual(metaAtualizado.getValorAtual());
                    metaExistente.setDataInicio(metaAtualizado.getDataInicio());
                    metaExistente.setDataFim(metaAtualizado.getDataFim());
                    metaExistente.setRecorrente(metaAtualizado.isRecorrente());
                    metaExistente.setTipo(metaAtualizado.getTipo());
                    metaExistente.setDataAtualizacao(LocalDateTime.now());
                    return metaRepository.save(metaExistente);
                })
                .orElseThrow(() -> new RuntimeException("Meta não encontrada com ID: " + id));
    }

    public void excluir(Long id) {
        if (!metaRepository.existsById(id)) {
            throw new RuntimeException("Meta não encontrada com ID: " + id);
        }
        metaRepository.deleteById(id);
    }

    public boolean validarMeta(Meta meta) {
        if (meta.getDescricao() == null || meta.getDescricao().trim().isEmpty()) return true;
        if (meta.getValorObjetivo() == null || meta.getValorObjetivo().compareTo(BigDecimal.ZERO) <= 0) return true;
        return false;
    }
}
