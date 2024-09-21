package com.friendly.services.uiservices.view.orm.iotw.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewColumnPK implements Serializable {

    private Long viewId;
    private String columnKey;

}
