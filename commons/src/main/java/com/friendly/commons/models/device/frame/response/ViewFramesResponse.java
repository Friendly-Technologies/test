package com.friendly.commons.models.device.frame.response;

import com.friendly.commons.models.view.ViewFrame;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
public class ViewFramesResponse {
    List<ViewFrame> items;
}
