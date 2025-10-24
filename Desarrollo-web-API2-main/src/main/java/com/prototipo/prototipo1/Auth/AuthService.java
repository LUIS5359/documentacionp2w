package com.prototipo.prototipo1.Auth;

import com.prototipo.prototipo1.Auth.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthResponse login(AuthRequest req) {
        var usuario = usuarioRepository.findByEmailIgnoreCase(req.email()).orElseThrow();
        String jwtToken = jwt.generate(usuario.getEmail(), usuario.getRol().getNombreRol());
        return new AuthResponse(jwtToken);
    }

    public AuthResponse register(RegisterRequest req) {
        if (usuarioRepository.existsByEmailIgnoreCase(req.email()))
            throw new IllegalArgumentException("Email ya registrado");

        var rol = rolRepository.findByNombreRolIgnoreCase(req.rol())
                .orElseThrow(() -> new IllegalArgumentException("Rol no válido: " + req.rol()));

        Usuario u = new Usuario();
        u.setNombreUsuario(req.nombreUsuario());
        u.setEmail(req.email().trim().toLowerCase());
        u.setRol(rol);
        u.setPasswordHash(encoder.encode(req.password()));

        usuarioRepository.save(u);

        String token = jwt.generate(u.getEmail(), u.getRol().getNombreRol());
        return new AuthResponse(token);
    }
}
