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
package org.cerberus.core.crud.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cerberus.core.engine.execution.impl.RecorderService;
import org.cerberus.core.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.List;

/**
 * Map la table Service
 *
 * @author cte
 */
@Data
public class AppService {

    private String service; // Name and reference of the service
    private String application; // application that reference the service.
    private String type; // either SOAP/REST
    private String method; // Method used : POST/GET
    private String servicePath; // Path to access the service
    private boolean isFollowRedir; // Path to access the service
    private String operation; // Operation used for SOAP Requests
    private String serviceRequest; // Content of the request.
    private String attachementURL; // Attachement in cas of SOAP call with attachement.
    private String fileName;
    private String kafkaTopic;
    private String kafkaKey;
    private String kafkaFilterPath;
    private String kafkaFilterValue;
    private String kafkaFilterHeaderPath;
    private String kafkaFilterHeaderValue;
    private boolean isAvroEnable;
    private String schemaRegistryURL;
    private String parentContentService;
    private String group; // Information in order to group the services in order to organise them
    private String description;
    private String UsrCreated;
    private Timestamp DateCreated;
    private String UsrModif;
    private Timestamp DateModif;

    /**
     * From here are data outside database model.
     */
    @EqualsAndHashCode.Exclude
    private List<AppServiceContent> contentList;
    @EqualsAndHashCode.Exclude
    private List<AppServiceHeader> headerList;
    @EqualsAndHashCode.Exclude
    private String proxyHost;
    @EqualsAndHashCode.Exclude
    private int proxyPort;
    @EqualsAndHashCode.Exclude
    private boolean proxy;
    @EqualsAndHashCode.Exclude
    private boolean proxyWithCredential;
    @EqualsAndHashCode.Exclude
    private String proxyUser;
    // Result from call.
    @EqualsAndHashCode.Exclude
    private String responseHTTPVersion;
    @EqualsAndHashCode.Exclude
    private int responseHTTPCode;
    @EqualsAndHashCode.Exclude
    private String responseHTTPBody;
    @EqualsAndHashCode.Exclude
    private String responseHTTPBodyContentType;
    @EqualsAndHashCode.Exclude
    private List<AppServiceHeader> responseHeaderList;
    @EqualsAndHashCode.Exclude
    private int timeoutms; // Timeout used during service request
    @EqualsAndHashCode.Exclude
    private byte[] file;
    @EqualsAndHashCode.Exclude
    private long kafkaResponseOffset;
    @EqualsAndHashCode.Exclude
    private int kafkaResponsePartition;
    @EqualsAndHashCode.Exclude
    private int kafkaWaitNbEvent;
    @EqualsAndHashCode.Exclude
    private int kafkaWaitSecond;
    @EqualsAndHashCode.Exclude
    private boolean recordTraceFile;

    /**
     * Invariant PROPERTY TYPE String.
     */
    public static final String TYPE_SOAP = "SOAP";
    public static final String TYPE_REST = "REST";
    public static final String TYPE_FTP = "FTP";
    public static final String TYPE_KAFKA = "KAFKA";
    public static final String METHOD_HTTPPOST = "POST";
    public static final String METHOD_HTTPGET = "GET";
    public static final String METHOD_HTTPDELETE = "DELETE";
    public static final String METHOD_HTTPPUT = "PUT";
    public static final String METHOD_HTTPPATCH = "PATCH";
    public static final String METHOD_KAFKAPRODUCE = "PRODUCE";
    public static final String METHOD_KAFKASEARCH = "SEARCH";
    public static final String RESPONSEHTTPBODYCONTENTTYPE_XML = "XML";
    public static final String RESPONSEHTTPBODYCONTENTTYPE_JSON = "JSON";
    public static final String RESPONSEHTTPBODYCONTENTTYPE_TXT = "TXT";
    public static final String RESPONSEHTTPBODYCONTENTTYPE_UNKNOWN = "UNKNOWN";

    public void addResponseHeaderList(AppServiceHeader object) {
        this.responseHeaderList.add(object);
    }

    public void addContentList(AppServiceContent object) {
        this.contentList.add(object);
    }

    public void addContentList(List <AppServiceContent> object) {
        this.contentList.addAll(object);
    }

    public JSONObject toJSONOnExecution() {
        switch (this.getType()) {
            case AppService.TYPE_FTP:
                return this.toJSONOnFTPExecution();
            case AppService.TYPE_KAFKA:
                return this.toJSONOnKAFKAExecution();
            default:
                return this.toJSONOnDefaultExecution();
        }
    }

    public JSONObject toJSONOnDefaultExecution() {

        JSONObject jsonMain = new JSONObject();
        JSONObject jsonMyRequest = new JSONObject();
        JSONObject jsonMyResponse = new JSONObject();
        try {
            // Request Information.
            if (!(this.getTimeoutms() == 0)) {
                jsonMyRequest.put("HTTP-TimeOutMs", this.getTimeoutms());
            }
            jsonMyRequest.put("CalledURL", this.getServicePath());
            if (!StringUtil.isEmpty(this.getMethod())) {
                jsonMyRequest.put("HTTP-Method", this.getMethod());
            }
            jsonMyRequest.put("ServiceType", this.getType());
            if (!(this.getHeaderList().isEmpty())) {
                JSONObject jsonHeaders = new JSONObject();
                for (AppServiceHeader header : this.getHeaderList()) {
                    jsonHeaders.put(header.getKey(), header.getValue());
                }
                jsonMyRequest.put("HTTP-Header", jsonHeaders);
            }
            if (!(this.getContentList().isEmpty())) {
                JSONObject jsonContent = new JSONObject();
                for (AppServiceContent content : this.getContentList()) {
                    jsonContent.put(content.getKey(), content.getValue());
                }
                jsonMyRequest.put("Content", jsonContent);
            }
            jsonMyRequest.put("HTTP-Request", this.getServiceRequest());

            JSONObject jsonProxy = new JSONObject();
            jsonProxy.put("HTTP-Proxy", this.isProxy());
            if (this.isProxy()) {
                jsonProxy.put("HTTP-ProxyHost", this.getProxyHost());
                if (!(this.getProxyPort() == 0)) {
                    jsonProxy.put("HTTP-ProxyPort", this.getProxyPort());
                }
                jsonProxy.put("HTTP-ProxyAuthentification", this.isProxyWithCredential());
                if (this.isProxyWithCredential()) {
                    jsonProxy.put("HTTP-ProxyUser", this.getProxyUser());
                }
            }
            jsonMyRequest.put("HTTP-Proxy", jsonProxy);
            jsonMyRequest.put("isFollowRedir", this.isFollowRedir());

            jsonMain.put("Request", jsonMyRequest);

            // Response Information.
            jsonMyResponse.put("HTTP-ReturnCode", this.getResponseHTTPCode());
            jsonMyResponse.put("HTTP-Version", this.getResponseHTTPVersion());
            if (!StringUtil.isEmpty(this.getResponseHTTPBody())) {
                try {
                    JSONArray respBody = new JSONArray(this.getResponseHTTPBody());
                    jsonMyResponse.put("HTTP-ResponseBody", respBody);
                } catch (JSONException e1) {
                    try {
                        JSONObject respBody = new JSONObject(this.getResponseHTTPBody());
                        jsonMyResponse.put("HTTP-ResponseBody", respBody);
                    } catch (JSONException e2) {
                        jsonMyResponse.put("HTTP-ResponseBody", this.getResponseHTTPBody());
                    }
                }
            }
            jsonMyResponse.put("HTTP-ResponseContentType", this.getResponseHTTPBodyContentType());
            if (!(this.getResponseHeaderList().isEmpty())) {
                JSONObject jsonHeaders = new JSONObject();
                for (AppServiceHeader header : this.getResponseHeaderList()) {
                    jsonHeaders.put(header.getKey(), header.getValue());
                }
                jsonMyResponse.put("Header", jsonHeaders);
            }
            jsonMain.put("Response", jsonMyResponse);

        } catch (JSONException ex) {
            Logger LOG = LogManager.getLogger(RecorderService.class);
            LOG.warn(ex);
        }
        return jsonMain;
    }

    public JSONObject toJSONOnKAFKAExecution() {
        JSONObject jsonMain = new JSONObject();
        JSONObject jsonMyRequest = new JSONObject();
        JSONObject jsonMyResponse = new JSONObject();
        try {
            // Request Information.
            if (!(this.getTimeoutms() == 0)) {
                jsonMyRequest.put("TimeOutMs", this.getTimeoutms());
            }
            jsonMyRequest.put("Servers", this.getServicePath());
            if (!StringUtil.isEmpty(this.getMethod())) {
                jsonMyRequest.put("KAFKA-Method", this.getMethod());
            }
            jsonMyRequest.put("ServiceType", this.getType());
            if (!(this.getContentList().isEmpty())) {
                JSONObject jsonProps = new JSONObject();
                for (AppServiceContent prop : this.getContentList()) {
                    if (prop.getKey().contains("passw")) {
                        jsonProps.put(prop.getKey(), "XXXXXXXX");
                    } else {
                        jsonProps.put(prop.getKey(), prop.getValue());
                    }
                }
                jsonMyRequest.put("KAFKA-Props", jsonProps);
            }
            if (!(this.getHeaderList().isEmpty())) {
                JSONObject jsonHeaders = new JSONObject();
                for (AppServiceHeader header : this.getHeaderList()) {
                    if (header.getKey().contains("passw")) {
                        jsonHeaders.put(header.getKey(), "XXXXXXXX");
                    } else {
                        jsonHeaders.put(header.getKey(), header.getValue());
                    }
                }
                jsonMyRequest.put("KAFKA-Header", jsonHeaders);
            }
            if (!StringUtil.isEmpty(this.getServiceRequest())) {
                try {
                    JSONObject reqBody = new JSONObject(this.getServiceRequest());
                    jsonMyRequest.put("KAFKA-Request", reqBody);
                } catch (JSONException e) {
                    jsonMyRequest.put("KAFKA-Request", this.getServiceRequest());
                }
            }
            jsonMyRequest.put("KAFKA-Key", this.getKafkaKey());
            if (!(this.getKafkaWaitNbEvent() == 0)) {
                jsonMyRequest.put("WaitNbEvents", this.getKafkaWaitNbEvent());
            }
            if (!(this.getKafkaWaitSecond() == 0)) {
                jsonMyRequest.put("WaitSeconds", this.getKafkaWaitSecond());
            }
            if (METHOD_KAFKASEARCH.equalsIgnoreCase(this.getMethod())) {
                JSONObject jsonFilters = new JSONObject();
                jsonFilters.put("Path", this.getKafkaFilterPath());
                jsonFilters.put("Value", this.getKafkaFilterValue());
                jsonMyRequest.put("KAFKA-SearchFilter", jsonFilters);
            }

            jsonMain.put("Request", jsonMyRequest);

            // Response Information.
            if (this.getKafkaResponseOffset() >= 0) {
                jsonMyResponse.put("Offset", this.getKafkaResponseOffset());
            }
            if (this.getKafkaResponsePartition() >= 0) {
                jsonMyResponse.put("Partition", this.getKafkaResponsePartition());
            }
            if (!StringUtil.isEmpty(this.getResponseHTTPBody())) {
                try {
                    JSONArray respBody = new JSONArray(this.getResponseHTTPBody());
                    jsonMyResponse.put("Messages", respBody);
                } catch (JSONException e) {
                    jsonMyResponse.put("Messages", this.getResponseHTTPBody());
                }
            }
            jsonMain.put("Response", jsonMyResponse);

        } catch (JSONException ex) {
            Logger LOG = LogManager.getLogger(RecorderService.class);
            LOG.warn(ex);
        }
        return jsonMain;
    }

    public JSONObject toJSONOnFTPExecution() {
        JSONObject jsonMain = new JSONObject();
        JSONObject jsonMyRequest = new JSONObject();
        JSONObject jsonMyResponse = new JSONObject();
        try {
            // Request Information.
            if (!(this.getTimeoutms() == 0)) {
                jsonMyRequest.put("FTP-TimeOutMs", this.getTimeoutms());
            }
            jsonMyRequest.put("CalledURL", this.getServicePath());
            if (!StringUtil.isEmpty(this.getMethod())) {
                jsonMyRequest.put("FTP-Method", this.getMethod());
            }
            jsonMyRequest.put("ServiceType", this.getType());
            if (!(this.getContentList().isEmpty())) {
                JSONObject jsonContent = new JSONObject();
                for (AppServiceContent content : this.getContentList()) {
                    jsonContent.put(content.getKey(), content.getValue());
                }
                jsonMyRequest.put("Content", jsonContent);
            }

            JSONObject jsonProxy = new JSONObject();
            jsonProxy.put("FTP-Proxy", this.isProxy());
            if (this.isProxy()) {
                jsonProxy.put("FTP-ProxyHost", this.getProxyHost());
                if (!(this.getProxyPort() == 0)) {
                    jsonProxy.put("FTP-ProxyPort", this.getProxyPort());
                }
                jsonProxy.put("FTP-ProxyAuthentification", this.isProxyWithCredential());
                if (this.isProxyWithCredential()) {
                    jsonProxy.put("FTP-ProxyUser", this.getProxyUser());
                }
            }
            jsonMyRequest.put("FTP-Proxy", jsonProxy);

            jsonMain.put("Request", jsonMyRequest);

            // Response Information.
            jsonMyResponse.put("FTP-ReturnCode", this.getResponseHTTPCode());
            jsonMyResponse.put("FTP-Version", this.getResponseHTTPVersion());
            jsonMyResponse.put("FTP-ResponseBody", this.getResponseHTTPBody());
            jsonMyResponse.put("FTP-ResponseContentType", this.getResponseHTTPBodyContentType());
            if (!(this.getResponseHeaderList().isEmpty())) {
                JSONObject jsonHeaders = new JSONObject();
                for (AppServiceHeader header : this.getResponseHeaderList()) {
                    jsonHeaders.put(header.getKey(), header.getValue());
                }
                jsonMyResponse.put("Header", jsonHeaders);
            }
            jsonMain.put("Response", jsonMyResponse);

        } catch (JSONException ex) {
            Logger LOG = LogManager.getLogger(RecorderService.class);
            LOG.warn(ex);
        }
        return jsonMain;
    }

}
