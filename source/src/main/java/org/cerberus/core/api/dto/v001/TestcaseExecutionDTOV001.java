/**
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

package org.cerberus.core.api.dto.v001;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.cerberus.core.api.dto.views.View;

/**
 * @author lucashimpens
 */
@Data
@Builder
@Jacksonized
@ApiModel(value = "TestcaseExecution")
public class TestcaseExecutionDTOV001 {

    @JsonView(value = {View.Public.GET.class})
    @ApiModelProperty(position = 0)
    private long id;

    @JsonView(value = {View.Public.GET.class})
    @ApiModelProperty(position = 1)
    private String testFolderId;

    @JsonView(value = {View.Public.GET.class})
    @ApiModelProperty(position = 2)
    private String testCaseId;

    @JsonView(value = {View.Public.GET.class})
    @ApiModelProperty(position = 3)
    private String description;

    @JsonView(value = {View.Public.GET.class})
    @ApiModelProperty(position = 4)
    private InvariantDTOV001 environment;

    @JsonView(value = {View.Public.GET.class})
    @ApiModelProperty(position = 5)
    private InvariantDTOV001 environmentData;

    @JsonView(value = {View.Public.GET.class})
    @ApiModelProperty(position = 6)
    private InvariantDTOV001 country;

    @JsonView(value = {View.Public.GET.class})
    @ApiModelProperty(position = 7)
    private InvariantDTOV001 priority;

    @JsonView(value = {View.Public.GET.class})
    @ApiModelProperty(position = 8)
    private int testcaseVersion;

    @JsonView(value = {View.Public.GET.class})
    @ApiModelProperty(position = 9)
    private String build;

    @JsonView(value = {View.Public.GET.class})
    @ApiModelProperty(position = 10)
    private String revision;

    @JsonView(value = {View.Public.GET.class})
    @ApiModelProperty(position = 11)
    private long start;

    @JsonView(value = {View.Public.GET.class})
    @ApiModelProperty(position = 12)
    private long end;

    @JsonView(value = {View.Public.GET.class})
    @ApiModelProperty(position = 14)
    private long durationInMillis;

    @JsonView(value = {View.Public.GET.class})
    @ApiModelProperty(position = 15)
    private String controlStatus;

    @JsonView(value = {View.Public.GET.class})
    @ApiModelProperty(position = 16)
    private String controlMessage;

    @JsonView(value = {View.Public.GET.class})
    @ApiModelProperty(position = 17)
    private CampaignExecutionRobotDTOV001 robot;

    @JsonView(value = {View.Public.GET.class})
    @ApiModelProperty(position = 18)
    private String url;

    @JsonView(value = {View.Public.GET.class})
    @ApiModelProperty(position = 19)
    private String tag;

    @JsonView(value = {View.Public.GET.class})
    @ApiModelProperty(position = 20)
    private String status;

    @JsonView(value = {View.Public.GET.class})
    @ApiModelProperty(position = 21)
    private String usrExecuted;

    @JsonView(value = {View.Public.GET.class})
    @ApiModelProperty(position = 22)
    private long queueID;

    @JsonView(value = {View.Public.GET.class})
    @ApiModelProperty(position = 23)
    private boolean isManualExecution;

    @JsonView(value = {View.Public.GET.class})
    @ApiModelProperty(position = 24)
    private String system;

    @JsonView(value = {View.Public.GET.class})
    @ApiModelProperty(position = 25)
    private String application;

    @JsonView(value = {View.Public.GET.class})
    @ApiModelProperty(position = 26)
    private String usrCreated;

    @JsonView(value = {View.Public.GET.class})
    @ApiModelProperty(position = 27)
    private String dateCreated;

    @JsonView(value = {View.Public.GET.class})
    @ApiModelProperty(position = 28)
    private String usrModif;

    @JsonView(value = {View.Public.GET.class})
    @ApiModelProperty(position = 29)
    private String dateModif;
}
