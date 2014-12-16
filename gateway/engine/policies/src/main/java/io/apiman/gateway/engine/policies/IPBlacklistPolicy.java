/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.apiman.gateway.engine.policies;

import io.apiman.gateway.engine.beans.PolicyFailureType;
import io.apiman.gateway.engine.beans.ServiceRequest;
import io.apiman.gateway.engine.components.IPolicyFailureFactoryComponent;
import io.apiman.gateway.engine.policies.config.IPListConfig;
import io.apiman.gateway.engine.policies.i18n.Messages;
import io.apiman.gateway.engine.policy.IPolicyChain;
import io.apiman.gateway.engine.policy.IPolicyContext;

/**
 * A simple policy that fails the inbound request if its IP address is
 * included in the list of dis-allowed IPs.
 *
 * @author eric.wittmann@redhat.com
 */
public class IPBlacklistPolicy extends AbstractIPListPolicy<IPListConfig> {

    /**
     * Constructor.
     */
    public IPBlacklistPolicy() {
    }

    /**
     * @see io.apiman.gateway.engine.policy.AbstractPolicy#getConfigurationClass()
     */
    @Override
    protected Class<IPListConfig> getConfigurationClass() {
        return IPListConfig.class;
    }

    /**
     * @see io.apiman.gateway.engine.policies.AbstractMappedPolicy#doApply(io.apiman.gateway.engine.beans.ServiceRequest, io.apiman.gateway.engine.policy.IPolicyContext, java.lang.Object, io.apiman.gateway.engine.policy.IPolicyChain)
     */
    @Override
    protected void doApply(ServiceRequest request, IPolicyContext context, IPListConfig config,
            IPolicyChain<ServiceRequest> chain) {
        String remoteAddr = getRemoteAddr(request, config);
        if (isMatch(config, remoteAddr)) {
            IPolicyFailureFactoryComponent ffactory = context.getComponent(IPolicyFailureFactoryComponent.class);
            String msg = Messages.i18n.format("IPBlacklistPolicy.Blacklisted", remoteAddr); //$NON-NLS-1$
            chain.doFailure(ffactory.createFailure(PolicyFailureType.Other, PolicyFailureCodes.IP_BLACKLISTED, msg));
        } else {
            super.doApply(request, context, config, chain);
        }
    }

}
