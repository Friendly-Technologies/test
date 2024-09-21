package com.friendly.services.productclass.orm.acs.model.projections;

public interface ProductGroupWithCountProjection extends ProductGroupInfoProjection {
    public Integer getCount();
}