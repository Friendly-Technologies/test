package com.friendly.services.infrastructure.config;

import com.friendly.commons.models.reports.DeviceReportXml;
import com.friendly.commons.models.reports.XmlFile;
import com.friendly.services.infrastructure.base.DateXmlAdapter;
import com.friendly.services.infrastructure.base.XmlConverter;
import com.friendly.services.settings.acs.model.License;
import com.friendly.services.uiservices.system.model.VersionXmlModel;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.util.HashMap;

import static javax.xml.bind.Marshaller.JAXB_ENCODING;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;

@Configuration
public class XmlConverterConfig {

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public Jaxb2Marshaller jaxb2Marshaller() {
        return createJaxb2Marshaller(License.class, DeviceReportXml.class, VersionXmlModel.class);
    }

    Jaxb2Marshaller createJaxb2Marshaller(final Class... classesToBeBound) {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(classesToBeBound);
        marshaller.setAdapters(new DateXmlAdapter());
        marshaller.setMarshallerProperties(new HashMap<String, Object>() {{
            put(JAXB_FORMATTED_OUTPUT, true);
            put(JAXB_ENCODING, "UTF-8");
        }});

        return marshaller;
    }

    @Bean
    public XmlConverter<License> licenseXmlConverter() {
        final XmlConverter<License> xmlConverter = new XmlConverter<>(jaxb2Marshaller());

        xmlConverter.getMarshaller().setMappedClass(License.class);

        return xmlConverter;
    }

    @Bean
    public XmlConverter<XmlFile> reportXmlConverter() {
        return new XmlConverter<>(jaxb2Marshaller());
    }

    @Bean
    public XmlConverter<VersionXmlModel> versionXmlConverter() {
        return new XmlConverter<>(jaxb2Marshaller());
    }


}
