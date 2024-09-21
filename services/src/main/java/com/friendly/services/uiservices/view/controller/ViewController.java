package com.friendly.services.uiservices.view.controller;

import static com.friendly.commons.CommonRegistry.IOT_AUTH_HEADER;

import com.friendly.commons.models.request.LongIdRequest;
import com.friendly.commons.models.request.LongIdsRequest;
import com.friendly.commons.models.view.AbstractView;
import com.friendly.commons.models.view.ConditionItem;
import com.friendly.commons.models.view.GetDeviceViewsListRequest;
import com.friendly.commons.models.view.response.ConditionsResponse;
import com.friendly.commons.models.view.response.DeviceColumnsResponse;
import com.friendly.commons.models.view.response.ViewsSimpleResponse;
import com.friendly.services.infrastructure.base.BaseController;
import com.friendly.services.settings.alerts.AlertProvider;
import com.friendly.services.uiservices.view.ColumnKey;
import com.friendly.services.uiservices.view.ViewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
import lombok.NonNull;
import org.springframework.web.bind.annotation.*;

/**
 * Controller that exposes an API to interact with Device
 * <p>
 * This controller is primarily a wrapper around the Device
 * </p>
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@RestController
@Api(value = "Operations with view")
@RequestMapping("iotw/View")
public class ViewController extends BaseController {

    @NonNull
    private final ViewService viewService;

    public ViewController(@NonNull AlertProvider alertProvider,
                          @NonNull ViewService viewService) {
        super(alertProvider);
        this.viewService = viewService;
    }

    @ApiOperation(value = "Get All Views")
    @PostMapping("items")
    public ViewsSimpleResponse getDeviceUpdateListViews(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                        @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                        @RequestBody final GetDeviceViewsListRequest request) {

        return viewService.getViews(token, request);
    }

    @ApiOperation(value = "Get View")
    @PostMapping("item")
    public AbstractView getView(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                @RequestHeader(IOT_AUTH_HEADER) final String token,
                                @RequestBody final LongIdRequest request) {

        return viewService.getView(token, request.getId());
    }

    @ApiOperation(value = "Create or Update View")
    @PutMapping("item")
    public AbstractView createView(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                   @RequestHeader(IOT_AUTH_HEADER) final String token,
                                   @RequestBody final AbstractView view) {

        return viewService.createOrUpdateView(token, view);
    }

    @ApiOperation(value = "Delete Views")
    @DeleteMapping("items")
    public void deleteViews(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                               @RequestHeader(IOT_AUTH_HEADER) final String token,
                               @RequestBody final LongIdsRequest request) {

        viewService.deleteViews(token, request.getIds());
    }

    @ApiOperation(value = "Get Device Columns")
    @PostMapping("columns")
    public DeviceColumnsResponse getDeviceColumns(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                  @RequestHeader(IOT_AUTH_HEADER) final String token) {

        return viewService.getDeviceColumns(token);
    }

    @ApiOperation(value = "Get Device Column Filters")
    @PostMapping("filterConditions")
    public ConditionsResponse getDeviceColumnFilters(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                     @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                     @RequestBody final ColumnKey columnKey) {

        return viewService.getFiltersByColumn(token, columnKey.getColumnKey());
    }

    @ApiOperation(value = "Get Device Items Conditions")
    @PostMapping("items/conditions")
    public List<ConditionItem> getDeviceItemConditions(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                     @RequestHeader(IOT_AUTH_HEADER) final String token) {

        return viewService.getItemsConditions(token);
    }

    @ApiOperation(value = "Create Device Items Conditions")
    @PutMapping("items/conditions")
    public List<ConditionItem> createDeviceItemConditions(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                          @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                          @RequestBody final List<ConditionItem> conditionItems) {

        return viewService.createConditionItems(token, conditionItems);
    }

}