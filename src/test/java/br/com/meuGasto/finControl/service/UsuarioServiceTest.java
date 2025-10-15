package br.com.meuGasto.finControl.service;

import br.com.meuGasto.finControl.entity.Usuario;
import br.com.meuGasto.finControl.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setNome("Test User");
        usuarioMock.setEmail("test@example.com");
        usuarioMock.setSenha("password123");
        usuarioMock.setAtivo(true);
        usuarioMock.setDataCriacao(LocalDateTime.now());
        usuarioMock.setDataAtualizacao(LocalDateTime.now());
    }

    @Test
    void salvar_DeveRetornarUsuarioSalvo() {
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        Usuario resultado = usuarioService.salvar(usuarioMock);

        assertNotNull(resultado);
        assertEquals(usuarioMock.getEmail(), resultado.getEmail());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void listarTodos_DeveRetornarListaDeUsuarios() {
        List<Usuario> usuarios = Arrays.asList(usuarioMock);
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        List<Usuario> resultado = usuarioService.listarTodos();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(usuarioRepository).findAll();
    }

    @Test
    void buscarPorId_QuandoExiste_DeveRetornarUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));

        Optional<Usuario> resultado = usuarioService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(usuarioMock.getId(), resultado.get().getId());
        verify(usuarioRepository).findById(1L);
    }

    @Test
    void buscarPorEmail_QuandoExiste_DeveRetornarUsuario() {
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(usuarioMock));

        Optional<Usuario> resultado = usuarioService.buscarPorEmail("test@example.com");

        assertTrue(resultado.isPresent());
        assertEquals(usuarioMock.getEmail(), resultado.get().getEmail());
        verify(usuarioRepository).findByEmail("test@example.com");
    }

    @Test
    void buscarPorEmailAtivo_QuandoExiste_DeveRetornarUsuario() {
        when(usuarioRepository.findByEmailAndAtivoTrue("test@example.com")).thenReturn(Optional.of(usuarioMock));

        Optional<Usuario> resultado = usuarioService.buscarPorEmailAtivo("test@example.com");

        assertTrue(resultado.isPresent());
        assertEquals(usuarioMock.getEmail(), resultado.get().getEmail());
        assertTrue(resultado.get().getAtivo());
        verify(usuarioRepository).findByEmailAndAtivoTrue("test@example.com");
    }

    @Test
    void atualizar_QuandoExiste_DeveAtualizarUsuario() {
        Usuario usuarioAtualizado = new Usuario();
        usuarioAtualizado.setNome("Updated Name");
        usuarioAtualizado.setEmail("updated@example.com");
        
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        Usuario resultado = usuarioService.atualizar(1L, usuarioAtualizado);

        assertNotNull(resultado);
        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void atualizar_QuandoNaoExiste_DeveLancarException() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            usuarioService.atualizar(1L, usuarioMock)
        );
        verify(usuarioRepository).findById(1L);
    }

    @Test
    void desativar_QuandoExiste_DeveDesativarUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        usuarioService.desativar(1L);

        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void ativar_QuandoExiste_DeveAtivarUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        usuarioService.ativar(1L);

        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void existeUsuarioComEmail_QuandoExiste_DeveRetornarTrue() {
        when(usuarioRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean resultado = usuarioService.existeUsuarioComEmail("test@example.com");

        assertTrue(resultado);
        verify(usuarioRepository).existsByEmail("test@example.com");
    }

    @Test
    void validarUsuario_QuandoValido_DeveRetornarTrue() {
        boolean resultado = usuarioService.validarUsuario(usuarioMock);

        assertTrue(resultado);
    }

    @Test
    void validarUsuario_QuandoInvalido_DeveRetornarFalse() {
        Usuario usuarioInvalido = new Usuario();
        boolean resultado = usuarioService.validarUsuario(usuarioInvalido);

        assertFalse(resultado);
    }

    @Test
    void getEmailUsuarioLogado_QuandoAutenticado_DeveRetornarEmail() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        String resultado = usuarioService.getEmailUsuarioLogado();

        assertEquals("test@example.com", resultado);
    }

    @Test
    void excluir_QuandoExiste_DeveExcluirUsuario() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(1L);

        usuarioService.excluir(1L);

        verify(usuarioRepository).existsById(1L);
        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    void excluir_QuandoNaoExiste_DeveLancarException() {
        when(usuarioRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> 
            usuarioService.excluir(1L)
        );
        verify(usuarioRepository).existsById(1L);
        verify(usuarioRepository, never()).deleteById(any());
    }
}
