package br.com.meuGasto.finControl.service;

import br.com.meuGasto.finControl.entity.Usuario;
import br.com.meuGasto.finControl.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");
        usuarioMock.setSenha("encodedPassword");
        usuarioMock.setAtivo(true);
    }

    @Test
    void loadUserByUsername_QuandoUsuarioExiste_DeveRetornarUserDetails() {
        when(usuarioRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(usuarioMock));

        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());
        verify(usuarioRepository).findByEmail("test@example.com");
    }

    @Test
    void loadUserByUsername_QuandoUsuarioNaoExiste_DeveLancarException() {
        when(usuarioRepository.findByEmail("nonexistent@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> 
            userDetailsService.loadUserByUsername("nonexistent@example.com")
        );
        verify(usuarioRepository).findByEmail("nonexistent@example.com");
    }
}
