/*
 * Cerberus Copyright (C) 2013 - 2017 cerberustesting
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This file is part of Cerberus.
 *
 * Cerberus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cerberus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cerberus.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.cerberus.core.api.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cerberus.core.api.controllers.wrappers.ResponseWrapper;
import org.cerberus.core.api.dto.v001.ApplicationDTOV001;
import org.cerberus.core.api.dto.v001.TestcaseDTOV001;
import org.cerberus.core.api.dto.v001.TestcaseSimplifiedCreationDTOV001;
import org.cerberus.core.api.dto.views.View;
import org.cerberus.core.api.mappers.v001.TestcaseMapperV001;
import org.cerberus.core.api.services.PublicApiAuthenticationService;
import org.cerberus.core.exception.CerberusException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;
import org.cerberus.core.crud.entity.Application;
import org.cerberus.core.crud.entity.CountryEnvironmentParameters;
import org.cerberus.core.crud.entity.Invariant;
import org.cerberus.core.crud.entity.TestCase;
import org.cerberus.core.crud.entity.TestCaseCountry;
import org.cerberus.core.crud.entity.TestCaseStep;
import org.cerberus.core.crud.entity.TestCaseStepAction;
import org.cerberus.core.crud.entity.TestCaseStepActionControl;
import org.cerberus.core.crud.service.IApplicationService;
import org.cerberus.core.crud.service.ICountryEnvironmentParametersService;
import org.cerberus.core.crud.service.IInvariantService;
import org.cerberus.core.crud.service.ITestCaseCountryService;
import org.cerberus.core.crud.service.ITestCaseService;
import org.cerberus.core.crud.service.ITestCaseStepActionControlService;
import org.cerberus.core.crud.service.ITestCaseStepActionService;
import org.cerberus.core.crud.service.ITestCaseStepService;

/**
 * @author MorganLmd
 */
@AllArgsConstructor
@Api(tags = "Testcase")
@Validated
@RestController
@RequestMapping(path = "/public/testcases")
public class TestcaseController {

    private static final String API_VERSION_1 = "X-API-VERSION=1";
    private static final String API_KEY = "X-API-KEY";
    private final IInvariantService invariantService;
    private final ITestCaseService testCaseService;
    private final ITestCaseCountryService testCaseCountryService;
    private final ITestCaseStepService testCaseStepService;
    private final ITestCaseStepActionService testCaseStepActionService;
    private final ITestCaseStepActionControlService testCaseStepActionControlService;
    private final IApplicationService applicationService;
    private final ICountryEnvironmentParametersService countryEnvironmentParametersService;
    private final TestcaseMapperV001 testcaseMapper;
    private final PublicApiAuthenticationService apiAuthenticationService;
    private static final Logger LOG = LogManager.getLogger(TestcaseController.class);

    @ApiOperation("Get all testcases filtered by test")
    @ApiResponse(code = 200, message = "ok", response = TestcaseDTOV001.class, responseContainer = "List")
    @JsonView(View.Public.GET.class)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{testFolderId}", headers = {API_VERSION_1}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseWrapper<List<TestcaseDTOV001>> findTestcasesByTest(
            @PathVariable("testFolderId") String testFolderId,
            @RequestHeader(name = API_KEY, required = false) String apiKey,
            Principal principal) {
        this.apiAuthenticationService.authenticate(principal, apiKey);
        return ResponseWrapper.wrap(
                this.testCaseService.findTestCaseByTest(testFolderId)
                        .stream()
                        .map(this.testcaseMapper::toDTO)
                        .collect(Collectors.toList())
        );
    }

    @ApiOperation("Get a testcase filtered by testFolderId and testCaseFolderId")
    @ApiResponse(code = 200, message = "ok", response = TestcaseDTOV001.class)
    @JsonView(View.Public.GET.class)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{testFolderId}/{testcaseId}", headers = {API_VERSION_1}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseWrapper<TestcaseDTOV001> findTestcaseByTestAndTestcase(
            @PathVariable("testFolderId") String testFolderId,
            @PathVariable("testcaseId") String testcaseId,
            @RequestHeader(name = API_KEY, required = false) String apiKey,
            Principal principal) throws CerberusException {
        this.apiAuthenticationService.authenticate(principal, apiKey);
        return ResponseWrapper.wrap(
                this.testcaseMapper
                        .toDTO(
                                this.testCaseService.findTestCaseByKeyWithDependencies(testFolderId, testcaseId, true).getItem()
                        )
        );
    }

    @ApiOperation("Create a new Testcase")
    @ApiResponse(code = 200, message = "ok")
    @JsonView(View.Public.GET.class)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(headers = {API_VERSION_1}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseWrapper<TestcaseDTOV001> createTestcase(
            @Valid @JsonView(View.Public.POST.class) @RequestBody TestcaseDTOV001 newTestcase,
            @RequestHeader(name = API_KEY, required = false) String apiKey,
            Principal principal) throws CerberusException {
        this.apiAuthenticationService.authenticate(principal, apiKey);

        return ResponseWrapper.wrap(
                this.testcaseMapper.toDTO(
                        this.testCaseService.createTestcaseWithDependenciesAPI(
                                this.testcaseMapper.toEntity(newTestcase)
                        )
                )
        );
    }

    @ApiOperation("Create a new Testcase with only few information")
    @ApiResponse(code = 200, message = "ok")
    @JsonView(View.Public.GET.class)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/create", headers = {API_VERSION_1}, produces = MediaType.APPLICATION_JSON_VALUE)
    public String createSimplifiedTestcase(
            @Valid @JsonView(View.Public.POST.class) @RequestBody TestcaseSimplifiedCreationDTOV001 newTestcase,
            Principal principal) throws CerberusException {

        JSONObject jsonResponse = new JSONObject();

        if (!this.applicationService.exist(newTestcase.getApplication())) {

            this.applicationService.create(
                    Application.builder()
                            .application(newTestcase.getApplication())
                            .description("")
                            .sort(10)
                            .type(newTestcase.getType())
                            .system(newTestcase.getSystem())
                            .subsystem("")
                            .svnurl("")
                            .bugTrackerNewUrl("")
                            .bugTrackerNewUrl("")
                            .UsrCreated(principal.getName())
                            .build());

            this.countryEnvironmentParametersService.create(
                    CountryEnvironmentParameters.builder()
                            .system(newTestcase.getSystem())
                            .country(newTestcase.getCountry())
                            .environment(newTestcase.getEnvironment())
                            .application(newTestcase.getApplication())
                            .ip(newTestcase.getUrl())
                            .domain("")
                            .url("")
                            .urlLogin("")
                            .var1("")
                            .var2("")
                            .var3("")
                            .var4("")
                            .build());
        }

        this.testCaseService.create(
                TestCase.builder()
                        .test(newTestcase.getTestFolderId())
                        .testcase(newTestcase.getTestcaseId())
                        .application(newTestcase.getApplication())
                        .description(newTestcase.getDescription())
                        .priority(1)
                        .status("WORKING")
                        .conditionOperator("always")
                        .conditionValue1("")
                        .conditionValue2("")
                        .conditionValue3("")
                        .type("AUTOMATED")
                        .isActive(true)
                        .isActivePROD(true)
                        .isActiveQA(true)
                        .isActiveUAT(true)
                        .usrCreated(principal.getName())
                        .build());

        List<Invariant> countryInvariantList = this.invariantService.readByIdName("COUNTRY");
        for(Invariant countryInvariant : countryInvariantList){

            this.testCaseCountryService.create(
                    TestCaseCountry.builder()
                            .test(newTestcase.getTestFolderId())
                            .testcase(newTestcase.getTestcaseId())
                            .country(countryInvariant.getValue())
                            .usrCreated(principal.getName())
                            .build());
        }



        this.testCaseStepService.create(
                TestCaseStep.builder()
                        .test(newTestcase.getTestFolderId())
                        .testcase(newTestcase.getTestcaseId())
                        .stepId(0)
                        .sort(1)
                        .isUsingLibraryStep(false)
                        .libraryStepStepId(0)
                        .loop("onceIfConditionTrue")
                        .conditionOperator("always")
                        .description("Go to the homepage and take a screenshot")
                        .usrCreated(principal.getName())
                        .build());

        this.testCaseStepActionService.create(
                TestCaseStepAction.builder()
                        .test(newTestcase.getTestFolderId())
                        .testcase(newTestcase.getTestcaseId())
                        .stepId(0)
                        .actionId(0)
                        .sort(1)
                        .conditionOperator("always")
                        .conditionValue1("")
                        .conditionValue2("")
                        .conditionValue3("")
                        .action("openUrlWithBase")
                        .value1("/")
                        .value2("")
                        .value3("")
                        .description("Open the homepage")
                        .conditionOperator("always")
                        .usrCreated(principal.getName())
                        .build());

        this.testCaseStepActionControlService.create(
                TestCaseStepActionControl.builder()
                        .test(newTestcase.getTestFolderId())
                        .testcase(newTestcase.getTestcaseId())
                        .stepId(0)
                        .actionId(0)
                        .controlId(0)
                        .sort(1)
                        .conditionOperator("always")
                        .conditionValue1("")
                        .conditionValue2("")
                        .conditionValue3("")
                        .control("takeScreenshot")
                        .value1("")
                        .value2("")
                        .value3("")
                        .description("Take a screenshot")
                        .usrCreated(principal.getName())
                        .build());

        try {
            jsonResponse.put("test", newTestcase.getTestFolderId());
            jsonResponse.put("testcase", newTestcase.getTestcaseId());
            jsonResponse.put("messageType", "OK");
            jsonResponse.put("message", "success");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonResponse.toString();
    }

    @ApiOperation("Update a Testcase")
    @ApiResponse(code = 200, message = "ok")
    @JsonView(View.Public.GET.class)
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(path = "/{testFolderId}/{testcaseId}", headers = {API_VERSION_1}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseWrapper<TestcaseDTOV001> update(
            @PathVariable("testcaseId") String testcaseId,
            @PathVariable("testFolderId") String testFolderId,
            @Valid @JsonView(View.Public.PUT.class) @RequestBody TestcaseDTOV001 testcaseToUpdate,
            @RequestHeader(name = API_KEY, required = false) String apiKey,
            Principal principal) throws CerberusException {

        this.apiAuthenticationService.authenticate(principal, apiKey);

        return ResponseWrapper.wrap(
                this.testcaseMapper.toDTO(
                        this.testCaseService.updateTestcaseAPI(
                                testFolderId,
                                testcaseId,
                                this.testcaseMapper.toEntity(testcaseToUpdate)
                        ))
        );
    }
}
