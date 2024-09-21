package com.friendly.services.uiservices.frame.controller;

import static com.friendly.commons.CommonRegistry.IOT_AUTH_HEADER;

import com.friendly.commons.models.device.frame.GetQoeDetailsRequest;
import com.friendly.commons.models.device.frame.response.FramesSimpleResponse;
import com.friendly.commons.models.device.frame.response.GetQoeDetailsResponse;
import com.friendly.commons.models.device.frame.response.ViewFramesResponse;
import com.friendly.commons.models.device.response.GetParametersFullNames;
import com.friendly.commons.models.device.response.QoeFrameItem;
import com.friendly.commons.models.request.LongIdRequest;
import com.friendly.commons.models.request.LongIdsRequest;
import com.friendly.commons.models.view.ViewFrame;
import com.friendly.services.infrastructure.base.BaseController;
import com.friendly.services.uiservices.frame.service.FrameService;
import com.friendly.services.qoemonitoring.service.QoeFrameService;
import com.friendly.services.settings.alerts.AlertProvider;
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
@Api(value = "Operations with frames")
@RequestMapping("iotw/Frame")
public class FrameController extends BaseController {

    @NonNull
    private final FrameService frameService;

    @NonNull
    private final QoeFrameService qoeFrameService;

    public FrameController(@NonNull AlertProvider alertProvider,
                           @NonNull FrameService frameService, @NonNull QoeFrameService qoeFrameService) {
        super(alertProvider);
        this.frameService = frameService;
        this.qoeFrameService = qoeFrameService;
    }

    @ApiOperation(value = "Get Frame")
    @PostMapping("item")
    public ViewFramesResponse getFrame(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                       @RequestHeader(IOT_AUTH_HEADER) final String token,
                                       @RequestBody LongIdsRequest request) {

        return frameService.getFrames(token, request.getIds());
    }

    @ApiOperation(value = "Get Frames")
    @PostMapping("items")
    public FramesSimpleResponse getFrames(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                          @RequestHeader(IOT_AUTH_HEADER) final String token) {

        return frameService.getFrames(token);
    }

    @ApiOperation(value = "Create or Update Frame")
    @PutMapping("item")
    public ViewFrame createUpdateFrame(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                       @RequestHeader(IOT_AUTH_HEADER) final String token,
                                       @RequestBody final ViewFrame frame) {

        return frameService.createUpdateFrame(token, frame);
    }

    @ApiOperation(value = "Delete Frame")
    @DeleteMapping("item")
    public void deleteFrames(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                             @RequestHeader(IOT_AUTH_HEADER) final String token,
                             @RequestBody final LongIdsRequest request) {

        frameService.deleteFrames(token, request.getIds());
    }

    @ApiOperation(value = "Get QoE frame item")
    @PostMapping("qoe/item")
    public List<QoeFrameItem> getQoeFrameItem(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                         @RequestHeader(IOT_AUTH_HEADER) final String token,
                                              @RequestBody final LongIdsRequest request) {

        return qoeFrameService.getQoeFrameItem(token, request);
    }

    @ApiOperation(value = "Create QoE frame item")
    @PutMapping("qoe/item")
    public QoeFrameItem createOrUpdateQoeFrame(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                               @RequestHeader(IOT_AUTH_HEADER) final String token,
                                               @RequestBody final QoeFrameItem request) {

        return qoeFrameService.createQoeFrameItem(token, request);
    }

    @ApiOperation(value = "Delete QoE frame item")
    @DeleteMapping("qoe/item")
    public void deleteQoeFrame(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                    @RequestHeader(IOT_AUTH_HEADER) final String token,
                                       @RequestBody final LongIdRequest request) {

        qoeFrameService.deleteQoeFrameItem(token, request);
    }

    @ApiOperation(value = "Get QoE frame item details")
    @PostMapping("qoe/item/details")
    public GetQoeDetailsResponse getQoeFrameItemDetails(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                               @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                        @RequestBody final GetQoeDetailsRequest request) {

        return qoeFrameService.getQoeFrameItemDetails(token, request);
    }


    @ApiOperation(value = "Get QoE parameters full names")
    @PostMapping("qoe/parameters/names")
    public GetParametersFullNames getParametersFullNames(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                         @RequestHeader(IOT_AUTH_HEADER) final String token) {

        return qoeFrameService.getParametersFullNames(token);
    }
}