package com.friendly.services.filemanagement.controller;

import com.friendly.commons.models.FTPage;
import com.friendly.commons.models.device.response.FileInstancesResponse;
import com.friendly.commons.models.device.response.FileNamesResponse;
import com.friendly.commons.models.device.response.FileTypeFiltersResponse;
import com.friendly.commons.models.file.FileExistResponse;
import com.friendly.commons.models.file.FileFtp;
import com.friendly.commons.models.file.FileFtpKey;
import com.friendly.commons.models.file.FileListFtpItemBody;
import com.friendly.commons.models.file.FilesFtpBody;
import com.friendly.commons.models.file.FilesFtpTypesBody;
import com.friendly.services.infrastructure.base.BaseController;
import com.friendly.services.filemanagement.model.GetFileInstancesByManufacturerAndModelRequest;
import com.friendly.services.filemanagement.model.GetFileNamesRequest;
import com.friendly.services.filemanagement.service.FileManagementService;
import com.friendly.services.settings.alerts.AlertProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static com.friendly.commons.CommonRegistry.IOT_AUTH_HEADER;

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
@Api(value = "File Management")
@RequestMapping("iotw/File")
public class FileManagementController extends BaseController {

    @NonNull
    private final FileManagementService fileManagementService;

    public FileManagementController(@NonNull AlertProvider alertProvider,
                                    @NonNull FileManagementService frameService) {
        super(alertProvider);
        this.fileManagementService = frameService;
    }

    @ApiOperation(value = "Get File Types")
    @PostMapping("fileTypes")
    public FileTypeFiltersResponse getFileTypes(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                @RequestBody FilesFtpTypesBody filesFtpTypesBody) {

        return fileManagementService.getFileTypes(token, filesFtpTypesBody);
    }

    @ApiOperation(value = "Get Files")
    @PostMapping("items")
    public FTPage<FileFtp> getFileList(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                     @RequestHeader(IOT_AUTH_HEADER) final String token,
                                       @RequestBody final FilesFtpBody filesFtpBody) {

        return fileManagementService.getList(token, filesFtpBody);
    }

    @ApiOperation(value = "Get File")
    @PostMapping("item")
    public FileFtp getFileList(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                               @RequestHeader(IOT_AUTH_HEADER) final String token,
                               @RequestBody final FileFtpKey fileFtpKey) {

        return fileManagementService.getFile(token, fileFtpKey);
    }

    @ApiOperation(value = "Update file")
    @PutMapping("item/edit")
    public FileFtp edit(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                        @RequestHeader(IOT_AUTH_HEADER) final String token,
                        @RequestBody final FileFtp fileFtp) {

        return fileManagementService.editFile(token, fileFtp);
    }

    @ApiOperation(value = "Add file")
    @PutMapping(value = "item/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileFtp add(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                       @RequestHeader(IOT_AUTH_HEADER) final String token,
                       @RequestPart(value = "fileFtp")
                       @Parameter(schema = @Schema(type = "string", format = "binary"))
                               FileFtp fileFtp,
                       @RequestPart(value = "file")
                               MultipartFile file) {

        return fileManagementService.addFile(token, fileFtp, file);
    }

    @ApiOperation(value = "Delete File")
    @DeleteMapping("item")
    public void deleteFile(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                           @RequestHeader(IOT_AUTH_HEADER) final String token,
                           @RequestBody final FileListFtpItemBody fileBody) {

        fileManagementService.deleteFile(token, fileBody.getFiles());
    }

    @ApiOperation(value = "Does file exist")
    @PostMapping("item/exist")
    public FileExistResponse exist(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                   @RequestHeader(IOT_AUTH_HEADER) final String token,
                                   @RequestBody FileFtpKey fileFtpKey) {

        return fileManagementService.isFileExist(token, fileFtpKey);
    }

    @ApiOperation(value = "Get file names")
    @PostMapping("fileNames")
    public FileNamesResponse getFileNames(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                          @RequestHeader(IOT_AUTH_HEADER) final String token,
                                          @RequestBody GetFileNamesRequest body) {
        return fileManagementService.getFileNames(token, body.getManufacturer(), body.getModel(), body.getFileTypeId());
    }

    @ApiOperation(value = "Get file instances by manufacturer and model")
    @PostMapping("/instances")
    public FileInstancesResponse getFileInstances(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                  @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                  @RequestBody GetFileInstancesByManufacturerAndModelRequest body) {
        return fileManagementService.getFileInstancesByManufacturerAndModel(token, body.getManufacturer(), body.getModel(), body.getFileTypeId());
    }
}