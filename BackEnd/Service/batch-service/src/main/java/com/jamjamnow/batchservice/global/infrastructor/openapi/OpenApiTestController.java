package com.jamjamnow.batchservice.global.infrastructor.openapi;

import com.jamjamnow.batchservice.domain.bus.entity.RawBusUsage;
import com.jamjamnow.batchservice.domain.bus.service.RawBusUsageService;
import com.jamjamnow.batchservice.global.infrastructor.openapi.dto.BusUsageItem;
import com.jamjamnow.batchservice.global.infrastructor.openapi.dto.OpenApiDynamicWrapper;
import com.jamjamnow.batchservice.global.infrastructor.openapi.dto.OpenApiResponse;
import com.jamjamnow.commonmodule.dto.Response;
import com.jamjamnow.persistencemodule.global.util.SnowflakeIdGenerator;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test/openapi")
public class OpenApiTestController {

    private final DynamicOpenApiClient openApiClient;
    private final SnowflakeIdGenerator snowflakeIdGenerator;
    private final RawBusUsageService rawBusUsageService;

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

    @PostMapping("/transport/save")
    public Response<String> saveToDb(
        @RequestParam String opr_ymd,
        @RequestParam(required = false) String ctpv_cd) {

        Map<String, String> param = Map.of(
            "opr_ymd", opr_ymd,
            "ctpv_cd", ctpv_cd != null ? ctpv_cd : "11"
        );

        OpenApiDynamicWrapper wrapper =
            openApiClient.fetch("transport", param, OpenApiDynamicWrapper.class);

        OpenApiResponse res = Optional.ofNullable(wrapper.getFirstResponse())
            .orElseThrow(() -> new IllegalArgumentException("OpenAPI 응답 없음"));

        List<BusUsageItem> items = Optional.ofNullable(res.body())
            .map(OpenApiResponse.Body::items)
            .map(OpenApiResponse.Body.Items::item)
            .orElse(List.of());

        List<RawBusUsage> toSave = items.stream()
            .map(item -> RawBusUsage.builder()
                .id(snowflakeIdGenerator.generateId())
                .oprYmd(parseDate(item.opr_ymd()))
                .dowNm(item.dow_nm())
                .ctpvCd(item.ctpv_cd())
                .ctpvNm(item.ctpv_nm())
                .sggCd(item.sgg_cd())
                .sggNm(item.sgg_nm())
                .emdCd(item.emd_cd())
                .emdNm(item.emd_nm())
                .rteId(item.rte_id())
                .sttnId(item.sttn_id())
                .usersTypeNm(item.users_type_nm())
                .utztnNope(item.utztn_nope())
                .build())
            .toList();

        rawBusUsageService.saveAllForce(toSave);

        return Response.success("저장 성공: " + toSave.size() + "건");
    }

    private LocalDate parseDate(String yyyymmdd) {
        return LocalDate.of(
            Integer.parseInt(yyyymmdd.substring(0, 4)),
            Integer.parseInt(yyyymmdd.substring(4, 6)),
            Integer.parseInt(yyyymmdd.substring(6, 8))
        );
    }
}
