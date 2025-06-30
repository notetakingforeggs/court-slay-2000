// TODO remove this if rest resource repositry works well



//package com.notetakingforeggs.WhitechapelAdviceSpringBackend.controller;
//
//import com.notetakingforeggs.WhitechapelAdviceSpringBackend.model.CourtCase;
//import com.notetakingforeggs.WhitechapelAdviceSpringBackend.service.CourtCaseService;
//import org.apache.coyote.Response;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.util.List;
//
////recall, path var for identifying resource, request params for filtering results
//
//@Controller
//@RequestMapping("api/v1/courtcase")
//
//public class CourtCaseController {
//
//    private final CourtCaseService service;
//
//    public CourtCaseController(CourtCaseService service) {
//        this.service = service;
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<List<CourtCase>> getCourtCasesByRegion(@PathVariable long regionId){
//        List<CourtCase> courtCasesByRegion = service.findByRegion(regionId);
//        return new ResponseEntity<>(courtCasesByRegion, HttpStatus.OK);
//    }
//
//}
