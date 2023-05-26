package no.nav.modig.security.tilgangskontroll.policy.pdp.picketlink.cache;

/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors. 
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

import no.nav.modig.security.tilgangskontroll.policy.pip.cache.Cache;
import org.jboss.security.xacml.locators.cache.DecisionCacheLocator;
import org.jboss.security.xacml.sunxacml.ctx.RequestCtx;
import org.jboss.security.xacml.sunxacml.ctx.ResponseCtx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.security.xacml.util.JBossXACMLUtil.getTokenList;

public class PicketLinkDecisionCache extends DecisionCacheLocator {

    @Autowired
    private Cache<RequestCtx, ResponseCtx> decisionCache;

    private static final String DEFAULT_PDP_CACHE_NAME = "pdp.decision";

    private static final String IGNORE_SUBJECT_ID = "ignoreSubjectID";
    private static final String IGNORE_RESOURCE_ID = "ignoreResourceID";
    private static final String IGNORE_ACTION_ID = "ignoreActionID";
    private static final String IGNORE_ENVIRONMENT_ID = "ignoreEnvironmentID";

    private static final String DECISION_CACHE_NAME = "decisionCacheName";

    private String pdpCacheName;

    /**
     * Add a {@code RequestCtx} and a {@code ResponseCtx} to the cache
     * 
     * @param request
     * @param response
     */
    public void add(RequestCtx request, ResponseCtx response) {
        RequestCtx cacheRequest = preprocessRequest(request);
        getDecisionCacheInstance().put(cacheRequest, response,
                getPDPCacheName());
    }

    /**
     * Get a {@code ResponseCtx} response that we have cached for a {@code RequestCtx} request.
     * 
     * @return response object if cached else null
     */
    public ResponseCtx get(RequestCtx request) {
        RequestCtx cacheRequest = preprocessRequest(request);
        return getDecisionCacheInstance().get(cacheRequest, getPDPCacheName());
    }

    private RequestCtx preprocessRequest(RequestCtx request) {
        List<String> subjectID = new ArrayList<>();
        List<String> resourceID = new ArrayList<>();
        List<String> actionID = new ArrayList<>();
        List<String> envID = new ArrayList<>();

        String ignoreSubjectOption = (String) optionMap.get(IGNORE_SUBJECT_ID);
        String ignoreResourceOption = (String) optionMap
                .get(IGNORE_RESOURCE_ID);
        String ignoreActionOption = (String) optionMap.get(IGNORE_ACTION_ID);
        String ignoreEnvOption = (String) optionMap.get(IGNORE_ENVIRONMENT_ID);

        subjectID.addAll(getTokenList(ignoreSubjectOption));
        resourceID.addAll(getTokenList(ignoreResourceOption));
        actionID.addAll(getTokenList(ignoreActionOption));
        envID.addAll(getTokenList(ignoreEnvOption));

        return DecisionCacheLocatorRequest.from(request, subjectID, resourceID,
                actionID, envID);
    }

    private Cache<RequestCtx, ResponseCtx> getDecisionCacheInstance() {
        if (decisionCache != null) {
            return decisionCache;
        }

        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        return decisionCache;
    }

    public String getPDPCacheName() {
        if (pdpCacheName != null && !pdpCacheName.isEmpty()) {
            return pdpCacheName;
        }
        String cacheName = (String) optionMap.get(DECISION_CACHE_NAME);
        pdpCacheName = (cacheName == null || cacheName.isEmpty()) ? DEFAULT_PDP_CACHE_NAME
                : cacheName;

        return pdpCacheName;
    }
}