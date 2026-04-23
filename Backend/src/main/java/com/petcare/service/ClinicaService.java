package com.petcare.service;

import com.petcare.exception.RecursoNaoEncontradoException;
import com.petcare.mapper.ClinicaMapper;
import com.petcare.mapper.request.ClinicaCreateRequest;
import com.petcare.mapper.request.ClinicaRequest;
import com.petcare.mapper.response.ClinicaResponse;
import com.petcare.model.Clinica;
import com.petcare.model.Usuario;
import com.petcare.repository.ClinicaRepository;
import com.petcare.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClinicaService {

    private final ClinicaRepository clinicaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ClinicaResponse createClinica(ClinicaRequest clinicaRequest) {
        Usuario usuario = Usuario.builder()
                .email(clinicaRequest.cnpj() + "@clinica.petcare")
                .nomeUsuario(clinicaRequest.nomeClinica().length() <= 10 ? clinicaRequest.nomeClinica() : clinicaRequest.nomeClinica().substring(0, 10))
                .senhaHash(passwordEncoder.encode(clinicaRequest.cnpj()))
                .nivelAcesso("CLINICA")
                .build();
        usuario = usuarioRepository.save(usuario);

        Clinica clinica = ClinicaMapper.toClinica(clinicaRequest);
        clinica.setUsuario(usuario);

        Clinica savedClinica = clinicaRepository.save(clinica);
        return ClinicaMapper.toClinicaResponse(savedClinica);
    }

    @Transactional
    public ClinicaResponse registrarNovaClinica(ClinicaCreateRequest dto, Usuario usuario) {
        Clinica clinica = Clinica.builder()
                .usuario(usuario)
                .nomeClinica(dto.nomeClinica())
                .razaoSocial(dto.razaoSocial())
                .cnpj(dto.cnpj())
                .build();

        Clinica savedClinica = clinicaRepository.save(clinica);
        return ClinicaMapper.toClinicaResponse(savedClinica);
    }

    public ClinicaResponse getClinicaById(UUID id) {
        Clinica clinica = clinicaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Clínica não encontrada"));
        return ClinicaMapper.toClinicaResponse(clinica);
    }

    public List<ClinicaResponse> getAllClinicas() {
        return clinicaRepository.findAll().stream()
                .map(ClinicaMapper::toClinicaResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClinicaResponse updateClinica(UUID id, ClinicaRequest clinicaRequest) {
        Clinica clinica = clinicaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Clínica não encontrada"));
        clinica.setNomeClinica(clinicaRequest.nomeClinica());
        clinica.setRazaoSocial(clinicaRequest.razaoSocial());
        clinica.setCnpj(clinicaRequest.cnpj());

        Clinica updatedClinica = clinicaRepository.save(clinica);
        return ClinicaMapper.toClinicaResponse(updatedClinica);
    }

    public void deleteClinica(UUID id) {
        if (!clinicaRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Clínica não encontrada");
        }
        clinicaRepository.deleteById(id);
    }
}
