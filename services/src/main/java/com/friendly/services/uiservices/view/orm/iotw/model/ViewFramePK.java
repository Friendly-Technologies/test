package com.friendly.services.uiservices.view.orm.iotw.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewFramePK implements Serializable {

    private Long viewId;
    private Long frameId;

}
