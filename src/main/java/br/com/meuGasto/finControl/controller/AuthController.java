package br.com.meuGasto.finControl.controller;

import br.com.meuGasto.finControl.dto.LoginRequest;
import br.com.meuGasto.finControl.dto.LoginResponse;
import br.com.meuGasto.finControl.entity.Usuario;
import br.com.meuGasto.finControl.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:4200"})
public class AuthController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        return authenticateAndBuildResponse(request);
    }

    private ResponseEntity<?> authenticateAndBuildResponse(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = "dummy-token"; // Substitua por geração real de token se necessário

            return ResponseEntity.ok(new LoginResponse(
                "Login realizado com sucesso",
                token,
                authentication.getName()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", "Credenciais inválidas",
                "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid Usuario usuario) {
        try {
            if (usuarioService.existeUsuarioComEmail(usuario.getEmail())) {
                return ResponseEntity.badRequest().body(Map.of(
                    "message", "Email já está em uso!"
                ));
            }

            if (!usuarioService.validarUsuario(usuario)) {
                return ResponseEntity.badRequest().body(Map.of(
                    "message", "Dados inválidos!"
                ));
            }

            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
            Usuario savedUser = usuarioService.salvar(usuario);

            return ResponseEntity.ok(Map.of(
                "message", "Usuário registrado com sucesso!",
                "userId", savedUser.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", "Erro ao registrar usuário",
                "error", e.getMessage()
            ));
        }
    }
}
