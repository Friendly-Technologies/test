package com.friendly.services.uiservices.frame.mapper;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.CAN_NOT_SERIALIZE_OBJECT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.view.*;
import com.friendly.services.uiservices.frame.orm.iotw.model.FrameEntity;
import com.friendly.services.uiservices.frame.orm.iotw.model.FrameParamDetailsEntity;
import com.friendly.services.uiservices.frame.orm.iotw.model.FrameParamEntity;
import com.friendly.services.uiservices.frame.orm.iotw.model.FrameTitleEntity;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FrameMapper {

    @NonNull
    private final ObjectMapper mapper;

    public FrameSimple frameEntityToFrameSimple(final FrameEntity entity) {
    return FrameSimple.builder()
        .id(entity.getId())
        .name(entity.getName())
        .icon(entity.getIcon())
        .isDefault(entity.getIsDefault())
        .type(
            Boolean.TRUE.equals(entity.getIsDefault()) ? PropertyType.DEFAULT : PropertyType.CUSTOM)
        .build();
    }

    public ViewFrame frameEntityToViewFrame(final FrameEntity entity) {
        return ViewFrame.builder()
                        .id(entity.getId())
                        .name(entity.getName())
                        .icon(entity.getIcon())
                        .isDefault(entity.getIsDefault())
                        .size(entity.getSize())
                        .titles(titleEntitiesToTitles(entity.getTitles()))
                        .rows(frameParamEntityToFrameParam(entity.getRows()))
                        .build();
    }

    public List<FrameTitleEntity> titlesToTitleEntities(final List<FrameTitle> titles, final FrameEntity frame) {
        if (titles == null) {
            return Collections.EMPTY_LIST;
        }
        return titles.stream()
                     .map(t -> titleToTitleEntity(t, frame))
                     .collect(Collectors.toList());
    }

    public FrameTitleEntity titleToTitleEntity(final FrameTitle title, final FrameEntity frame) {
        if (title == null) {
            return null;
        }
        return FrameTitleEntity.builder()
                               .id(title.getId())
                               .frame(frame)
                               .index(title.getIndex())
                               .name(title.getName())
                               .build();
    }

    public List<FrameTitle> titleEntitiesToTitles(final List<FrameTitleEntity> titles) {
        return titles.stream()
                     .map(this::titleEntityToTitle)
                     .collect(Collectors.toList());
    }

    public FrameTitle titleEntityToTitle(final FrameTitleEntity title) {
        if (title == null) {
            return null;
        }
        return FrameTitle.builder()
                         .id(title.getId())
                         .index(title.getIndex())
                         .name(title.getName())
                         .build();
    }

    public List<FrameParamEntity> frameRowsToFrameParamEntities(final List<FrameRow> params,
                                                                final FrameEntity frame) {
        if (params != null) {
            return params.stream()
                         .map(p -> {
                             final FrameParamEntity param =
                                     FrameParamEntity.builder()
                                                     .id(p.getId())
                                                     .frame(frame)
                                                     .index(p.getIndex())
                                                     .name(p.getName())
                                                     .build();
                             param.setDetails(paramDetailsToFrameParams(p.getDetails(), param));
                             return param;
                         })
                         .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public List<FrameRow> frameParamEntityToFrameParam(final List<FrameParamEntity> params) {
        return params.stream()
                     .map(p -> FrameRow.builder()
                                       .id(p.getId())
                                       .name(p.getName())
                                       .index(p.getIndex())
                                       .details(frameParamToParamDetails(p))
                                       .build())
                     .collect(Collectors.toList());
    }

    public List<ParamDetails> frameParamToParamDetails(final FrameParamEntity paramEntity) {
        return paramEntity.getDetails()
                          .stream()
                          .map(d -> {
                              try {
                                  return ParamDetails.builder()
                                                     .id(d.getId())
                                                     .index(d.getIndex())
                                                     .fullNames(Arrays.asList(d.getFullNames().split(" ,")))
                                                     .inputType(d.getInputType())
                                                     .dataFormatType(d.getDataFormatType())
                                                     .required(d.getRequired())
                                                     .blackList(d.getBlackList())
                                                     .whiteList(d.getWhiteList())
                                                     .scale(d.getScale())
                                                     .minValue(d.getMinValue())
                                                     .maxValue(d.getMaxValue())
                                                     .options(mapper.readValue(d.getOptions(),
                                                                               new TypeReference<List<ParamOption>>() {
                                                                               }))
                                                     .build();
                              } catch (IOException e) {
                                  throw new FriendlyIllegalArgumentException(CAN_NOT_SERIALIZE_OBJECT,
                                                                             "frame parameter");
                              }
                          })
                          .collect(Collectors.toList());
    }

    public List<FrameParamDetailsEntity> paramDetailsToFrameParams(final List<ParamDetails> details,
                                                                   final FrameParamEntity frameParam) {
        return details.stream()
                      .map(d -> {
                          try {
                              return FrameParamDetailsEntity.builder()
                                                            .id(d.getId())
                                                            .param(frameParam)
                                                            .index(d.getIndex())
                                                            .fullNames(String.join(" ,", d.getFullNames()))
                                                            .inputType(d.getInputType())
                                                            .dataFormatType(d.getDataFormatType())
                                                            .required(d.getRequired())
                                                            .blackList(d.getBlackList())
                                                            .whiteList(d.getWhiteList())
                                                            .scale(d.getScale())
                                                            .minValue(d.getMinValue())
                                                            .maxValue(d.getMaxValue())
                                                            .options(mapper.writeValueAsString((d.getOptions())))
                                                            .build();
                          } catch (JsonProcessingException e) {
                              throw new FriendlyIllegalArgumentException(CAN_NOT_SERIALIZE_OBJECT,
                                                                         "frame parameter");
                          }
                      })
                      .collect(Collectors.toList());
    }
    
}
