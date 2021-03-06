/*
 *
 *  * Copyright 2020 New Relic Corporation. All rights reserved.
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.newrelic.agent.instrumentation.weaver;

import com.newrelic.agent.Agent;
import com.newrelic.agent.logging.IAgentLogger;
import com.newrelic.weave.violation.WeaveViolation;
import com.newrelic.weave.weavepackage.PackageValidationResult;

import java.util.List;
import java.util.logging.Level;

public class WeaveViolationLogger {
    private final IAgentLogger logger;

    public WeaveViolationLogger(IAgentLogger logger) {
        this.logger = logger;
    }

    public void logWeaveViolations(PackageValidationResult packageResult, ClassLoader classloader, boolean isCustom) {
        logger.log(Level.FINEST, "Skipping instrumentation module {0}. "
                + "The most likely cause is that {0} shouldn''t apply to this application.", packageResult.getWeavePackage().getName());

        if (!isCustom && !Agent.isDebugEnabled()) {
            return;
        }

        Level actualLevel = isCustom ? Level.INFO : Level.FINEST;

        List<WeaveViolation> violations = packageResult.getViolations();
        logger.log(actualLevel, "{0} - {1} violations against classloader {2}",
                packageResult.getWeavePackage().getName(), violations.size(), classloader);
        for (WeaveViolation violation : violations) {
            logger.log(actualLevel, "WeaveViolation: {0}", violation.getType().name());
            logger.log(actualLevel, "\t\tClass: {0}", violation.getClazz());
            if (violation.getMethod() != null) {
                logger.log(actualLevel, "\t\tMethod: {0}", violation.getMethod());
            }
            if (violation.getField() != null) {
                logger.log(actualLevel, "\t\tField: {0}", violation.getField());
            }
            logger.log(actualLevel, "\t\tReason: {0}", violation.getType().getMessage());
        }

    }
}
