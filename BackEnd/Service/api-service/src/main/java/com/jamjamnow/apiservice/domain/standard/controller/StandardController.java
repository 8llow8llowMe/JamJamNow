package com.jamjamnow.apiservice.domain.standard.controller;

import com.jamjamnow.apiservice.domain.standard.dto.EmdResponse;
import com.jamjamnow.apiservice.domain.standard.dto.SggResponse;
import com.jamjamnow.apiservice.domain.standard.dto.SidoResponse;
import com.jamjamnow.apiservice.domain.standard.service.StandardService;
import com.jamjamnow.commonmodule.dto.Response;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/standards")
public class StandardController {

    private final StandardService standardService;

    @GetMapping("/sidos")
    public ResponseEntity<Response<List<SidoResponse>>> getSidoList() {
        List<SidoResponse> sidoResponses = standardService.getSidoList();
        return ResponseEntity.ok().body(Response.success(sidoResponses));
    }

    @GetMapping("/sidos/{ctpvCd}/sggs")
    public ResponseEntity<Response<List<SggResponse>>> getSggList(@PathVariable String ctpvCd) {
        List<SggResponse> sggResponses = standardService.getSggList(ctpvCd);
        return ResponseEntity.ok().body(Response.success(sggResponses));
    }

    @GetMapping("/sggs/{sggCd}/emds")
    public ResponseEntity<Response<List<EmdResponse>>> getEmdList(@PathVariable String sggCd) {
        List<EmdResponse> emdResponses = standardService.getEmdList(sggCd);
        return ResponseEntity.ok().body(Response.success(emdResponses));
    }
}
