package ca.gc.dfo.psffs.controllers;

import ca.gc.dfo.psffs.json.ChecksumObjectRequest;
import ca.gc.dfo.psffs.json.ChecksumObjectsRequest;
import ca.gc.dfo.psffs.json.ChecksumObjectsResponse;
import ca.gc.dfo.psffs.services.ChecksumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(ChecksumObjectController.CHECKSUM_OBJECTS_PATH)
public class ChecksumObjectController
{
    public static final String CHECKSUM_OBJECTS_PATH = "/checksumObjects";

    @Autowired
    private ChecksumService checksumService;

    @RequestMapping(method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public @ResponseBody
    ChecksumObjectsResponse getChecksumObjects(@RequestBody ChecksumObjectsRequest request)
    {
        ChecksumObjectsResponse response = new ChecksumObjectsResponse();
        if (request.getRequests() != null && request.getRequests().size() > 0) {
            for (ChecksumObjectRequest req : request.getRequests()) {
                response.getResponses().add(checksumService.wrapChecksumAroundObjects(ChecksumService.Objects.valueOf(req.getObjectStoreName().toUpperCase()).ENTITY, req.getChecksumUUID()));
            }
        }
        return response;
    }
}
