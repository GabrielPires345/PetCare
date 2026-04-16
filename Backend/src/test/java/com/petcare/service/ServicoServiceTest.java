package com.petcare.service;

import com.petcare.mapper.ServicoMapper;
import com.petcare.mapper.request.ServicoRequest;
import com.petcare.mapper.response.ServicoResponse;
import com.petcare.model.Servico;
import com.petcare.repository.ServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServicoServiceTest {

    @Mock
    private ServicoRepository servicoRepository;

    @InjectMocks
    private ServicoService servicoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateServico() {
        ServicoRequest request = new ServicoRequest("Consulta", "Consulta geral", BigDecimal.valueOf(150), 60);
        when(servicoRepository.save(any(Servico.class))).thenAnswer(invocation -> {
            Servico s = invocation.getArgument(0);
            return Servico.builder().id(UUID.randomUUID()).nome(s.getNome()).descricao(s.getDescricao())
                    .precoBase(s.getPrecoBase()).duracaoMinutos(s.getDuracaoMinutos()).build();
        });

        ServicoResponse result = servicoService.createServico(request);

        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals("Consulta", result.nome());
        assertEquals("Consulta geral", result.descricao());
        assertEquals(BigDecimal.valueOf(150), result.precoBase());
        assertEquals(60, result.duracaoMinutos());
    }

    @Test
    void shouldReturnServicoById() {
        UUID id = UUID.randomUUID();
        Servico servico = Servico.builder().id(id).nome("Banho").descricao("Banho e tosa").precoBase(BigDecimal.valueOf(80)).duracaoMinutos(45).build();
        when(servicoRepository.findById(id)).thenReturn(Optional.of(servico));

        ServicoResponse result = servicoService.getServicoById(id);

        assertEquals("Banho", result.nome());
        assertEquals("Banho e tosa", result.descricao());
        assertEquals(BigDecimal.valueOf(80), result.precoBase());
        assertEquals(45, result.duracaoMinutos());
    }

    @Test
    void shouldThrowWhenServicoNotFound() {
        when(servicoRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> servicoService.getServicoById(UUID.randomUUID()));
    }

    @Test
    void shouldReturnAllServicos() {
        Servico s1 = Servico.builder().id(UUID.randomUUID()).nome("A").precoBase(BigDecimal.ONE).duracaoMinutos(10).build();
        Servico s2 = Servico.builder().id(UUID.randomUUID()).nome("B").precoBase(BigDecimal.ONE).duracaoMinutos(10).build();
        when(servicoRepository.findAll()).thenReturn(List.of(s1, s2));

        List<ServicoResponse> result = servicoService.getAllServicos();

        assertEquals(2, result.size());
    }

    @Test
    void shouldUpdateServico() {
        UUID id = UUID.randomUUID();
        Servico existing = Servico.builder().id(id).nome("Old").precoBase(BigDecimal.TEN).duracaoMinutos(30).build();
        ServicoRequest request = new ServicoRequest("New Name", "New desc", BigDecimal.valueOf(200), 90);
        when(servicoRepository.findById(id)).thenReturn(Optional.of(existing));
        when(servicoRepository.save(any(Servico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ServicoResponse result = servicoService.updateServico(id, request);

        assertEquals("New Name", result.nome());
        assertEquals("New desc", result.descricao());
        assertEquals(BigDecimal.valueOf(200), result.precoBase());
        assertEquals(90, result.duracaoMinutos());
    }

    @Test
    void shouldDeleteServico() {
        UUID id = UUID.randomUUID();
        when(servicoRepository.existsById(id)).thenReturn(true);

        servicoService.deleteServico(id);

        verify(servicoRepository).deleteById(id);
    }

    @Test
    void shouldThrowWhenDeletingNonExistentServico() {
        when(servicoRepository.existsById(any())).thenReturn(false);
        assertThrows(RuntimeException.class, () -> servicoService.deleteServico(UUID.randomUUID()));
    }
}
