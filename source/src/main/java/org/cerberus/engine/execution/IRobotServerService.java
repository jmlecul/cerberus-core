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
package org.cerberus.engine.execution;

import java.util.HashMap;
import org.cerberus.engine.entity.Session;
import org.cerberus.crud.entity.TestCaseExecution;
import org.cerberus.exception.CerberusException;
import org.json.JSONArray;
import org.openqa.selenium.Capabilities;

/**
 *
 * @author bcivel
 */
public interface IRobotServerService {

    /**
     * Start the selenium Server
     *
     * @param tCExecution (with Session object and capabilities)
     * @throws CerberusException
     */
    void startServer(TestCaseExecution tCExecution) throws CerberusException;

    /**
     * Stop the server if started
     *
     * @param tCExecution
     * @return true if server successfully closed
     */
    boolean stopServer(TestCaseExecution tCExecution);

    /**
     * Find the capabilities used by the server
     *
     * @param session
     * @return the capabilities of the server
     */
    Capabilities getUsedCapabilities(Session session);

    /**
     *
     * @param options
     * @return
     */
    public HashMap<String, String> getMapFromOptions(JSONArray options);

    /**
     *
     * @param session
     * @param timeout
     */
    public void setOptionsTimeout(Session session, Integer timeout);

    /**
     *
     * @param session
     * @param highlightElement
     */
    public void setOptionsHighlightElement(Session session, Integer highlightElement);

    /**
     *
     * @param session
     * @param minSimilarity
     */
    public void setOptionsMinSimilarity(Session session, String minSimilarity);

    /**
     *
     * @param session
     * @param typeDelay
     */
    public void setOptionsTypeDelay(Session session, String typeDelay);

    /**
     *
     * @param session
     */
    public void setOptionsToDefault(Session session);

}
