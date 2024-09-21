package com.friendly.services.settings.snmpserver.sender;

import com.friendly.commons.models.settings.SnmpVersionType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.lang.management.ManagementFactory;

@Slf4j
@AllArgsConstructor
public class SnmpSender {

    private static final String trapOid = "1.3.6.1.2.1.1";

    private final String host;
    private final String port;
    private final String community;
    private final SnmpVersionType version;

    public void sendSnmpTrap(final String message) {
        try {
            // Create Transport Mapping
            final TransportMapping<UdpAddress> transport = new DefaultUdpTransportMapping();
            transport.listen();

            // Create Target
            final CommunityTarget target = new CommunityTarget();
            if (version.equals(SnmpVersionType.V1)) {
                target.setVersion(SnmpConstants.version1);
            } else {
                target.setVersion(SnmpConstants.version2c);
            }
            target.setCommunity(new OctetString(community));
            target.setAddress(new UdpAddress(host + "/" + port));
            target.setRetries(2);
            target.setTimeout(5000);

            final long sysUpTime = ManagementFactory.getRuntimeMXBean().getUptime();
            final PDU pdu;
            if (version.equals(SnmpVersionType.V1)) {
                pdu = getPduV1(sysUpTime, message);
            } else {
                pdu = getPduV2(sysUpTime, message);
            }

            // Send the PDU
            final Snmp snmp = new Snmp(transport);
            log.trace("Sending {} Trap to {} on Port {}", version, host, port);
            snmp.send(pdu, target);
            snmp.close();
        } catch (Exception e) {
            log.error("Error in Sending {} Trap to {} on Port {} Exception Message = {}",
                      version, host, port, e.getMessage());
        }
    }

    private PDUv1 getPduV1(final long sysUpTime, final String message) {
        final PDUv1 pdu = new PDUv1();

        pdu.setType(PDU.V1TRAP);
        pdu.setEnterprise(new OID(trapOid));
        pdu.setGenericTrap(PDUv1.ENTERPRISE_SPECIFIC);
        pdu.setSpecificTrap(1);
        pdu.setAgentAddress(new IpAddress(host));
        pdu.setTimestamp(sysUpTime);
        pdu.add(new VariableBinding(new OID(trapOid), new OctetString(message)));

        return pdu;
    }

    private PDU getPduV2(final long sysUpTime, final String message) {
        final PDU pdu = new PDU();

        pdu.add(new VariableBinding(SnmpConstants.sysUpTime, new TimeTicks(sysUpTime)));
        pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, new OID(trapOid)));
        pdu.add(new VariableBinding(SnmpConstants.snmpTrapAddress, new IpAddress(host)));
        // variable binding for Enterprise Specific objects, Severity (should be defined in MIB file)
        pdu.add(new VariableBinding(new OID(trapOid), new OctetString(message)));
        pdu.setType(PDU.NOTIFICATION);

        return pdu;
    }


}
