package com.jamjamnow.backend.global.infrastructor.openapi;

import com.jamjamnow.backend.global.common.dto.Response;
import com.jamjamnow.backend.global.infrastructor.openapi.dto.OpenApiDynamicWrapper;
import com.jamjamnow.backend.global.infrastructor.openapi.dto.OpenApiResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test/openapi")
public class OpenApiTestController {

    private final DynamicOpenApiClient openApiClient;

    @GetMapping("/transport")
    public Response<OpenApiResponse> testTransportApi(
        @RequestParam String opr_ymd,
        @RequestParam(required = false) String ctpv_cd) {

        Map<String, String> param = Map.of(
            "opr_ymd", opr_ymd,
            "ctpv_cd", ctpv_cd != null ? ctpv_cd : "11"
        );

        OpenApiDynamicWrapper wrapper =
            openApiClient.fetch("transport", param, OpenApiDynamicWrapper.class);

        OpenApiResponse res = wrapper.getFirstResponse();

        if (res == null || res.header() == null) {
            return Response.fail("NO_DATA", "OpenAPI 응답이 비어 있습니다.");
        }

        return Response.success(res);
    }
}
