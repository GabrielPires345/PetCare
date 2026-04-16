package com.petcare.service;

import com.petcare.mapper.VeterinarioMapper;
import com.petcare.mapper.request.VeterinarioRequest;
import com.petcare.mapper.response.VeterinarioResponse;
import com.petcare.model.Especialidade;
import com.petcare.model.Usuario;
import com.petcare.model.Veterinario;
import com.petcare.repository.EspecialidadeRepository;
import com.petcare.repository.UsuarioRepository;
import com.petcare.repository.VeterinarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VeterinarioServiceTest {

    @Mock private VeterinarioRepository veterinarioRepository;
    @Mock private EspecialidadeRepository especialidadeRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private VeterinarioService veterinarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateVeterinario() {
        UUID espId = UUID.randomUUID();
        VeterinarioRequest request = new VeterinarioRequest("Dr Silva", "CRMV123", Set.of(espId));
        Especialidade esp = Especialidade.builder().id(espId).nome("Cirurgia").build();

        when(passwordEncoder.encode("CRMV123")).thenReturn("encoded");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> Usuario.builder().id(UUID.randomUUID()).build());
        when(especialidadeRepository.findById(espId)).thenReturn(Optional.of(esp));
        when(veterinarioRepository.save(any(Veterinario.class))).thenAnswer(inv -> {
            Veterinario v = inv.getArgument(0);
            return Veterinario.builder().id(UUID.randomUUID()).nome(v.getNome()).crmv(v.getCrmv()).especialidades(v.getEspecialidades()).build();
        });

        VeterinarioResponse result = veterinarioService.createVeterinario(request);

        assertNotNull(result);
        assertEquals("Dr Silva", result.nome());
        assertEquals("CRMV123", result.crmv());
        verify(usuarioRepository).save(any(Usuario.class));
        verify(veterinarioRepository).save(any(Veterinario.class));
    }

    @Test
    void shouldCreateVeterinarioWithoutSpecialties() {
        VeterinarioRequest request = new VeterinarioRequest("Dr NoSpec", "CRMV000", null);

        when(passwordEncoder.encode("CRMV000")).thenReturn("encoded");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> Usuario.builder().id(UUID.randomUUID()).build());
        when(veterinarioRepository.save(any(Veterinario.class))).thenAnswer(inv -> {
            Veterinario v = inv.getArgument(0);
            return Veterinario.builder().id(UUID.randomUUID()).nome(v.getNome()).crmv(v.getCrmv()).especialidades(v.getEspecialidades()).build();
        });

        VeterinarioResponse result = veterinarioService.createVeterinario(request);

        assertEquals("Dr NoSpec", result.nome());
    }

    @Test
    void shouldReturnVeterinarioById() {
        UUID id = UUID.randomUUID();
        Veterinario vet = Veterinario.builder().id(id).nome("Dr Test").crmv("CRMV999").especialidades(new HashSet<>()).build();
        when(veterinarioRepository.findById(id)).thenReturn(Optional.of(vet));

        VeterinarioResponse result = veterinarioService.getVeterinarioById(id);

        assertEquals("Dr Test", result.nome());
        assertEquals("CRMV999", result.crmv());
    }

    @Test
    void shouldThrowWhenVeterinarioNotFound() {
        when(veterinarioRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> veterinarioService.getVeterinarioById(UUID.randomUUID()));
    }

    @Test
    void shouldReturnAllVeterinarios() {
        Veterinario v1 = Veterinario.builder().id(UUID.randomUUID()).nome("A").crmv("1").especialidades(new HashSet<>()).build();
        Veterinario v2 = Veterinario.builder().id(UUID.randomUUID()).nome("B").crmv("2").especialidades(new HashSet<>()).build();
        when(veterinarioRepository.findAll()).thenReturn(List.of(v1, v2));

        List<VeterinarioResponse> result = veterinarioService.getAllVeterinarios();

        assertEquals(2, result.size());
    }

    @Test
    void shouldUpdateVeterinario() {
        UUID id = UUID.randomUUID();
        Veterinario existing = Veterinario.builder().id(id).nome("Old").crmv("CRMV001").especialidades(new HashSet<>()).build();
        VeterinarioRequest request = new VeterinarioRequest("Dr Updated", "CRMV999", null);
        when(veterinarioRepository.findById(id)).thenReturn(Optional.of(existing));
        when(veterinarioRepository.save(any(Veterinario.class))).thenAnswer(inv -> inv.getArgument(0));

        VeterinarioResponse result = veterinarioService.updateVeterinario(id, request);

        assertEquals("Dr Updated", result.nome());
        assertEquals("CRMV999", result.crmv());
    }

    @Test
    void shouldUpdateVeterinarioEspecialidades() {
        UUID id = UUID.randomUUID();
        UUID espId = UUID.randomUUID();
        Veterinario existing = Veterinario.builder().id(id).nome("Dr").crmv("CRMV1").especialidades(new HashSet<>()).build();
        Especialidade esp = Especialidade.builder().id(espId).nome("Cardiologia").build();
        VeterinarioRequest request = new VeterinarioRequest("Dr", "CRMV1", Set.of(espId));
        when(veterinarioRepository.findById(id)).thenReturn(Optional.of(existing));
        when(especialidadeRepository.findById(espId)).thenReturn(Optional.of(esp));
        when(veterinarioRepository.save(any(Veterinario.class))).thenAnswer(inv -> inv.getArgument(0));

        VeterinarioResponse result = veterinarioService.updateVeterinario(id, request);

        assertTrue(result.especialidadeIds().contains(espId));
    }

    @Test
    void shouldThrowWhenUpdatingNonExistentVeterinario() {
        when(veterinarioRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> veterinarioService.updateVeterinario(UUID.randomUUID(), new VeterinarioRequest("X", "Y", null)));
    }

    @Test
    void shouldDeleteVeterinario() {
        UUID id = UUID.randomUUID();
        when(veterinarioRepository.existsById(id)).thenReturn(true);

        veterinarioService.deleteVeterinario(id);

        verify(veterinarioRepository).deleteById(id);
    }

    @Test
    void shouldThrowWhenDeletingNonExistentVeterinario() {
        when(veterinarioRepository.existsById(any())).thenReturn(false);
        assertThrows(RuntimeException.class, () -> veterinarioService.deleteVeterinario(UUID.randomUUID()));
    }
}
