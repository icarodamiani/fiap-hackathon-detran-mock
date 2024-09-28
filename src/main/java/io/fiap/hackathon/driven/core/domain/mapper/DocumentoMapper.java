package io.fiap.hackathon.driven.core.domain.mapper;

import io.fiap.hackathon.driven.core.domain.Documento;
import io.fiap.hackathon.driver.controller.dto.DocumentoDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DocumentoMapper extends BaseMapper<DocumentoDTO, Documento> {
}
