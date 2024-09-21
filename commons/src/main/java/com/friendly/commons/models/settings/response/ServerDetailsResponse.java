package com.friendly.commons.models.settings.response;

import com.friendly.commons.models.settings.ServerDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ServerDetailsResponse {
    List<ServerDetails> items;
}
