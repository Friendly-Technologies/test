package com.friendly.services.infrastructure.base;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;

import javax.xml.bind.JAXBElement;
import javax.xml.transform.stream.StreamSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.apache.commons.lang3.StringUtils.trimToNull;

/**
 * Helper class that helps with conversion of XML string to Object and vice versa
 * using Jaxb2Marshaller
 *
 * @param <T> - a generic instance of mapped class needed for marshalling XML
 * @author Friendly Tech
 * @since 0.0.2
 */
@Component
@RequiredArgsConstructor
public class XmlConverter<T> {

    @NonNull
    private final Jaxb2Marshaller marshaller;

    public Jaxb2Marshaller getMarshaller() {
        return marshaller;
    }

    /**
     * Converts an specific Object to XML string respectively to Object XML annotations
     * This functionality doesn't provide an XML wrapper with {@link JAXBElement} parent element
     * and allow to marshal object as is
     *
     * @param objectToConvert - an generic instance of T
     * @return converted XML string
     */
    public String convertToXml(final T objectToConvert, final Class<? extends T> mappedClass) {
        final StringResult stringResult = new StringResult();

        this.marshaller.setMappedClass(mappedClass);
        this.marshaller.marshal(objectToConvert, stringResult);

        return stringResult.toString();
    }

    /**
     * Converts a specific XML string to Object respectively to mapped class
     *
     * @param xml- an XML string
     * @return an instance of a generic class T
     * @throws IllegalArgumentException - when xml string doesn't exist
     */
    public T convertToObject(final String xml) {
        Validate.notEmpty(trimToNull(xml), "Cannot convert to Object. XML string is null");

        return unmarshalSourceXml(xml);
    }

    /**
     * Converts a specific XML string to Object respectively to mapped class
     *
     * @param fileXml- an XML file
     * @return an instance of a generic class T
     * @throws IllegalArgumentException - when xml string doesn't exist
     */
    public T convertToObject(final File fileXml) throws FileNotFoundException {

        return unmarshalSourceXml(fileXml);
    }

    @SuppressWarnings("unchecked")
    private T unmarshalSourceXml(final String xml) {
        final Object convertedObject = this.marshaller.unmarshal(new StringSource(xml));

        /*This workaround required for cases when an object doesn't have an annotation @XmlRootElement*/
        if (convertedObject instanceof JAXBElement) {
            return ((JAXBElement<T>) convertedObject).getValue();
        }

        return (T) convertedObject;
    }


    private T unmarshalSourceXml(final File fileXml) throws FileNotFoundException {
        final Object convertedObject = this.marshaller.unmarshal(new StreamSource(new FileInputStream(fileXml)));

        /*This workaround required for cases when an object doesn't have an annotation @XmlRootElement*/
        if (convertedObject instanceof JAXBElement) {
            return ((JAXBElement<T>) convertedObject).getValue();
        }

        return (T) convertedObject;
    }
}
