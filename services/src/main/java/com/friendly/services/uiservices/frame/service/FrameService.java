package com.friendly.services.uiservices.frame.service;

import com.friendly.commons.exceptions.FriendlyEntityNotFoundException;
import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.device.Device;
import com.friendly.commons.models.device.frame.response.FramesSimpleResponse;
import com.friendly.commons.models.device.frame.response.ViewFramesResponse;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.view.FrameRow;
import com.friendly.commons.models.view.FrameSimple;
import com.friendly.commons.models.view.FrameTitle;
import com.friendly.commons.models.view.ViewFrame;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.uiservices.frame.mapper.FrameMapper;
import com.friendly.services.qoemonitoring.mapper.QoeFrameMapper;
import com.friendly.services.uiservices.frame.orm.iotw.model.FrameEntity;
import com.friendly.services.qoemonitoring.orm.iotw.repository.QoeFrameItemRepository;
import com.friendly.services.uiservices.frame.orm.iotw.repository.FrameRepository;
import com.friendly.services.uiservices.view.orm.iotw.repository.ViewFrameRepository;
import com.friendly.services.settings.usergroup.UserGroupService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.FRAME_NOT_FOUND;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.FRAME_NOT_UNIQUE;

/**
 * Service that exposes the base functionality for interacting with {@link Device} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FrameService {

    @NonNull
    private final QoeFrameItemRepository qoeFrameItemRepository;

    @NonNull
    private final FrameRepository frameRepository;

    @NonNull
    private final ViewFrameRepository viewFrameRepository;

    @NonNull
    private final FrameMapper frameMapper;

    @NonNull
    private final JwtService jwtService;

    @NonNull
    private final UserGroupService userGroupService;
    
    @NonNull
    private final QoeFrameMapper qoeFrameMapper;

    @Transactional
    public ViewFrame createUpdateFrame(final String token, final ViewFrame frame) {
        jwtService.getSession(token);

        final Long frameId = frame.getId();
        final List<FrameRow> params = frame.getRows();
        final List<FrameTitle> titles = frame.getTitles();
        if (frameId != null) {
            final Optional<FrameEntity> frameEntityOptional = frameRepository.findById(frameId);
            if (frameEntityOptional.isPresent()) {
                if (frameRepository.existsByNameAndIdNot(frame.getName(), frameId)) {
                    throw new FriendlyIllegalArgumentException(FRAME_NOT_UNIQUE, frame.getName());
                }

                final FrameEntity currentFrameEntity = frameEntityOptional.get();
                final FrameEntity updatedFrameEntity =
                        currentFrameEntity.toBuilder()
                                .icon(frame.getIcon())
                                .size(frame.getSize())
                                .build();
                if (!currentFrameEntity.getIsDefault()) {
                    updatedFrameEntity.setName(frame.getName());
                    updatedFrameEntity.setTitles(frameMapper.titlesToTitleEntities(titles, updatedFrameEntity));
                    updatedFrameEntity.setRows(frameMapper.frameRowsToFrameParamEntities(params, updatedFrameEntity));
                }
                return frameMapper.frameEntityToViewFrame(frameRepository.saveAndFlush(updatedFrameEntity));
            } else {
                throw new FriendlyEntityNotFoundException(FRAME_NOT_FOUND, frameId);
            }
        } else {
            //create new frame
            if (frameRepository.existsByName(frame.getName())) {
                throw new FriendlyIllegalArgumentException(FRAME_NOT_UNIQUE, frame.getName());
            }

            return frameMapper.frameEntityToViewFrame(saveFrame(frame, frameId, titles, params));
        }
    }

    private FrameEntity saveFrame(final ViewFrame frame, final Long frameId,
                                  final List<FrameTitle> titles, final List<FrameRow> params) {
        final FrameEntity frameEntity = FrameEntity.builder()
                .id(frameId)
                .name(frame.getName())
                .icon(frame.getIcon())
                .isDefault(false)
                .size(frame.getSize())
                .build();
        frameEntity.setTitles(frameMapper.titlesToTitleEntities(titles, frameEntity));
        frameEntity.setRows(frameMapper.frameRowsToFrameParamEntities(params, frameEntity));
        return frameRepository.saveAndFlush(frameEntity);
    }

    @Transactional
    public void deleteFrames(final String token, final List<Long> frameIds) {
        final Session session = jwtService.getSession(token);

        final List<FrameEntity> frames = frameRepository.findAllByIdInAndIsDefault(frameIds, false);
        final List<Long> ids = frames.stream()
                .map(FrameEntity::getId)
                .collect(Collectors.toList());

        viewFrameRepository.deleteAllByFrameIdIn(ids);
        frameRepository.deleteAllByIdIn(ids);
        userGroupService.deleteCustomFramePermission(session.getClientType(), frames.stream()
                .map(FrameEntity::getName)
                .collect(Collectors.toList()));
    }

    public FramesSimpleResponse getFrames(final String token) {
        jwtService.getSession(token);

        List<FrameSimple> frames = Stream.concat(
                        frameRepository.findAll().stream().map(frameMapper::frameEntityToFrameSimple),
                        qoeFrameItemRepository.findAll().stream().map(qoeFrameMapper::qoeFrameToFrameSimple)
                )
                .collect(Collectors.toList());

        return new FramesSimpleResponse(frames);
    }

    public ViewFramesResponse getFrames(final String token, final List<Long> frameIds) {
        jwtService.getSession(token);

        List<ViewFrame> frames = frameRepository.findAllById(frameIds)
                .stream()
                .map(frameMapper::frameEntityToViewFrame)
                .collect(Collectors.toList());
        return new ViewFramesResponse(frames);
    }

}
