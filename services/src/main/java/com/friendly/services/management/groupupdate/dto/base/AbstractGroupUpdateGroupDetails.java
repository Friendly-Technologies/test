package com.friendly.services.management.groupupdate.dto.base;

import com.friendly.services.management.groupupdate.dto.GroupUpdateActivation;
import com.friendly.services.management.groupupdate.dto.GroupUpdateReactivation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AbstractGroupUpdateGroupDetails  implements Serializable {
    private Integer id;
    private String name;
    private Integer random;
    private GroupUpdateActivation activation;
    private GroupUpdateReactivation reactivation;
}