package br.com.meuGasto.finControl.service;

import br.com.meuGasto.finControl.entity.Usuario;
import br.com.meuGasto.finControl.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    /**
     * Salva um novo usuário (senha deve vir já criptografada)
     */
    public Usuario salvar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
    
    /**
     * Busca todos os usuários
     */
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }
    
    /**
     * Busca um usuário por ID
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }
    
    /**
     * Busca um usuário por email
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
    
    /**
     * Busca um usuário ativo por email
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmailAtivo(String email) {
        return usuarioRepository.findByEmailAndAtivoTrue(email);
    }
    
    /**
     * Atualiza um usuário existente (senha deve vir já criptografada)
     */
    public Usuario atualizar(Long id, Usuario usuarioAtualizado) {
        return usuarioRepository.findById(id)
                .map(usuarioExistente -> {
                    usuarioExistente.setNome(usuarioAtualizado.getNome());
                    usuarioExistente.setEmail(usuarioAtualizado.getEmail());
                    if (usuarioAtualizado.getSenha() != null && !usuarioAtualizado.getSenha().isEmpty()) {
                        usuarioExistente.setSenha(usuarioAtualizado.getSenha());
                    }
                    usuarioExistente.setAtivo(usuarioAtualizado.getAtivo());
                    usuarioExistente.setDataAtualizacao(java.time.LocalDateTime.now());
                    return usuarioRepository.save(usuarioExistente);
                })
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));
    }
    
    /**
     * Exclui um usuário por ID
     */
    public void excluir(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado com ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }
    
    /**
     * Desativa um usuário (soft delete)
     */
    public void desativar(Long id) {
        usuarioRepository.findById(id)
                .ifPresentOrElse(usuario -> {
                    usuario.setAtivo(false);
                    usuario.setDataAtualizacao(java.time.LocalDateTime.now());
                    usuarioRepository.save(usuario);
                }, () -> {
                    throw new RuntimeException("Usuário não encontrado com ID: " + id);
                });
    }
    
    /**
     * Ativa um usuário
     */
    public void ativar(Long id) {
        usuarioRepository.findById(id)
                .ifPresentOrElse(usuario -> {
                    usuario.setAtivo(true);
                    usuario.setDataAtualizacao(java.time.LocalDateTime.now());
                    usuarioRepository.save(usuario);
                }, () -> {
                    throw new RuntimeException("Usuário não encontrado com ID: " + id);
                });
    }
    
    /**
     * Verifica se existe usuário com email
     */
    public boolean existeUsuarioComEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
    
    /**
     * Valida se o usuário pode ser salvo
     */
    public boolean validarUsuario(Usuario usuario) {
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            return false;
        }
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            return false;
        }
        return !(usuario.getSenha() == null || usuario.getSenha().trim().isEmpty());
    }
    
    /**
     * Verifica se existe usuário com o ID
     */
    public boolean existeUsuario(Long id) {
        return usuarioRepository.existsById(id);
    }

    /**
     * Obtém o email do usuário logado
     */
    public String getEmailUsuarioLogado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return null;
    }
}
