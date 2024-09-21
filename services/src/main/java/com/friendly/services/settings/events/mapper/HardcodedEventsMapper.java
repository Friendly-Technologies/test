package com.friendly.services.settings.events.mapper;

import com.friendly.commons.models.settings.acs.events.HardcodedEventItem;
import com.friendly.commons.models.settings.acs.events.HardcodedEventUrlItem;
import com.friendly.services.management.events.orm.acs.model.EventHardcodedEntity;
import com.friendly.services.management.events.orm.acs.model.EventReceiverUrlEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HardcodedEventsMapper {

    public EventHardcodedEntity eventToEventHardcodedEntity(final HardcodedEventItem item) {
        return EventHardcodedEntity.builder()
                .id(item.getId())
                .description(item.getDescription())
                .name(item.getName())
                .notification(item.getEnabled())
                .build();
    }

    public HardcodedEventItem eventHardcodedEntityToEvent(final EventHardcodedEntity entity, List<Integer> urlIds) {
        return HardcodedEventItem.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .name(entity.getName())
                .enabled(entity.getNotification())
                .urlIds(urlIds)
                .build();
    }

    public EventReceiverUrlEntity eventUrlToEventReceiverUrlEntity(final HardcodedEventUrlItem item) {
        return EventReceiverUrlEntity.builder()
                .id(item.getId())
                .url(item.getUrl())
                .useFtacsNs(item.getUseFtacsNs())
                .build();
    }

    public HardcodedEventUrlItem eventReceiverUrlEntityToEventUrl(final EventReceiverUrlEntity entity) {
        return HardcodedEventUrlItem.builder()
                .id(entity.getId())
                .url(entity.getUrl())
                .useFtacsNs(entity.getUseFtacsNs())
                .build();
    }



}
