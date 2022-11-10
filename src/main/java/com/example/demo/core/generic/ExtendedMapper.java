package com.example.demo.core.generic;

import java.util.List;
import java.util.Set;

@SuppressWarnings({"java:S119", "java:S117"})
public interface ExtendedMapper<BO extends ExtendedEntity, DTO extends ExtendedDTO> {

  BO fromDTO(DTO dto);

  List<BO> fromDTOs(List<DTO> dtos);

  Set<BO> fromDTOs(Set<DTO> dtos);

  DTO toDTO(BO BO);

  List<DTO> toDTOs(List<BO> BOs);

  Set<DTO> toDTOs(Set<BO> BOs);
}
