package br.com.alura.AluraFake.infra.security;

import br.com.alura.AluraFake.user.Role;

public record LoginResponseDTO(Long id, String user, Role role, String token) {
}
