package com.friendly.services.settings.alerts.mapper;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.settings.Alerts;
import com.friendly.services.settings.alerts.orm.iotw.model.AlertsEntity;
import com.friendly.services.settings.alerts.orm.iotw.model.AlertsSpecificDomainEntity;
import org.springframework.stereotype.Component;

@Component
public class AlertsMapper {

    public AlertsEntity alertsToAlertsEntity(final ClientType clientType, final Alerts alerts) {
        return AlertsEntity.builder()
                .id(clientType)
                .viaProgram(alerts.isViaProgram())
                .viaEmail(alerts.isViaEmail())
                .viaSms(alerts.isViaSms())
                .viaSnmp(alerts.isViaSnmp())
                .emails(alerts.getEmails())
                .phoneNumbers(alerts.getPhoneNumbers())
                .alertTimesType(alerts.getAlertTimesType())
                .interval(alerts.getInterval())
                .build();
    }

    public Alerts alertsEntityToAlerts(final AlertsEntity alerts) {
        return Alerts.builder()
                .viaProgram(alerts.isViaProgram())
                .viaEmail(alerts.isViaEmail())
                .viaSms(alerts.isViaSms())
                .viaSnmp(alerts.isViaSnmp())
                .alertTimesType(alerts.getAlertTimesType())
                .interval(alerts.getInterval())
                .emails(alerts.getEmails())
                .phoneNumbers(alerts.getPhoneNumbers())
                .build();
    }

    public Alerts alertsSpecificEntityToAlerts(AlertsSpecificDomainEntity alerts) {
        return Alerts.builder()
                .viaProgram(alerts.isViaProgram())
                .viaEmail(alerts.isViaEmail())
                .viaSms(alerts.isViaSms())
                .viaSnmp(alerts.isViaSnmp())
                .alertTimesType(alerts.getAlertTimesType())
                .interval(alerts.getInterval())
                .emails(alerts.getEmails())
                .phoneNumbers(alerts.getPhoneNumbers())
                .build();
    }

    public AlertsSpecificDomainEntity toSpecificEntity(Integer id, Integer domainId,
                                                       ClientType clientType, Alerts alerts) {
        return AlertsSpecificDomainEntity.builder()
                .id(id)
                .domainId(domainId)
                .clientType(clientType)
                .interval(alerts.getInterval())
                .alertTimesType(alerts.getAlertTimesType())
                .emails(alerts.getEmails())
                .phoneNumbers(alerts.getPhoneNumbers())
                .viaEmail(alerts.isViaEmail())
                .viaProgram(alerts.isViaProgram())
                .viaSms(alerts.isViaSms())
                .viaSnmp(alerts.isViaSnmp())
                .build();
    }
}
