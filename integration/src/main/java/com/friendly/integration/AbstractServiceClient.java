package com.friendly.integration;

import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.friendly.commons.errors.ErrorApi;
import com.friendly.integration.util.QueryModifierBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static brave.internal.HexCodec.toLowerHex;
import static java.lang.String.format;
import static org.apache.http.HttpStatus.*;

/**
 * Abstract service client that contains reusable functionality that is shared
 * among all RESTful client implementations
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
public abstract class AbstractServiceClient {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractServiceClient.class);

    protected static final String APPLICATION_JSON = "application/json";
    private static final String PLAIN_TEXT = "text/plain";
    private static final String HTTP_SCHEME = "http";
    private static final String LOCATION_HEADER = "Location";

    public static final List<Integer> ALLOWED_ERROR_CODES = Arrays.asList(SC_BAD_REQUEST,
                                                                          SC_NOT_FOUND,
                                                                          SC_METHOD_NOT_ALLOWED,
                                                                          SC_UNPROCESSABLE_ENTITY,
                                                                          SC_INTERNAL_SERVER_ERROR,
                                                                          SC_UNAUTHORIZED);

    private static final int MAX_CONN_TOTAL = 32;
    private static final int MAX_CONN_PER_ROUTE = 32;

    private static final String TRACE_ID_NAME = "X-B3-TraceId";
    private static final String SPAN_ID_NAME = "X-B3-SpanId";
    private static final String PARENT_SPAN_ID_NAME = "X-B3-ParentSpanId";
    private static final String SAMPLED_NAME = "X-B3-Sampled";
    private static final String FLAGS_NAME = "X-B3-Flags";

    private final CloseableHttpClient httpClient;
    private final String apiHost;
    private final int apiPort;
    private final Tracer tracer;

    /**
     * Creates a new service client that will connect to the supplied API Host
     *
     * @param apiHost {@link String} containing the API host to connect
     * @param apiPort {@link int} containing the API port number to connect
     * @param tracer  Instance of {@link Tracer} to get access to Correlation ID information
     */
    protected AbstractServiceClient(
            final String apiHost,
            final int apiPort,
            final Tracer tracer) {

        super();

        this.apiHost = apiHost;
        this.apiPort = apiPort;
        this.tracer = tracer;
        this.httpClient = HttpClients.custom()
                                     .addInterceptorFirst(createTracingInfoInterceptor())
                                     .setMaxConnPerRoute(MAX_CONN_PER_ROUTE)
                                     .setMaxConnTotal(MAX_CONN_TOTAL)
                                     .build();
    }

    /**
     * Creates a new service client that will connect to the supplied API Host
     *
     * @param apiHost {@link String} containing the API host to connect
     * @param apiPort {@link int} containing the API port number to connect
     */
    protected AbstractServiceClient(
            final String apiHost,
            final int apiPort) {

        this(apiHost, apiPort, null);
    }

    /**
     * Gets the Host that this API client is going to use
     *
     * @return {@link String} of the API client host
     */
    protected String getApiHost() {

        return apiHost;
    }

    /**
     * Gets the port on which this current API service client will operate
     *
     * @return Port on which the service client will operate
     */
    protected int getApiPort() {

        return apiPort;
    }

    /**
     * Gets the current {@link CloseableHttpClient} used for this service client
     *
     * @return {@link CloseableHttpClient} for this client
     */
    private CloseableHttpClient getHttpClient() {

        return httpClient;
    }

    /**
     * Gets the HTTP Scheme (e.g. http, https) being used for this service client
     *
     * @return HTTP scheme in use by this client
     */
    protected String getHttpScheme() {

        return HTTP_SCHEME;
    }

    /**
     * Builds a {@link URI} to make an HTTP request to the given path
     *
     * @param path {@link String} of the Path for the HTTP request
     * @return {@link URI} for the defined path
     */
    URI buildRequestUri(final String path) {

        return buildRequestUri(path, new QueryModifierBuilder());
    }

    /**
     * Builds a {@link URI} to make an HTTP request to the given path
     *
     * @param path {@link String} of the Path for the HTTP request
     * @return {@link URI} for the defined path
     */
    protected URI buildRequestUri(final String path, final String... args) {
        return buildRequestUri(format(path, args));
    }

    /**
     * Builds a {@link URI} to make an HTTP request to the given path
     *
     * @param path {@link String} of the Path for the HTTP request
     * @return {@link URI} for the defined path
     */
    protected URI buildRequestUri(final String path, final QueryModifierBuilder queryModifierBuilder) {

        final URIBuilder uriBuilder = new URIBuilder().setScheme(getHttpScheme())
                                                      .setHost(getApiHost())
                                                      .setPort(getApiPort())
                                                      .setPath(path)
                                                      .setCustomQuery(queryModifierBuilder.toString());
        return buildRequestUri(uriBuilder);
    }

    /**
     * Builds a {@link URI} to make an HTTP request using the given {@link URIBuilder}
     *
     * @param uriBuilder {@link URIBuilder} of the Path for the HTTP request
     * @return {@link URI} from the properties defined in the {@link URIBuilder}
     */
    protected URI buildRequestUri(final URIBuilder uriBuilder) {

        try {
            return uriBuilder.build();
        } catch (final URISyntaxException use) {
            LOG.error("Error building Request URI", use);
            return null;
        }
    }

    /**
     * Executes an {@link HttpUriRequest}, handling conversion of the return type to the given Collection
     *
     * @param request         {@link HttpUriRequest} that is being executed
     * @param collectionClazz {@link Class} of the {@link Collection} type that is returned
     * @param collectionType  {@link Class} of the return type in the Collection
     * @param <T>             Type definition that's returned from the JSON conversion
     * @return {@link Collection} of {@link Class} collectionType objects
     * @throws NotFoundException         when an HTTP 404 is encountered
     * @throws InvalidRequestException   when an HTTP 400 is encountered
     * @throws UnauthorizedUserException when an HTTP 401 is encountered
     */
    protected <T> T executeRequest(final HttpUriRequest request,
                                   final Class<? extends Collection> collectionClazz,
                                   final Class collectionType)
            throws NotFoundException, InvalidRequestException, UnauthorizedUserException {

        return executeRequest(request, collectionClazz, collectionType, false);
    }

    /**
     * Executes an {@link HttpUriRequest}, handling conversion of the return type to the given Collection
     *
     * @param request         {@link HttpUriRequest} that is being executed
     * @param collectionClazz {@link Class} of the {@link Collection} type that is returned
     * @param collectionType  {@link Class} of the return type in the Collection
     * @param <T>             Type definition that's returned from the JSON conversion
     * @param useAnnotations  {@link Boolean} representing whether to use annotations
     * @return {@link Collection} of {@link Class} collectionType objects
     * @throws NotFoundException         when an HTTP 404 is encountered
     * @throws InvalidRequestException   when an HTTP 400 is encountered
     * @throws UnauthorizedUserException when an HTTP 401 is encountered
     */
    protected <T> T executeRequest(final HttpUriRequest request,
                                   final Class<? extends Collection> collectionClazz,
                                   final Class collectionType,
                                   final boolean useAnnotations)
            throws NotFoundException, InvalidRequestException, UnauthorizedUserException {

        final ObjectMapper objectMapper = createObjectMapper(useAnnotations);

        final JavaType type = objectMapper.getTypeFactory()
                                          .constructCollectionType(collectionClazz, collectionType);

        return executeRequestInternally(request, useAnnotations, objectMapper, type);
    }

    /**
     * Executes an {@link HttpUriRequest}, converting response to the defined type
     *
     * @param request {@link HttpUriRequest} that is being executed
     * @param t       {@link Type} representing the Type to convert JSON response
     * @param <T>     Type definition that's returned from the JSON conversion
     * @return Object that was converted from the JSON response, null if empty object
     * @throws NotFoundException         when an HTTP 404 is encountered
     * @throws InvalidRequestException   when an HTTP 400 is encountered
     * @throws InternalServerException   when an HTTP 500 is encountered
     * @throws UnauthorizedUserException when an HTTP 401 is encountered
     */
    protected <T> T executeRequest(final HttpUriRequest request,
                                   final Type t) throws NotFoundException, InvalidRequestException,
            UnauthorizedUserException {
        return executeRequest(request, t, false);
    }

    /**
     * Executes an {@link HttpUriRequest}, converting response to the defined type
     *
     * @param request        {@link HttpUriRequest} that is being executed
     * @param typeReference  {@link TypeReference} representing the TypeReference to convert JSON response
     * @param useAnnotations {@link Boolean} representing whether to use annotations
     * @param <T>            Type definition that's returned from the JSON conversion
     * @return Object that was converted from the JSON response, null if empty object
     * @throws NotFoundException         when an HTTP 404 is encountered
     * @throws InvalidRequestException   when an HTTP 400 is encountered
     * @throws InternalServerException   when an HTTP 500 is encountered
     * @throws UnauthorizedUserException when an HTTP 401 is encountered
     */
    <T> T executeRequest(final HttpUriRequest request, final TypeReference typeReference, final boolean useAnnotations)
            throws NotFoundException, InvalidRequestException, UnauthorizedUserException {

        final ObjectMapper objectMapper = createObjectMapper(useAnnotations);
        final String jsonData = httpRequest(request, useAnnotations);

        try {
            // Response is valid and should map to the request entity Type
            return (T) objectMapper.readValue(jsonData, typeReference);

        } catch (final IOException ioException) {
            LOG.error("Error executing collection request", ioException);
            return null;
        }
    }

    /**
     * Executes an {@link HttpUriRequest}, converting response to the defined type
     *
     * @param request        {@link HttpUriRequest} that is being executed
     * @param t              {@link Type} representing the Type to convert JSON response
     * @param useAnnotations {@link Boolean} representing whether to use annotations
     * @param <T>            Type definition that's returned from the JSON conversion
     * @return Object that was converted from the JSON response, null if empty object
     * @throws NotFoundException         when an HTTP 404 is encountered
     * @throws InvalidRequestException   when an HTTP 400 is encountered
     * @throws InternalServerException   when an HTTP 500 is encountered
     * @throws UnauthorizedUserException when an HTTP 401 is encountered
     */
    protected <T> T executeRequest(final HttpUriRequest request,
                                   final Type t,
                                   final boolean useAnnotations)
            throws NotFoundException, InvalidRequestException, InternalServerException,
            UnauthorizedUserException {

        final ObjectMapper objectMapper = createObjectMapper(useAnnotations);

        final JavaType type = objectMapper.constructType(t);

        return executeRequestInternally(request, useAnnotations, objectMapper, type);
    }

    /**
     * Executes an {@link HttpUriRequest}, converting response to the defined type
     *
     * @param request          {@link HttpUriRequest} that is being executed
     * @param t                {@link Class} representing the Type to convert JSON response
     * @param parametrizedType {@link Class} representing the Subtype to convert JSON response
     * @param useAnnotations   {@link Boolean} representing whether to use annotations
     * @param <T>              Type definition that's returned from the JSON conversion
     * @return Object that was converted from the JSON response, null if empty object
     * @throws NotFoundException         when an HTTP 404 is encountered
     * @throws InvalidRequestException   when an HTTP 400 is encountered
     * @throws InternalServerException   when an HTTP 500 is encountered
     * @throws UnauthorizedUserException when an HTTP 401 is encountered
     */
    protected <T> T executeParametrizedRequest(final HttpUriRequest request,
                                               final Class t,
                                               final Class parametrizedType,
                                               final boolean useAnnotations)
            throws NotFoundException, InvalidRequestException, InternalServerException,
            UnauthorizedUserException {

        final ObjectMapper objectMapper = createObjectMapper(useAnnotations);

        final JavaType type = objectMapper.getTypeFactory()
                                          .constructParametricType(t, parametrizedType);

        return executeRequestInternally(request, useAnnotations, objectMapper, type);
    }

    /**
     * Executes an {@link HttpUriRequest}, handling conversion of the return type to the given Collection
     *
     * @param request        {@link HttpUriRequest} that is being executed
     * @param mapClass       {@link Class} of the {@link Collection} type that is returned
     * @param mapKeyType     {@link Class} of the key in {@link Map}
     * @param mapValueType   {@link Class} of the value in {@link Map}
     * @param <T>            Type definition that's returned from the JSON conversion
     * @param useAnnotations {@link Boolean} representing whether to use annotations
     * @return {@link Collection} of {@link Class} collectionType objects
     * @throws NotFoundException         when an HTTP 404 is encountered
     * @throws InvalidRequestException   when an HTTP 400 is encountered
     * @throws UnauthorizedUserException when an HTTP 401 is encountered
     */
    <T> T executeRequest(final HttpUriRequest request,
                         final Class<? extends Map> mapClass,
                         final Class mapKeyType,
                         final Class mapValueType,
                         final boolean useAnnotations)
            throws NotFoundException, InvalidRequestException, UnauthorizedUserException {

        final ObjectMapper objectMapper = createObjectMapper(useAnnotations);

        final JavaType type = objectMapper.getTypeFactory()
                                          .constructMapType(mapClass, mapKeyType, mapValueType);

        return executeRequestInternally(request, useAnnotations, objectMapper, type);
    }

    private ObjectMapper createObjectMapper(final boolean useAnnotations) {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        return objectMapper.enable(MapperFeature.ALLOW_EXPLICIT_PROPERTY_RENAMING)
                           .configure(MapperFeature.USE_ANNOTATIONS, useAnnotations);
    }

    /**
     * Executes an {@link HttpUriRequest}, handling conversion of the return type to the given type
     *
     * @param request        {@link HttpUriRequest} that is being executed
     * @param useAnnotations {@link Boolean} representing whether to use annotations
     * @param objectMapper   {@link ObjectMapper} to convert response
     * @param type           {@link JavaType} to convert
     * @param <T>            Type definition that's returned from the JSON conversion
     * @return {@link Collection} of {@link Class} collectionType objects
     */
    private <T> T executeRequestInternally(final HttpUriRequest request, final boolean useAnnotations,
                                           final ObjectMapper objectMapper, final JavaType type) {

        final String jsonData = httpRequest(request, useAnnotations);

        try {
            //Empty response is valid in case expected type is Void
            if (type.isTypeOrSubTypeOf(Void.class) || StringUtils.isBlank(jsonData)) {
                return null;
            }

            // Response is valid and should map to the request entity Type
            return objectMapper.readValue(jsonData, type);

        } catch (final IOException ioException) {
            LOG.error("Error executing request", ioException);
            return null;
        }
    }

    /**
     * Executes an {@link HttpUriRequest}, handling conversion of the return type to the given Collection
     *
     * @param request        {@link HttpUriRequest} that is being executed
     * @param useAnnotations {@link Boolean} representing whether to use annotations
     * @return {@link String} of json data
     * @throws NotFoundException         when an HTTP 404 is encountered
     * @throws InvalidRequestException   when an HTTP 400 is encountered
     * @throws UnauthorizedUserException when an HTTP 401 is encountered
     */
    protected String httpRequest(final HttpUriRequest request, final boolean useAnnotations)
            throws NotFoundException, InvalidRequestException, UnauthorizedUserException {

        final ObjectMapper objectMapper = createObjectMapper(useAnnotations);

        try (final CloseableHttpResponse response = getHttpClient().execute(request)) {

            final int statusCode = response.getStatusLine()
                                           .getStatusCode();

            handleResponseCode(response, statusCode, objectMapper);

            /*
             * Request should be valid at this point, so decipher the response entity
             */

            final String jsonData = (response.getEntity() != null)
                    ? EntityUtils.toString(response.getEntity())
                    : StringUtils.EMPTY;
            LOG.trace("JSON Data: {}", jsonData);

            return jsonData;

        } catch (final IOException ioException) {
            LOG.error("Error executing collection request", ioException);
            return null;
        }
    }

    /**
     * Returns an entity id taken from the first location header
     *
     * @param httpUri {@link HttpUriRequest} that is being executed
     * @param pattern Representing the api that should be in the location header url
     * @return an entity id taken from the first location header
     */
    protected String getEntityIdOfLocationHeader(final HttpUriRequest httpUri, final Pattern pattern) {
        final String locationHeader = httpRequestLocationHeader(httpUri, true, pattern);

        if (StringUtils.isNotBlank(locationHeader)) {
            final String[] splitLocationHeader = locationHeader.split("/");
            return splitLocationHeader[splitLocationHeader.length - 1];
        } else {
            return StringUtils.EMPTY;
        }
    }

    /**
     * Executes an {@link HttpUriRequest}, to extract the first location header
     *
     * @param request                 {@link HttpUriRequest} that is being executed
     * @param useAnnotations          {@link Boolean} representing whether to use annotations
     * @param patternOfLocationHeader {@link Pattern} representing the api that should be in the location header url
     * @return {@link String} of the HTTP uri for the location header, if there are multiple or none an empty string
     * is returned
     * @throws NotFoundException         when an HTTP 404 is encountered
     * @throws InvalidRequestException   when an HTTP 400 is encountered
     * @throws UnauthorizedUserException when an HTTP 401 is encountered
     */
    protected String httpRequestLocationHeader(final HttpUriRequest request,
                                               final boolean useAnnotations,
                                               final Pattern patternOfLocationHeader)
            throws NotFoundException, InvalidRequestException, UnauthorizedUserException {

        final ObjectMapper objectMapper = createObjectMapper(useAnnotations);

        try (final CloseableHttpResponse response = getHttpClient().execute(request)) {

            final int statusCode = response.getStatusLine()
                                           .getStatusCode();

            handleResponseCode(response, statusCode, objectMapper);

            /*
             * Request should be valid at this point, so decipher the response entity
             */

            final Header[] locationHeaders = response.getHeaders(LOCATION_HEADER);

            if (locationHeaders.length > 0) {
                if (patternOfLocationHeader.matcher(locationHeaders[0].getValue())
                                           .matches()) {
                    return locationHeaders[0].getValue();
                }
            }
        } catch (final IOException ioException) {
            LOG.error("Error executing collection request", ioException);
            return null;
        }

        return Strings.EMPTY;
    }

    private void handleResponseCode(final CloseableHttpResponse response,
                                    final int statusCode,
                                    final ObjectMapper objectMapper)
            throws NotFoundException, InvalidRequestException, InternalServerException, UnauthorizedUserException {

        if (ALLOWED_ERROR_CODES.contains(statusCode)) {
            try {
                final String jsonResponse = EntityUtils.toString(response.getEntity());
                LOG.debug("Json data: {}", jsonResponse);
                final List<ErrorApi> errors = getErrorsByJsonResponse(objectMapper, jsonResponse);
                final ErrorApi firstError = errors.stream()
                                                  .findFirst()
                                                  .orElseThrow(
                                                          () -> new InternalServerException(
                                                                  ClientErrorRegistry.CLIENT_EMPTY_ERROR_COLLECTION));
                processError(statusCode, firstError, errors);
            } catch (final IOException e) {
                throw new InternalServerException(ClientErrorRegistry.CLIENT_ERROR_EXECUTING_COLLECTION_REQUEST);
            }
        }
    }

    private List<ErrorApi> getErrorsByJsonResponse(final ObjectMapper objectMapper, final String jsonResponse)
            throws IOException {
        if (StringUtils.isBlank(jsonResponse)) {
            final ErrorApi error = ErrorApi.builder(ClientErrorRegistry.CLIENT_EMPTY_RESPONSE.getErrorMessage(),
                                                    ClientErrorRegistry.CLIENT_EMPTY_RESPONSE.getErrorCode())
                                           .build();
            return Collections.singletonList(error);
        } else {
            return getErrorsFromJsonResponse(objectMapper, jsonResponse);
        }
    }

    private void processError(final int statusCode,
                              final ErrorApi errorApi,
                              final List<ErrorApi> errors) {
        switch (statusCode) {
            case SC_NOT_FOUND:
                throw new NotFoundException(errorApi.getError(), errorApi.getCode());
            case SC_BAD_REQUEST:
            case SC_UNPROCESSABLE_ENTITY:
            case SC_METHOD_NOT_ALLOWED:
                throw new InvalidRequestException(errorApi.getError(), errorApi.getCode());
            case SC_INTERNAL_SERVER_ERROR:
                throw new InternalServerException(errorApi.getError(), errorApi.getCode());
            case SC_UNAUTHORIZED:
                throw new UnauthorizedUserException(errorApi.getError(), 401);
            default:
                throw new InternalServerException(ClientErrorRegistry.CLIENT_UNSUPPORTED_STATUS_CODE, statusCode);
        }
    }

    /**
     * Gets a JSON as a {@link StringEntity} for the given entity
     * WARNING: Using with primitives can cause additional wrapping in the resulted String
     * so that behaviour can be unpredictable.
     * For {@link String} use {@link AbstractServiceClient#getStringEntity)
     *
     * @param entity Entity that is to be converted to a JSON-based {@link StringEntity}
     * @param <T>    Type of the Entity
     * @return JSON {@link StringEntity} for the given entity
     */
    <T> StringEntity getJsonStringEntity(final T entity) {

        return getJsonStringEntity(entity, false);
    }

    /**
     * Gets a JSON as a {@link StringEntity} for the given entity
     * WARNING: Using with primitives can cause additional wrapping in the resulted String
     * so that behaviour can be unpredictable.
     * For {@link String} use {@link AbstractServiceClient#getStringEntity)
     *
     * @param entity         Entity that is to be converted to a JSON-based {@link StringEntity}
     * @param <T>            Type of the Entity
     * @param useAnnotations Allows to use annotations to work with JSON
     * @return JSON {@link StringEntity} for the given entity
     */
    protected <T> StringEntity getJsonStringEntity(final T entity, final boolean useAnnotations) {

        try {
            // Convert the entity to JSON String
            final ObjectMapper objectMapper = createObjectMapper(useAnnotations);
            final String entityString = objectMapper.writeValueAsString(entity);
            LOG.trace("JSON String Entity: {}", entityString);

            // Create the HTTP StringEntity with Content-Type set
            final StringEntity jsonEntity = new StringEntity(entityString);
            jsonEntity.setContentType(APPLICATION_JSON);
            return jsonEntity;

        } catch (final UnsupportedEncodingException unSupportedEncodingException) {
            LOG.error("Unsupported encoding setting JSON entity", unSupportedEncodingException);
            return null;

        } catch (final IOException ioException) {
            LOG.error("Error setting JSON entity", ioException);
            return null;
        }
    }

    /**
     * Gets a {@link StringEntity} for the given {@link String}
     *
     * @param entity Entity that is to be converted to a String based {@link StringEntity}
     * @return String {@link StringEntity} for the given String
     */
    StringEntity getStringEntity(final String entity) {
        try {
            final StringEntity stringEntity = new StringEntity(entity);
            stringEntity.setContentType(PLAIN_TEXT);
            return stringEntity;
        } catch (final UnsupportedEncodingException unSupportedEncodingException) {
            LOG.error("Unsupported encoding setting default text String entity", unSupportedEncodingException);
            return null;
        }
    }

    private List<ErrorApi> getErrorsFromJsonResponse(final ObjectMapper objectMapper, final String jsonResponse)
            throws IOException {

        return objectMapper.readValue(jsonResponse,
                                      objectMapper.getTypeFactory()
                                                  .constructCollectionType(List.class,
                                                                           ErrorApi.class));
    }

    private HttpRequestInterceptor createTracingInfoInterceptor() {
        return (httpRequest, httpContext) -> {
            if (Objects.nonNull(tracer)) {
                final Span span = tracer.nextSpan();

                if (Objects.nonNull(span)) {
                    addTracingHeaders(httpRequest, span.context());
                }
            } else {
                final long traceId = UUID.randomUUID()
                                         .getMostSignificantBits();

                final TraceContext traceContext = TraceContext.newBuilder()
                                                              .traceId(traceId)
                                                              .spanId(traceId)
                                                              .build();
                addTracingHeaders(httpRequest, traceContext);
            }
        };
    }

    private void addTracingHeaders(final HttpRequest request, final TraceContext traceContext) {
        request.setHeader(TRACE_ID_NAME, traceContext.traceIdString());
        request.setHeader(SPAN_ID_NAME, toLowerHex(traceContext.spanId()));
        request.setHeader(PARENT_SPAN_ID_NAME, toLowerHex(traceContext.parentIdAsLong()));
        request.setHeader(SAMPLED_NAME, Boolean.TRUE.equals(traceContext.sampled())
                ? "1"
                : "0");
        request.setHeader(FLAGS_NAME, Boolean.TRUE.equals(traceContext.debug())
                ? "1"
                : "0");
    }

    protected Logger getLogger() {
        return LOG;
    }

    /**
     * Wrapper class for HttpPost to allow for making an HttpDelete request with body
     *
     * @author alex.sharp (asharp)
     * @since 0.13
     */
    static class HttpDeleteWithBody extends HttpPost {

        HttpDeleteWithBody(final URI uri) {
            super(uri);
        }

        @Override
        public String getMethod() {
            return "DELETE";
        }
    }

    protected URIBuilder getUriBuilder(final String path, final String... args) {
        return new URIBuilder().setScheme(getHttpScheme())
                               .setHost(getApiHost())
                               .setPort(getApiPort())
                               .setPath(String.format(path, args));
    }

    protected <T> Optional<T> suppressNotFoundException(final Supplier<T> responsePayloadSupplier) {
        try {
            return Optional.ofNullable(responsePayloadSupplier.get());
        } catch (final NotFoundException ex) {
            getLogger().info("Suppress exception:{}", ex);
            return Optional.empty();
        }
    }
}
