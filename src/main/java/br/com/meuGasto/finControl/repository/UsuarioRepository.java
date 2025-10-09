package br.com.meuGasto.finControl.repository;

import br.com.meuGasto.finControl.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Buscar usuário por email
    Optional<Usuario> findByEmail(String email);
    
    // Verificar se existe usuário com email
    boolean existsByEmail(String email);
    
    // Buscar usuário ativo por email
    Optional<Usuario> findByEmailAndAtivoTrue(String email);
}
