package com.friendly.services.device.parameterstree.service;

import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.FTPage;
import com.friendly.commons.models.settings.RetrieveMode;
import com.friendly.commons.models.settings.RetrieveModeBody;
import com.friendly.commons.models.user.Session;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.productclass.orm.acs.model.ProductClassGroupEntity;
import com.friendly.services.device.parameterstree.orm.acs.model.RetrieveModeEntity;
import com.friendly.services.device.parameterstree.orm.acs.repository.RetrieveModeRepository;
import com.friendly.services.infrastructure.config.provider.AcsProvider;
import com.friendly.services.infrastructure.utils.PageUtils;
import com.ftacs.Exception_Exception;
import com.ftacs.IntegerArrayWS;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.ACS_EXCEPTION;

/**
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RetrieveModeService {

    @NonNull
    private final JwtService jwtService;

    @NonNull
    private final RetrieveModeRepository retrieveModeRepository;


    public FTPage<RetrieveMode> getRetrieveMode(final String token, final RetrieveModeBody body) {
        final List<Pageable> pageable = PageUtils.createPageRequest(body.getPageNumbers(), body.getPageSize(),
                null, "id");
        jwtService.getSession(token);

        final List<Page<RetrieveModeEntity>> pageEntities =
                pageable.stream()
                        .map(retrieveModeRepository::findAll)
                        .collect(Collectors.toList());
        final List<RetrieveMode> retrieveModes =
                pageEntities.stream()
                        .map(Page::getContent)
                        .flatMap(entities -> entities.stream()
                                .map(this::entityToRetrieveMode))
                        .collect(Collectors.toList());

        final FTPage<RetrieveMode> page = new FTPage<>();
        return page.toBuilder()
                .items(retrieveModes)
                .pageDetails(PageUtils.buildPageDetails(pageEntities))
                .build();
    }

    private RetrieveMode entityToRetrieveMode(final RetrieveModeEntity entity) {
        final ProductClassGroupEntity productGroup = entity.getProductGroup();

        return productGroup == null ? null :
                RetrieveMode.builder()
                        .id(productGroup.getId().intValue())
                        .manufacturer(productGroup.getManufacturerName())
                        .model(productGroup.getModel())
                        .build();
    }

    public boolean addRetrieveMode(final String token, final List<Integer> modelIds) {
        final Session session = jwtService.getSession(token);

        final IntegerArrayWS ids = new IntegerArrayWS();
        ids.getId().addAll(modelIds);

        try {
            AcsProvider.getAcsWebService(session.getClientType()).addRetrieveMethod(ids);
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
        return true;
    }

    public boolean removeRetrieveMode(final String token, final List<Integer> modelIds) {
        final Session session = jwtService.getSession(token);

        final IntegerArrayWS ids = new IntegerArrayWS();
        ids.getId().addAll(modelIds);

        AcsProvider.getAcsWebService(session.getClientType())
                .removeRetrieveMethod(ids);

        return true;
    }

}
