package com.petcare.service;

import com.petcare.mapper.EspecialidadeMapper;
import com.petcare.mapper.request.EspecialidadeRequest;
import com.petcare.mapper.response.EspecialidadeResponse;
import com.petcare.model.Especialidade;
import com.petcare.repository.EspecialidadeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EspecialidadeServiceTest {

    @Mock private EspecialidadeRepository especialidadeRepository;

    @InjectMocks
    private EspecialidadeService especialidadeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateEspecialidade() {
        EspecialidadeRequest request = new EspecialidadeRequest("Cirurgia");
        when(especialidadeRepository.save(any(Especialidade.class))).thenAnswer(inv -> {
            Especialidade e = inv.getArgument(0);
            return Especialidade.builder().id(UUID.randomUUID()).nome(e.getNome()).build();
        });

        EspecialidadeResponse result = especialidadeService.criarEspecialidade(request);

        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals("Cirurgia", result.nome());
    }

    @Test
    void shouldReturnEspecialidadeById() {
        UUID id = UUID.randomUUID();
        Especialidade esp = Especialidade.builder().id(id).nome("Cardiologia").build();
        when(especialidadeRepository.findById(id)).thenReturn(Optional.of(esp));

        EspecialidadeResponse result = especialidadeService.getEspecialidadeById(id);

        assertEquals("Cardiologia", result.nome());
    }

    @Test
    void shouldThrowWhenEspecialidadeNotFound() {
        when(especialidadeRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> especialidadeService.getEspecialidadeById(UUID.randomUUID()));
    }

    @Test
    void shouldReturnAllEspecialidades() {
        Especialidade e1 = Especialidade.builder().id(UUID.randomUUID()).nome("A").build();
        Especialidade e2 = Especialidade.builder().id(UUID.randomUUID()).nome("B").build();
        when(especialidadeRepository.findAll()).thenReturn(List.of(e1, e2));

        List<EspecialidadeResponse> result = especialidadeService.getAllEspecialidades();

        assertEquals(2, result.size());
    }

    @Test
    void shouldUpdateEspecialidade() {
        UUID id = UUID.randomUUID();
        Especialidade existing = Especialidade.builder().id(id).nome("Old Name").build();
        EspecialidadeRequest request = new EspecialidadeRequest("New Name");
        when(especialidadeRepository.findById(id)).thenReturn(Optional.of(existing));
        when(especialidadeRepository.save(any(Especialidade.class))).thenAnswer(inv -> inv.getArgument(0));

        EspecialidadeResponse result = especialidadeService.atualizarEspecialidade(id, request);

        assertEquals("New Name", result.nome());
    }

    @Test
    void shouldThrowWhenUpdatingNonExistentEspecialidade() {
        when(especialidadeRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> especialidadeService.atualizarEspecialidade(UUID.randomUUID(), new EspecialidadeRequest("X")));
    }

    @Test
    void shouldDeleteEspecialidade() {
        UUID id = UUID.randomUUID();
        when(especialidadeRepository.existsById(id)).thenReturn(true);

        especialidadeService.deletarEspecialidade(id);

        verify(especialidadeRepository).deleteById(id);
    }

    @Test
    void shouldThrowWhenDeletingNonExistentEspecialidade() {
        when(especialidadeRepository.existsById(any())).thenReturn(false);
        assertThrows(RuntimeException.class, () -> especialidadeService.deletarEspecialidade(UUID.randomUUID()));
    }
}
