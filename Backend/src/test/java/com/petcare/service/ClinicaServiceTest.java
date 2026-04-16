package com.petcare.service;

import com.petcare.mapper.ClinicaMapper;
import com.petcare.mapper.request.ClinicaRequest;
import com.petcare.mapper.response.ClinicaResponse;
import com.petcare.model.Clinica;
import com.petcare.model.Usuario;
import com.petcare.repository.ClinicaRepository;
import com.petcare.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClinicaServiceTest {

    @Mock private ClinicaRepository clinicaRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClinicaService clinicaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateClinica() {
        ClinicaRequest request = new ClinicaRequest("Pet Clinic", "Pet Clinic LTDA", "12345678000100");

        when(passwordEncoder.encode("12345678000100")).thenReturn("encoded");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> Usuario.builder().id(UUID.randomUUID()).build());
        when(clinicaRepository.save(any(Clinica.class))).thenAnswer(inv -> {
            Clinica c = inv.getArgument(0);
            return Clinica.builder().id(UUID.randomUUID()).nomeClinica(c.getNomeClinica()).razaoSocial(c.getRazaoSocial()).cnpj(c.getCnpj()).build();
        });

        ClinicaResponse result = clinicaService.createClinica(request);

        assertNotNull(result);
        assertEquals("Pet Clinic", result.nomeClinica());
        assertEquals("Pet Clinic LTDA", result.razaoSocial());
        assertEquals("12345678000100", result.cnpj());
        verify(usuarioRepository).save(any(Usuario.class));
        verify(clinicaRepository).save(any(Clinica.class));
    }

    @Test
    void shouldReturnClinicaById() {
        UUID id = UUID.randomUUID();
        Clinica clinica = Clinica.builder().id(id).nomeClinica("My Clinic").razaoSocial("My Corp").cnpj("00000000000100").build();
        when(clinicaRepository.findById(id)).thenReturn(Optional.of(clinica));

        ClinicaResponse result = clinicaService.getClinicaById(id);

        assertEquals("My Clinic", result.nomeClinica());
        assertEquals("My Corp", result.razaoSocial());
    }

    @Test
    void shouldThrowWhenClinicaNotFound() {
        when(clinicaRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> clinicaService.getClinicaById(UUID.randomUUID()));
    }

    @Test
    void shouldReturnAllClinicas() {
        Clinica c1 = Clinica.builder().id(UUID.randomUUID()).nomeClinica("A").build();
        Clinica c2 = Clinica.builder().id(UUID.randomUUID()).nomeClinica("B").build();
        when(clinicaRepository.findAll()).thenReturn(List.of(c1, c2));

        List<ClinicaResponse> result = clinicaService.getAllClinicas();

        assertEquals(2, result.size());
    }

    @Test
    void shouldUpdateClinica() {
        UUID id = UUID.randomUUID();
        Clinica existing = Clinica.builder().id(id).nomeClinica("Old Clinic").razaoSocial("Old Corp").cnpj("111").build();
        ClinicaRequest request = new ClinicaRequest("New Clinic", "New Corp", "222");
        when(clinicaRepository.findById(id)).thenReturn(Optional.of(existing));
        when(clinicaRepository.save(any(Clinica.class))).thenAnswer(inv -> inv.getArgument(0));

        ClinicaResponse result = clinicaService.updateClinica(id, request);

        assertEquals("New Clinic", result.nomeClinica());
        assertEquals("New Corp", result.razaoSocial());
        assertEquals("222", result.cnpj());
    }

    @Test
    void shouldThrowWhenUpdatingNonExistentClinica() {
        when(clinicaRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> clinicaService.updateClinica(UUID.randomUUID(), new ClinicaRequest("X", "Y", "Z")));
    }

    @Test
    void shouldDeleteClinica() {
        UUID id = UUID.randomUUID();
        when(clinicaRepository.existsById(id)).thenReturn(true);

        clinicaService.deleteClinica(id);

        verify(clinicaRepository).deleteById(id);
    }

    @Test
    void shouldThrowWhenDeletingNonExistentClinica() {
        when(clinicaRepository.existsById(any())).thenReturn(false);
        assertThrows(RuntimeException.class, () -> clinicaService.deleteClinica(UUID.randomUUID()));
    }
}
