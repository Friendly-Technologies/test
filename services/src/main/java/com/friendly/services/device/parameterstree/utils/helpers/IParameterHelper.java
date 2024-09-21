package com.friendly.services.device.parameterstree.utils.helpers;

import com.friendly.services.device.parameterstree.orm.acs.model.AbstractParameterEntity;

import java.util.List;

public interface IParameterHelper {
    boolean isParamExist(final Long ownerId, final String param);
    boolean isParamExistLike(final Long ownerId, final String param);
    String getRootParamName(Long ownerId);
    List<String> getParamNamesLike(final Long ownerId, final String param);
    String decryptValueIfNeeded(final String value, final Long nameId);

    String getParameterExtendedValue(final Long cpeParameterId);

    List<? extends AbstractParameterEntity> findAllByOwnerId(Long ownerId);

    List<? extends AbstractParameterEntity> findAllByOwnerIdAndFullNameLike(Long ownerId, String name);
}